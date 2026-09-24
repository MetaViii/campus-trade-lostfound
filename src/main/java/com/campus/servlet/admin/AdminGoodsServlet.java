package com.campus.servlet.admin;

import com.campus.servlet.BaseServlet;
import com.campus.service.CategoryService;
import com.campus.service.GoodsService;
import com.campus.service.impl.CategoryServiceImpl;
import com.campus.service.impl.GoodsServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 后台商品管理控制器。
 * 管理员可查询全部商品并进行状态维护或删除。
 */
@WebServlet("/admin/goods/*")
public class AdminGoodsServlet extends BaseServlet {

    private final GoodsService goodsService = new GoodsServiceImpl();
    private final CategoryService categoryService = new CategoryServiceImpl();

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
        Integer categoryId = getInteger(req, "categoryId");
        Integer status = getInteger(req, "status");
        int pageNum = getInt(req, "page", 1);
        req.setAttribute("page", goodsService.findByPage(keyword, categoryId, status, null, pageNum, 10));
        req.setAttribute("categories", categoryService.findAllByType(1));
        req.setAttribute("keyword", keyword);
        req.setAttribute("categoryId", categoryId);
        req.setAttribute("status", status);
        forward(req, resp, "/WEB-INF/views/admin/goods.jsp");
    }

    private void status(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer id = getInteger(req, "id");
        Integer status = getInteger(req, "status");
        if (id != null && status != null) {
            goodsService.updateStatus(id, status);
        }
        redirect(req, resp, "/admin/goods/list");
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer id = getInteger(req, "id");
        if (id != null) {
            goodsService.delete(id);
        }
        redirect(req, resp, "/admin/goods/list");
    }
}
