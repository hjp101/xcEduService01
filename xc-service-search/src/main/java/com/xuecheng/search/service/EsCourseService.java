package com.xuecheng.search.service;

import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EsCourseService {
    @Value("${xuecheng.course.index}")
    private String index;
    @Value("${xuecheng.media.index}")
    private String media_index;
    @Value("${xuecheng.course.type}")
    private String type;
    @Value("${xuecheng.media.type}")
    private String media_type;
    @Value("${xuecheng.course.source_field}")
    private String source_field;
    @Value("${xuecheng.media.source_field}")
    private String media_source_field;

    @Autowired
    RestHighLevelClient client;

    /**
     * 课程搜素
     * @param page
     * @param size
     * @param courseSearchParam
     * @return
     */
    public QueryResponseResult<CoursePub> list(int page, int size, CourseSearchParam courseSearchParam) {
        //设置索引
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        //source字段过滤
        String[] strings = source_field.split(",");
        searchSourceBuilder.fetchSource(strings,new String[]{});
        //关键字
        if(StringUtils.isNotEmpty(courseSearchParam.getKeyword())){
            //匹配关键字
            MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(courseSearchParam.getKeyword(),
                    "name","teachplan","description");
            //设置匹配占比
            multiMatchQueryBuilder.minimumShouldMatch("70%");
            //提升name字段的得分
            multiMatchQueryBuilder.field("name",10);
            boolQueryBuilder.must(multiMatchQueryBuilder);
        }
        //过滤,根据分类和难易等级
        if(StringUtils.isNotEmpty(courseSearchParam.getMt())){
            boolQueryBuilder.filter(QueryBuilders.termQuery("mt",courseSearchParam.getMt()));
        }
        if(StringUtils.isNotEmpty(courseSearchParam.getSt())){
            boolQueryBuilder.filter(QueryBuilders.termQuery("st",courseSearchParam.getSt()));
        }
        if(StringUtils.isNotEmpty(courseSearchParam.getGrade())){
            boolQueryBuilder.filter(QueryBuilders.termQuery("grade",courseSearchParam.getGrade()));
        }
        //分页
        if(page <= 0){
            page = 1;
        }
        if(size <= 0){
            size = 10;
        }
        int start = (page -1)*size;
        searchSourceBuilder.from(start);
        searchSourceBuilder.size(size);
        //布尔查询
        searchSourceBuilder.query(boolQueryBuilder);
        //高亮设置
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<font class = ''eslight''>");
        highlightBuilder.postTags("</font>");
        //设置高亮字段
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
        searchSourceBuilder.highlighter(highlightBuilder);
        //请求搜素
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = null;
        try {
             response = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return new QueryResponseResult<>(CommonCode.FAIL,new QueryResult<CoursePub>());
        }
        //结果集处理
        SearchHits hits = response.getHits();
        SearchHit[] searchHits = hits.getHits();
        //记录总数
        long value = hits.getTotalHits().value;
        //数据列表
        List<CoursePub> list = new ArrayList<>();
        for (SearchHit hit : searchHits) {
            CoursePub coursePub = new CoursePub();
            //取出source
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            //取出名称
            String name = (String) sourceAsMap.get("name");
            //取出高亮字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if(highlightFields != null){
                HighlightField highNames = highlightFields.get("name");
                if(highNames != null){
                    Text[] highNamesTxt = highNames.getFragments();
                    StringBuffer stringBuffer = new StringBuffer();
                    for (Text text : highNamesTxt) {
                        stringBuffer.append(text);
                    }
                    name = stringBuffer.toString();
                }
            }
            String pic = (String) sourceAsMap.get("pic");
            Float price = (Float) sourceAsMap.get("price");
            coursePub.setName(name);
            coursePub.setPic(pic);
            coursePub.setPrice(price);
            list.add(coursePub);
        }

        QueryResult<CoursePub> queryResult = new QueryResult();
        queryResult.setList(list);
        queryResult.setTotal(value);
        QueryResponseResult<CoursePub> queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS,queryResult);
        return queryResponseResult;
    }

    public QueryResponseResult<TeachplanMediaPub> getmedia(String[] teachplanIds) {
        //设置搜索对象
        SearchRequest searchRequest = new SearchRequest(media_index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //源字段过滤
        String[] split = media_source_field.split(",");
        searchSourceBuilder.fetchSource(split,new String[]{});
        //查询条件
        searchSourceBuilder.query(QueryBuilders.termsQuery("teachplan",teachplanIds));
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = null;
        //使用es客户端进行搜索请求Es
        List<TeachplanMediaPub> teachplanMediaPubList = new ArrayList<>();
        long totalHits = 0;
        try {
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            //获取搜索结果
            SearchHits hits = searchResponse.getHits();
            SearchHit[] resultHits = hits.getHits();
             totalHits = hits.getTotalHits().value;
            for(SearchHit hit:resultHits){
                TeachplanMediaPub teachplanMediaPub= new TeachplanMediaPub();
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                //取出课程计划媒资信息
                String courseid = (String) sourceAsMap.get("courseid");
                String media_id = (String) sourceAsMap.get("media_id");
                String media_url = (String) sourceAsMap.get("media_url");
                String teachplan_id = (String) sourceAsMap.get("teachplan_id");
                String media_fileoriginalname = (String) sourceAsMap.get("media_fileoriginalname");

                teachplanMediaPub.setCourseId(courseid);
                teachplanMediaPub.setMediaUrl(media_url);
                teachplanMediaPub.setMediaFileOriginalName(media_fileoriginalname);
                teachplanMediaPub.setMediaId(media_id);
                teachplanMediaPub.setTeachplanId(teachplan_id);
                teachplanMediaPubList.add(teachplanMediaPub);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //数据集合
        QueryResult<TeachplanMediaPub> queryResult = new QueryResult<>();
        queryResult.setList(teachplanMediaPubList);
        queryResult.setTotal(totalHits);
        QueryResponseResult<TeachplanMediaPub> queryResponseResult = new QueryResponseResult<>(CommonCode.SUCCESS,queryResult);
        return queryResponseResult;

    }

    /**
     * 根据课程id查询
     * @param id
     * @return
     */
    public Map<String, CoursePub> getall(String id) {
        //设置索引库
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //查询条件,根据课程id查询
        searchSourceBuilder.query(QueryBuilders.termsQuery("id",id));
        //不过滤原字段
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = null;
        //执行搜索
        Map<String,CoursePub> map = new HashMap<>();
        try {
             searchResponse = client.search(searchRequest,RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            //日志
        }
        //获取搜索结果
        SearchHits hits = searchResponse.getHits();
        SearchHit[] resultHits = hits.getHits();
        for (SearchHit resultHit : resultHits) {
            CoursePub coursePub = new CoursePub();
            //获取源文档的内容
            Map<String, Object> sourceAsMap = resultHit.getSourceAsMap();
            //课程id
            String courseId = (String) sourceAsMap.get("id");
            String name = (String) sourceAsMap.get("name");
            String grade = (String) sourceAsMap.get("grade");
            String charge = (String) sourceAsMap.get("charge");
            String pic = (String) sourceAsMap.get("pic");
            String description = (String) sourceAsMap.get("description");
            String teachplan = (String) sourceAsMap.get("teachplan");
            coursePub.setId(courseId);
            coursePub.setName(name);
            coursePub.setPic(pic);
            coursePub.setGrade(grade);
            coursePub.setTeachplan(teachplan);
            coursePub.setDescription(description);
            map.put(courseId,coursePub);
        }
        return map;
    }
}
