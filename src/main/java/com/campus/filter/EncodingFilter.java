package com.campus.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
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
        // 静态资源（css / js / 图片）直接按字节原样输出即可，不给它们设置响应编码。
        // 否则 Tomcat 会按平台默认编码（中文 Windows 上是 GBK）去读文件再转成 UTF-8，
        // 文件里的中文注释就会变成乱码。
        if (!isStatic(request)) {
            response.setCharacterEncoding(encoding);
        }
        chain.doFilter(request, response);
    }

    /** 判断当前请求是否在访问 /static/ 下的静态资源 */
    private boolean isStatic(ServletRequest request) {
        if (!(request instanceof HttpServletRequest)) {
            return false;
        }
        HttpServletRequest req = (HttpServletRequest) request;
        String uri = req.getRequestURI();
        return uri != null && uri.startsWith(req.getContextPath() + "/static/");
    }

    @Override
    public void destroy() {
    }
}
