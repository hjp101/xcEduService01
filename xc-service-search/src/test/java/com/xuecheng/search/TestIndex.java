package com.xuecheng.search;

import javafx.scene.control.IndexRange;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import org.elasticsearch.client.RestClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.naming.directory.SearchResult;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @author Administrator
 * @version 1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestIndex {

    @Autowired
    private RestHighLevelClient restHighLevelClient;


//    @Autowired
//    private RestClient restClient;
   //创建索引ku
//    @Test
//    public void testCreateIndex() throws IOException {
//        //创建索引对象
//        CreateIndexRequest createIndexRequest = new CreateIndexRequest("xc_course");
//        //设置参数
//        createIndexRequest.settings(Settings.builder().put("number_of_shards","1").put("number_of_replicas","0"));
//        //指定映射
////        createIndexRequest.mapping("doc"," {\n" +
////                " \t\"properties\": {\n" +
////                "            \"studymodel\":{\n" +
////                "             \"type\":\"keyword\"\n" +
////                "           },\n" +
////                "            \"name\":{\n" +
////                "             \"type\":\"keyword\"\n" +
////                "           },\n" +
////                "           \"description\": {\n" +
////                "              \"type\": \"text\",\n" +
////                "              \"analyzer\":\"ik_max_word\",\n" +
////                "              \"search_analyzer\":\"ik_smart\"\n" +
////                "           },\n" +
////                "           \"pic\":{\n" +
////                "             \"type\":\"text\",\n" +
////                "             \"index\":false\n" +
////                "           }\n" +
////                " \t}\n" +
////                "}", XContentType.JSON);
//        XContentBuilder builder = XContentFactory.jsonBuilder()
//                .startObject()
//                .field("properties")
//                .startObject()
//                .field("name").startObject().field("index", "true").field("type", "keyword").endObject()
//                .field("age").startObject().field("index", "true").field("type", "integer").endObject()
//                .field("money").startObject().field("index", "true").field("type", "double").endObject()
//                .field("address").startObject().field("index", "true").field("type", "text").field("analyzer", "ik_max_word").endObject()
//                .field("birthday").startObject().field("index", "true").field("type", "date").field("format", "strict_date_optional_time||epoch_millis").endObject()
//                .endObject()
//                .endObject();
////        CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
//        createIndexRequest.mapping("_doc",builder);
////        CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
////        boolean acknowledged = createIndexResponse.isAcknowledged();
//        //操作索引的客户端
//        IndicesClient indices = restHighLevelClient.indices();
//        //执行创建索引库
//        CreateIndexResponse response = indices.create(createIndexRequest);
//        //得到响应
//        boolean acknowledged = response.isAcknowledged();
//        System.out.println(acknowledged);
//
//    }



    @Test
    public void testCreateIndex2() throws IOException {

        XContentBuilder builder = XContentFactory.jsonBuilder()
                .startObject()
                .field("properties")
                .startObject()
                .field("name").startObject().field("index", "true").field("type", "keyword").endObject()
                .field("studymodel").startObject().field("index", "true").field("type", "keyword").endObject()
                .field("pic").startObject().field("index", "false").field("type", "text").endObject()
                .field("description").startObject().field("index", "true").field("type", "text").field("analyzer", "ik_smart").endObject()
                .endObject()
                .endObject();
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("xc_course");
        createIndexRequest.mapping(builder);
        CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        boolean acknowledged = createIndexResponse.isAcknowledged();
        System.out.println(acknowledged);
    }

    /**
     * 删除索引
     * @throws IOException
     */
    @Test
    public void deleteIndex() throws IOException {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("xc_course");
        //操作索引的客户端
        IndicesClient indices = restHighLevelClient.indices();
        //执行删除索引
        AcknowledgedResponse response = indices.delete(deleteIndexRequest,RequestOptions.DEFAULT);
        System.out.println(response.isAcknowledged());
    }

    /**
     * 添加文档
     * @throws IOException
     */
    @Test
    public void addDoc() throws IOException {
        //文档内容
        //准备文档
        Map<String,Object> jsonMap = new HashMap<>();
        jsonMap.put("name", "spring cloud实战");
        jsonMap.put("description", "本课程主要从四个章节进行讲解： 1.微服务架构入门 2.spring cloud 基础入门 3.实战Spring Boot 4.注册中心eureka。");
        jsonMap.put("studymodel", "201001");
        SimpleDateFormat dateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        jsonMap.put("timestamp", dateFormat.format(new Date()));
        jsonMap.put("price", 5.6f);
        //创建索引创建对象
        IndexRequest indexRequest = new IndexRequest("xc_course");
        //文档内容
        indexRequest.source(jsonMap);
        //通过client发送
        IndexResponse index =restHighLevelClient.index(indexRequest,RequestOptions.DEFAULT);
        System.out.println(index.getResult());
    }

    /**
     * 查询文档
     */
    @Test
    public void queryDoc() throws IOException {
        //查询请求对象
        GetRequest getRequest = new GetRequest("xc_course","_doc","Rzjm3XMBFl-TK0IyY-vk");
        GetResponse response = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        Map<String, Object> ma = response.getSourceAsMap();
        System.out.println(ma);

    }


    /**
     * 搜索Type下的全部记录
     */
    @Test
    public void testSearchAll() throws IOException {
        SearchRequest searchRequest = new SearchRequest("xc_course");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        //source字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel"},new String[]{});
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit : searchHits) {
            String index = searchHit.getIndex();
            String id = searchHit.getId();
            Map<String, DocumentField> fields = searchHit.getFields();
            Map<String, Object> map = searchHit.getSourceAsMap();
            String name = (String) map.get("name");
            String studymodel = (String) map.get("studymodel");
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(index);
        }

    }


    /**
     * 搜索Type下的全部记录(分页)
     */
    @Test
    public void testSearchAllByPage() throws IOException {
        SearchRequest searchRequest = new SearchRequest("xc_course");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        //分页查询。设置起始下标，从0开始
        searchSourceBuilder.from(0);
        //每页显示个数
        searchSourceBuilder.size(1);
        //source字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel"},new String[]{});
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit : searchHits) {
            String index = searchHit.getIndex();
            String id = searchHit.getId();
            Map<String, DocumentField> fields = searchHit.getFields();
            Map<String, Object> map = searchHit.getSourceAsMap();
            String name = (String) map.get("name");
            String studymodel = (String) map.get("studymodel");
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(index);
        }
    }

    /**
     * TermQuery 精确查询
     */
    @Test
    public void testTermQuery() throws IOException {
        SearchRequest searchRequest = new SearchRequest("xc_course");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("name","spring"));
        //source字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel"},new String[]{});
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit : searchHits) {
            String index = searchHit.getIndex();
            String id = searchHit.getId();
            Map<String, DocumentField> fields = searchHit.getFields();
            Map<String, Object> map = searchHit.getSourceAsMap();
            String name = (String) map.get("name");
            String studymodel = (String) map.get("studymodel");
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(index);
            System.out.println(hits.getTotalHits().value);

        }
    }

    /**
     * 根据文档id查询
     */
    @Test
    public void testQueryById() throws IOException {
        SearchRequest searchRequest = new SearchRequest("xc_course");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        String[] idList = new String[]{"1","3"};
        List<String> asList = Arrays.asList(idList);

        searchSourceBuilder.query(QueryBuilders.termsQuery("_id",asList));
        //source字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel"},new String[]{});
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit : searchHits) {
            String index = searchHit.getIndex();
            String id = searchHit.getId();
            Map<String, DocumentField> fields = searchHit.getFields();
            Map<String, Object> map = searchHit.getSourceAsMap();
            String name = (String) map.get("name");
            String studymodel = (String) map.get("studymodel");
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(index);
            System.out.println(hits.getTotalHits().value);

        }
    }

    /**
     * 根据关键字搜索
     */
    @Test
    public void testMatchQuery() throws IOException {
        SearchRequest searchRequest = new SearchRequest("xc_course");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //source字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel"},new String[]{});
        //匹配关键字
        searchSourceBuilder.query(QueryBuilders.matchQuery("description","spring开发").operator(Operator.OR));
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit : searchHits) {
            String index = searchHit.getIndex();
            String id = searchHit.getId();
            Map<String, DocumentField> fields = searchHit.getFields();
            Map<String, Object> map = searchHit.getSourceAsMap();
            String name = (String) map.get("name");
            String studymodel = (String) map.get("studymodel");
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(index);
            System.out.println(hits.getTotalHits().value);

        }
    }

    /**
     * 根据关键字搜索(MiniMum)
     */
    @Test
    public void testMatchQueryMiniMum() throws IOException {
        SearchRequest searchRequest = new SearchRequest("xc_course");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //source字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel"},new String[]{});
        //匹配关键字
        searchSourceBuilder.query(QueryBuilders.matchQuery("description","spring开发框架").minimumShouldMatch("80%"));
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit : searchHits) {
            String index = searchHit.getIndex();
            String id = searchHit.getId();
            Map<String, DocumentField> fields = searchHit.getFields();
            Map<String, Object> map = searchHit.getSourceAsMap();
            String name = (String) map.get("name");
            String studymodel = (String) map.get("studymodel");
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(index);
            System.out.println(hits.getTotalHits().value);

        }
    }

    /**
     * 根据关键字搜索(MultiMatch)
     */
    @Test
    public void testMultiMatch() throws IOException {
        SearchRequest searchRequest = new SearchRequest("xc_course");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //source字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel"},new String[]{});
        //匹配关键字
        searchSourceBuilder.query(QueryBuilders.multiMatchQuery("spring框架","name","description").minimumShouldMatch("50%").field("name",10));

        searchRequest.source(searchSourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit : searchHits) {
            String index = searchHit.getIndex();
            String id = searchHit.getId();
            Map<String, DocumentField> fields = searchHit.getFields();
            Map<String, Object> map = searchHit.getSourceAsMap();
            String name = (String) map.get("name");
            String studymodel = (String) map.get("studymodel");
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(index);
            System.out.println(hits.getTotalHits().value);

        }
    }


    /**
     * BoolQuery,将搜素关键词进行分词，拿分词去索引库索引
     */
    @Test
    public void testBoolQuery() throws IOException {
        //创建搜素请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //创建搜素源配置对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //source字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel"},new String[]{});
        //multiQuery
        String keyword = "spring开发框架";
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(keyword,"name","description")
                .minimumShouldMatch("50%").field("name",10);
        //TermQuery
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("studymodel","201001");
        //布尔查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(multiMatchQueryBuilder);
        boolQueryBuilder.must(termQueryBuilder);
        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit : searchHits) {
            String index = searchHit.getIndex();
            String id = searchHit.getId();
            Map<String, DocumentField> fields = searchHit.getFields();
            Map<String, Object> map = searchHit.getSourceAsMap();
            String name = (String) map.get("name");
            String studymodel = (String) map.get("studymodel");
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(index);
            System.out.println(hits.getTotalHits().value);

        }
    }

    /**
     * 布尔查询过滤器
     */
    @Test
    public void testFilter() throws IOException {
        //创建搜素请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //创建搜素源配置对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //source字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel"},new String[]{});
        //multiQuery
        String keyword = "spring开发框架";
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(keyword,"name","description")
                .minimumShouldMatch("50%").field("name",10);
//        //TermQuery
//        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("studymodel","201001");
        //布尔查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(multiMatchQueryBuilder);
//        boolQueryBuilder.must(termQueryBuilder);
        boolQueryBuilder.filter(QueryBuilders.termQuery("studymodel","201001"));
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(60).lte(100));
        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit : searchHits) {
            String index = searchHit.getIndex();
            String id = searchHit.getId();
            Map<String, DocumentField> fields = searchHit.getFields();
            Map<String, Object> map = searchHit.getSourceAsMap();
            String name = (String) map.get("name");
            String studymodel = (String) map.get("studymodel");
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(index);
            System.out.println(hits.getTotalHits().value);

        }
    }

    /**
     * 排序
     */
    @Test
    public void testSort() throws IOException {
        //创建搜素请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //创建搜素源配置对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //source字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel"},new String[]{});
        //multiQuery
        String keyword = "spring开发框架";
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(keyword,"name","description")
                .minimumShouldMatch("50%").field("name",10);
//        //TermQuery
//        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("studymodel","201001");
        //布尔查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(multiMatchQueryBuilder);
//        boolQueryBuilder.must(termQueryBuilder);
        boolQueryBuilder.filter(QueryBuilders.termQuery("studymodel","201001"));
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(60).lte(100));
        searchSourceBuilder.query(boolQueryBuilder);
        //排序
        searchSourceBuilder.sort(new FieldSortBuilder("studymodel").order(SortOrder.DESC));
        searchSourceBuilder.sort(new FieldSortBuilder("price").order(SortOrder.ASC));
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit : searchHits) {
            String index = searchHit.getIndex();
            String id = searchHit.getId();
            Map<String, DocumentField> fields = searchHit.getFields();
            Map<String, Object> map = searchHit.getSourceAsMap();
            String name = (String) map.get("name");
            String studymodel = (String) map.get("studymodel");
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(index);
            System.out.println(hits.getTotalHits().value);

        }
    }

    /**
     * 高亮
     */
    @Test
    public void testHighlight() throws IOException {
        //创建搜素请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //创建搜素源配置对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //source字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel"},new String[]{});
        //multiQuery
        String keyword = "spring开发框架";
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(keyword,"name","description")
                .minimumShouldMatch("50%").field("name",10);
//        //TermQuery
//        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("studymodel","201001");
        //布尔查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(multiMatchQueryBuilder);
//        boolQueryBuilder.must(termQueryBuilder);
        boolQueryBuilder.filter(QueryBuilders.termQuery("studymodel","201001"));
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(60).lte(100));
        searchSourceBuilder.query(boolQueryBuilder);
        //排序
        searchSourceBuilder.sort(new FieldSortBuilder("studymodel").order(SortOrder.DESC));
        searchSourceBuilder.sort(new FieldSortBuilder("price").order(SortOrder.ASC));
        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<tag>");
        highlightBuilder.postTags("</tag>");
        //设置高亮字段
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
        searchSourceBuilder.highlighter(highlightBuilder);
        searchRequest.source(searchSourceBuilder);

        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit : searchHits) {
            String index = searchHit.getIndex();
            String id = searchHit.getId();
            Map<String, DocumentField> fields = searchHit.getFields();
            Map<String, Object> map = searchHit.getSourceAsMap();
            String name = (String) map.get("name");
            String studymodel = (String) map.get("studymodel");
            //取出高亮字段
            Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
            if(highlightFields != null){
                HighlightField highlightField = highlightFields.get("name");
                if(highlightField != null){
                    Text[] texts = highlightField.getFragments();
                    StringBuffer stringBuffer = new StringBuffer();
                    for (Text text : texts) {
                        stringBuffer.append(text);
                    }
                    name = stringBuffer.toString();
                }
            }
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(index);
            System.out.println(hits.getTotalHits().value);
            System.out.println();

        }
    }





}
