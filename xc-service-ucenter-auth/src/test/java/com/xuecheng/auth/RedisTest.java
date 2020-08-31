package com.xuecheng.auth;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisTest {
    @Autowired
    private StringRedisTemplate redisTemplate;

    //测试redis
    @Test
    public void testRedis(){
        //定义key
        String key = "user_token:9734b68f‐cf5e‐456f‐9bd6‐df578c711390";
        //定义Map
        Map<String,String> map = new HashMap<>();
        map.put("id","1");
        map.put("username","hjp");
        //将Map转成JSON字符串
        String jsonString = JSON.toJSONString(map);
        redisTemplate.boundValueOps(key).set(jsonString,60, TimeUnit.SECONDS);
        //读取过期时间
        Long expire = redisTemplate.getExpire(key);
        //根据key获取value
        String result = redisTemplate.opsForValue().get(key);
        System.out.println(result);
        System.out.println(expire);
    }
}
