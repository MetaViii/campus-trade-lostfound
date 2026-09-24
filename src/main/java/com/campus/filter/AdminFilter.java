package com.campus.filter;

import com.campus.entity.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 管理员过滤器。
 * 在登录过滤器之后执行，进一步校验当前用户是否为管理员，
 * 非管理员访问后台时转发到错误提示页。
 */
public class AdminFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        HttpSession session = request.getSession(false);
        User user = (session == null) ? null : (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/user/login?tip=login");
            return;
        }
        if (user.getRole() == null || user.getRole() != 1) {
            request.setAttribute("error", "您没有权限访问后台管理，请使用管理员账号登录。");
            request.getRequestDispatcher("/WEB-INF/views/common/error.jsp").forward(request, response);
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
