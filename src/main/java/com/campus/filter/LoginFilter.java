package com.campus.filter;

import com.campus.entity.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 登录过滤器。
 * 系统采用“登录墙”策略：除登录、注册页与静态资源外，所有页面都必须登录后才能访问。
 * 未登录用户一律重定向到登录页，并携带原始访问地址，登录成功后可跳回。
 */
public class LoginFilter implements Filter {

    /** 无需登录即可访问的路径白名单 */
    private static final Set<String> WHITELIST = new HashSet<>(Arrays.asList(
            "/user/login", "/user/register", "/favicon.ico"));

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        String path = request.getRequestURI().substring(request.getContextPath().length());

        // 静态资源（css/js/图片）与白名单路径直接放行
        if (path.startsWith("/static/") || WHITELIST.contains(path)) {
            chain.doFilter(req, resp);
            return;
        }

        HttpSession session = request.getSession(false);
        User user = (session == null) ? null : (User) session.getAttribute("user");

        if (user == null) {
            String fullUrl = request.getRequestURI();
            if (request.getQueryString() != null) {
                fullUrl += "?" + request.getQueryString();
            }
            String returnUrl = URLEncoder.encode(fullUrl, "UTF-8");
            response.sendRedirect(request.getContextPath() + "/user/login?tip=login&returnUrl=" + returnUrl);
            return;
        }
        chain.doFilter(req, resp);
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }
}
