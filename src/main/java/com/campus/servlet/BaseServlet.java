package com.campus.servlet;

import com.campus.entity.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet 基类。
 * 封装获取登录用户、解析整型参数、转发与重定向等通用操作，供各控制器复用。
 */
public class BaseServlet extends HttpServlet {

    /** 获取当前登录用户，未登录返回 null */
    protected User getLoginUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (User) session.getAttribute("user");
    }

    /** 解析整型参数，缺失或非法返回 null */
    protected Integer getInteger(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        if (v == null || v.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(v.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** 解析整型参数，缺失或非法返回默认值 */
    protected int getInt(HttpServletRequest req, String name, int defaultValue) {
        Integer v = getInteger(req, name);
        return v == null ? defaultValue : v;
    }

    /** 解析浮点参数，缺失或非法返回 null */
    protected Double getDouble(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        if (v == null || v.trim().isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(v.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** 转发到内部视图 */
    protected void forward(HttpServletRequest req, HttpServletResponse resp, String page)
            throws ServletException, IOException {
        req.getRequestDispatcher(page).forward(req, resp);
    }

    /** 重定向到上下文内的路径 */
    protected void redirect(HttpServletRequest req, HttpServletResponse resp, String path)
            throws IOException {
        resp.sendRedirect(req.getContextPath() + path);
    }
}
