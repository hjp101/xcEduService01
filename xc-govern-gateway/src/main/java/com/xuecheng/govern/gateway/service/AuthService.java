package com.xuecheng.govern.gateway.service;

import com.netflix.client.http.HttpRequest;
import com.xuecheng.framework.utils.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Service
public class AuthService {
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    //查询身份令牌
    public String getTokenFromCookie(HttpServletRequest request){
        Map<String, String> map = CookieUtil.readCookie(request, "uid");
        String uid = map.get("uid");
        if(StringUtils.isEmpty(uid)){
            return null;
        }
        return uid;
    }

    //从header查询jwt令牌
    public String getJwtFromHeader(HttpServletRequest request){
        String authorization = request.getHeader("Authorization");
        if(StringUtils.isEmpty(authorization)){
            //拒绝访问
            return null;
        }
        if(!authorization.startsWith("Bear ")){
            //拒绝访问
            return null;
        }
        return authorization;
    }

    //查询令牌有效期
    public long getExpire(String token){
        //token在redis中的key
        String key = "user_token"+token;
        Long expire = stringRedisTemplate.getExpire(key);
        return expire;

    }
}
