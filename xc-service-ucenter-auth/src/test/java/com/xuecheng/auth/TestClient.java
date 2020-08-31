package com.xuecheng.auth;

import com.xuecheng.framework.client.XcServiceList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestClient {

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void testClient(){
        //采用客户端负载均衡，从Eureka获取认证服务的ip和端口
        ServiceInstance serviceInstance = loadBalancerClient.choose(XcServiceList.XC_SERVICE_UCENTER_AUTH);
        URI uri = serviceInstance.getUri();
        String authUrl = uri + "/auth/oauth/token";

        //URI url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType
        //  url就是 申请令牌的url /oauth/token         
        // method http的方法类型         
        // requestEntity请求内容         
        // responseType，将响应的结果生成的类型

        //请求的内容分两部分
        //1 header信息，包含http basic 认证信息
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        String httpbasic = httpBasic("XcWebApp","XcWebApp");
        headers.add("Authorization",httpbasic);
        //2.包括grant_type,username.password
        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type","password");
        body.add("username","itcast");
        body.add("password","111111");
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body, headers);
        //指定restTemplate遇到400或者401响应时，不要抛出异常，正常返回值
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                if(response.getRawStatusCode() != 400 && response.getRawStatusCode() != 401){
                    super.handleError(response);
                }
            }
        });
        //远程调用令牌
        ResponseEntity<Map> exchange = restTemplate.exchange(authUrl, HttpMethod.POST, httpEntity, Map.class);
        Map map = exchange.getBody();
        System.out.println(map);


    }


    //将客户端id和客户端密码进行封装编码成base64
    public String httpBasic(String clientId,String clientSecret){
        //将客户端id和客户端密码进行拼接
        String string = clientId+":"+clientSecret;
        //进行base64编码
        byte[] encode = Base64Utils.encode(string.getBytes());
        return "Basic "+new String(encode);

    }


    @Test
    public void testPasswrodEncoder(){
        //原始密码
        String password = "111111";
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        //使用BCrypt加密，每次加密使用一个随机盐
        for(int i=0;i<10;i++){
            String encode = bCryptPasswordEncoder.encode(password);
            System.out.println(encode);
            //校验
            boolean matches = bCryptPasswordEncoder.matches(password, encode);
            System.out.println(matches);
        }

    }

}
