package com.xuecheng.framework.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.RequestContext;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        if(requestAttributes != null){
            HttpServletRequest request = requestAttributes.getRequest();
            //取出当前请求的Header，拿到JWT
            Enumeration<String> headerNames = request.getHeaderNames();
            if(headerNames != null){
                while ((headerNames.hasMoreElements())){
                    String headerName = headerNames.nextElement();
                    String headerValue = request.getHeader(headerName);
                    //将Header向下传递
                    requestTemplate.header(headerName,headerValue);
                }
            }
        }
    }
}
