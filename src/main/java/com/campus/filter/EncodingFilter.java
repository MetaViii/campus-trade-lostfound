package com.campus.filter;

import javax.servlet.*;
import java.io.IOException;

/**
 * 字符编码过滤器。
 * 统一设置请求与响应的字符编码，避免提交或显示中文时出现乱码。
 */
public class EncodingFilter implements Filter {

    private String encoding = "UTF-8";

    @Override
    public void init(FilterConfig filterConfig) {
        String e = filterConfig.getInitParameter("encoding");
        if (e != null && !e.trim().isEmpty()) {
            encoding = e;
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        request.setCharacterEncoding(encoding);
        response.setCharacterEncoding(encoding);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
