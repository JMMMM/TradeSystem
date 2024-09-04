package com.wujm1.tradesystem.interceptor;

import com.wujm1.tradesystem.utils.HttpContextUtils;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * HttpServletRequest 过滤器
 * 解决: request.getInputStream()只能读取一次的问题
 * 目标: 流可重复读
 */
@Component
@WebFilter(filterName = "HttpServletRequestFilter", urlPatterns = "/")
public class HttpServletRequestFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ServletRequest requestWrapper = null;
        if (request instanceof HttpServletRequest) {
            requestWrapper = new RequestWrapper((HttpServletRequest) request);
        }
        //获取请求中的流，将取出来的字符串，再次转换成流，然后把它放入到新 request对象中
        // 在chain.doFiler方法中传递新的request对象
        if (null == requestWrapper) {
            chain.doFilter(request, response);
        } else {
            chain.doFilter(requestWrapper, response);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }


    /***
     * HttpServletRequest 包装器
     * 解决: request.getInputStream()只能读取一次的问题
     * 目标: 流可重复读
     */
    public class RequestWrapper extends HttpServletRequestWrapper {

        /**
         * 请求体
         */
        private String body;

        public RequestWrapper(HttpServletRequest request) {
            super(request);
            body = getBody(request);
        }

        /**
         * 获取请求体
         *
         * @param request 请求
         * @return 请求体
         */
        private String getBody(HttpServletRequest request) {
            return HttpContextUtils.getBodyString(request);
        }

        /**
         * 获取请求体
         *
         * @return 请求体
         */
        public String getBody() {
            return body;
        }


        @Override
        public BufferedReader getReader() throws IOException {
            return new BufferedReader(new InputStreamReader(getInputStream()));
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            // 创建字节数组输入流
            final ByteArrayInputStream bais = new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));

            return new ServletInputStream() {
                @Override
                public boolean isFinished() {
                    return false;
                }

                @Override
                public boolean isReady() {
                    return false;
                }

                @Override
                public void setReadListener(ReadListener readListener) {

                }

                @Override
                public int read() throws IOException {
                    return bais.read();
                }
            };
        }


    }
}

