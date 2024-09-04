package com.wujm1.tradesystem.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

/**
 * @author wujiaming
 * @date 2024-09-04 15:12
 */
@Component
@Slf4j
public class LoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("URL: " + request.getRequestURL() + ", Method: " + request.getMethod() + ", Parameters: " + request.getParameterMap());
        // 如果需要打印请求体，可以使用 ContentCachingRequestWrapper 来缓存请求体
        if (request instanceof ContentCachingRequestWrapper) {
            ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;
            String body = new String(wrapper.getContentAsByteArray());
            sb.append(", Body: " + body);
        }
        Enumeration headers = request.getHeaderNames();
        while (headers.hasMoreElements()) {
            String header = headers.nextElement().toString();
            sb.append(", Header: " + header + "=" + request.getHeader(header));
        }
        log.info(sb.toString());
        return true;
    }
}
