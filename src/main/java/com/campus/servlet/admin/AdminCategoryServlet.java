package com.campus.servlet.admin;

import com.campus.servlet.BaseServlet;
import com.campus.service.CategoryService;
import com.campus.service.impl.CategoryServiceImpl;
import com.campus.util.ServiceException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * 后台分类管理控制器。
 * 支持商品分类与失物招领分类的新增、修改、启停与删除。
 */
@WebServlet("/admin/category/*")
public class AdminCategoryServlet extends BaseServlet {

    private final CategoryService categoryService = new CategoryServiceImpl();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getPathInfo() == null ? "/list" : req.getPathInfo();
        switch (path) {
            case "/list":   list(req, resp); break;
            case "/add":    add(req, resp); break;
            case "/edit":   edit(req, resp); break;
            case "/status": status(req, resp); break;
            case "/delete": delete(req, resp); break;
            default:        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void list(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("goodsCategories", categoryService.findAllByType(1));
        req.setAttribute("lostFoundCategories", categoryService.findAllByType(2));
        forward(req, resp, "/WEB-INF/views/admin/category.jsp");
    }

    private void add(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            categoryService.add(req.getParameter("name"), getInteger(req, "type"), getInt(req, "status", 1));
            redirect(req, resp, "/admin/category/list");
        } catch (ServiceException e) {
            redirect(req, resp, "/admin/category/list?msg=" + URLEncoder.encode(e.getMessage(), "UTF-8"));
        }
    }

    private void edit(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            categoryService.modify(getInteger(req, "id"), req.getParameter("name"),
                    getInteger(req, "type"), getInt(req, "status", 1));
            redirect(req, resp, "/admin/category/list");
        } catch (ServiceException e) {
            redirect(req, resp, "/admin/category/list?msg=" + URLEncoder.encode(e.getMessage(), "UTF-8"));
        }
    }

    private void status(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer id = getInteger(req, "id");
        Integer status = getInteger(req, "status");
        if (id != null && status != null) {
            categoryService.updateStatus(id, status);
        }
        redirect(req, resp, "/admin/category/list");
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer id = getInteger(req, "id");
        try {
            if (id != null) {
                categoryService.delete(id);
            }
            redirect(req, resp, "/admin/category/list");
        } catch (RuntimeException e) {
            // 该分类下存在商品或失物招领信息，无法删除
            String msg = URLEncoder.encode("该分类已被使用，无法删除，建议改为停用", "UTF-8");
            redirect(req, resp, "/admin/category/list?msg=" + msg);
        }
    }
}
