package com.xuecheng.search.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author Administrator
 * @version 1.0
 **/
@Configuration
public class ElasticsearchConfig {

//    @Value("${xuecheng.elasticsearch.hostlist}")
//    private String hostlist;
//
//    @Bean
//    public RestHighLevelClient restHighLevelClient(){
//        //解析hostlist配置信息
//        String[] split = hostlist.split(",");
//        //创建HttpHost数组，其中存放es主机和端口的配置信息
//        HttpHost[] httpHostArray = new HttpHost[split.length];
//        for(int i=0;i<split.length;i++){
//            String item = split[i];
//            httpHostArray[i] = new HttpHost(item.split(":")[0], Integer.parseInt(item.split(":")[1]), "http");
//        }
//        //创建RestHighLevelClient客户端
//        return new RestHighLevelClient(RestClient.builder(httpHostArray));
//    }
//
//    //项目主要使用RestHighLevelClient，对于低级的客户端暂时不用
//    @Bean
//    public RestClient restClient(){
//        //解析hostlist配置信息
//        String[] split = hostlist.split(",");
//        //创建HttpHost数组，其中存放es主机和端口的配置信息
//        HttpHost[] httpHostArray = new HttpHost[split.length];
//        for(int i=0;i<split.length;i++){
//            String item = split[i];
//            httpHostArray[i] = new HttpHost(item.split(":")[0], Integer.parseInt(item.split(":")[1]), "http");
//        }
//        return RestClient.builder(httpHostArray).build();
//    }

    private static final int ADDRESS_LENGTH = 2;
    private static final String HTTP_SCHEME = "http";
    //权限验证
    final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

    /**
     * 使用冒号隔开ip和端口
     */
    @Value("${elasticsearch.address}")
    private String[] address;

//    @Value("${elasticsearch.username}")
//    private String username;

//    @Value("${elasticsearch.password}")
//    private String password;


    @Bean
    public RestClientBuilder restClientBuilder() {
        HttpHost[] hosts = Arrays.stream(address)
                .map(this::makeHttpHost)
                .filter(Objects::nonNull)
                .toArray(HttpHost[]::new);
//        log.debug("hosts:{}", Arrays.toString(hosts));
        //配置权限验证
//        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
        RestClientBuilder restClientBuilder = RestClient.builder(hosts).setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
            @Override
            public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            }
        });
        return restClientBuilder;
    }


    @Bean(name = "highLevelClient")
    public RestHighLevelClient highLevelClient(@Autowired RestClientBuilder restClientBuilder) {
//        restClientBuilder.setMaxRetryTimeoutMillis(60000);
        return new RestHighLevelClient(restClientBuilder);
    }

    /**
     * 处理请求地址
     * @param s
     * @return HttpHost
     */
    private HttpHost makeHttpHost(String s) {
        assert StringUtils.isNotEmpty(s);
        String[] address = s.split(":");
        if (address.length == ADDRESS_LENGTH) {
            String ip = address[0];
            int port = Integer.parseInt(address[1]);
            return new HttpHost(ip, port, HTTP_SCHEME);
        } else {
            return null;
        }
    }

}
