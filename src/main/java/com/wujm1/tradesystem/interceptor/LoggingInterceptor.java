package com.wujm1.tradesystem.interceptor;

import com.wujm1.tradesystem.utils.HttpContextUtils;
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
        try {
            String remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
            StringBuilder sb = new StringBuilder();
            sb.append("IP: " + remoteAddr + "," + request.getMethod() + " url: " + request.getRequestURL() + ",");
            Enumeration headers = request.getHeaderNames();
            while (headers.hasMoreElements()) {
                String header = headers.nextElement().toString();
                sb.append(", Header: " + header + "=" + request.getHeader(header));
            }
            String parameterMessage = " Parameters: [%s]";
            StringBuilder tmp = new StringBuilder();
            for (String key : request.getParameterMap().keySet()) {
                tmp.append(key + "=" + request.getParameter(key) + ", ");
            }
            if (tmp.length() > 0) {
                tmp.delete(tmp.length() - 2, tmp.length());
            }
            sb.append(String.format(parameterMessage, tmp));

            String bodyMessage = ",Body:[%s] ";
            tmp = new StringBuilder();
            //打印POST请求的Body
            if (request instanceof HttpServletRequestFilter.RequestWrapper) {
                tmp.append(HttpContextUtils.getBodyString(request));
            }
            sb.append(String.format(bodyMessage, tmp));

            log.info(sb.toString());
        } catch (Exception e) {
            log.error("LoggingInterceptor preHandle error", e);
        }
        return true;
    }
}
