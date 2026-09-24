package com.campus.servlet.admin;

import com.campus.servlet.BaseServlet;
import com.campus.service.UserService;
import com.campus.service.impl.UserServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * 后台用户管理控制器。
 * 支持用户分页查询、禁用/恢复以及删除。
 */
@WebServlet("/admin/user/*")
public class AdminUserServlet extends BaseServlet {

    private final UserService userService = new UserServiceImpl();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getPathInfo() == null ? "/list" : req.getPathInfo();
        switch (path) {
            case "/list":   list(req, resp); break;
            case "/status": status(req, resp); break;
            case "/delete": delete(req, resp); break;
            default:        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void list(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String keyword = req.getParameter("keyword");
        int pageNum = getInt(req, "page", 1);
        req.setAttribute("page", userService.findByPage(keyword, pageNum, 10));
        req.setAttribute("keyword", keyword);
        forward(req, resp, "/WEB-INF/views/admin/user.jsp");
    }

    private void status(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer id = getInteger(req, "id");
        Integer status = getInteger(req, "status");
        if (id != null && status != null) {
            userService.updateStatus(id, status);
        }
        redirect(req, resp, "/admin/user/list");
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer id = getInteger(req, "id");
        try {
            if (id != null) {
                userService.delete(id);
            }
            redirect(req, resp, "/admin/user/list");
        } catch (RuntimeException e) {
            // 该用户已发布商品或失物招领信息，存在外键关联，无法直接删除
            String msg = URLEncoder.encode("该用户已发布内容，无法删除，建议改为禁用", "UTF-8");
            redirect(req, resp, "/admin/user/list?msg=" + msg);
        }
    }
}
