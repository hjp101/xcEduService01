package com.xuecheng.govern.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.govern.gateway.service.AuthService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginFilter extends ZuulFilter {

    @Autowired
    private AuthService authService;
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        //得到request
        HttpServletRequest request = requestContext.getRequest();
        //查询身份令牌
        String tokenFromCookie = authService.getTokenFromCookie(request);
        if(tokenFromCookie == null){
            //拒绝访问
            access_denied();
        }
        //从header中取jwt
        String jwtFromHeader = authService.getJwtFromHeader(request);
        if(StringUtils.isEmpty(jwtFromHeader)){
            //拒绝访问
            access_denied();
            return null;
        }
        //从redis取出jwt的过期时间
        long expire = authService.getExpire(tokenFromCookie);
        if(expire<0){
            //拒绝访问
            access_denied();
            return null;
        }


        return null;
    }

    //拒绝访问
    public void access_denied(){
        //上下文的对象
        RequestContext requestContext = RequestContext.getCurrentContext();
        //设置响应代码
        requestContext.setResponseStatusCode(200);
        //拒绝访问
        requestContext.setSendZuulResponse(false);
        //构建响应的信息
        ResponseResult responseResult = new ResponseResult(CommonCode.UNAUTHENTICATED);
        //转成json
        String string = JSON.toJSONString(responseResult);
        requestContext.setResponseBody(string);
        //转成json，设置contentType
        HttpServletResponse response = requestContext.getResponse();
        response.setContentType("application/json;charset=utf-8");
    }
}
