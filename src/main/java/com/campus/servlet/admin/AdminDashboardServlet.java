package com.campus.servlet.admin;

import com.campus.servlet.BaseServlet;
import com.campus.service.*;
import com.campus.service.impl.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 后台首页（仪表盘）控制器。
 * 统计各类数据数量并展示最新发布的信息。
 */
@WebServlet("/admin/index")
public class AdminDashboardServlet extends BaseServlet {

    private final UserService userService = new UserServiceImpl();
    private final GoodsService goodsService = new GoodsServiceImpl();
    private final LostFoundService lostFoundService = new LostFoundServiceImpl();
    private final CategoryService categoryService = new CategoryServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("userCount", userService.countTotal());
        req.setAttribute("goodsCount", goodsService.countTotal());
        req.setAttribute("lostFoundCount", lostFoundService.countTotal());
        req.setAttribute("categoryCount", categoryService.findAll().size());
        req.setAttribute("recentGoods", goodsService.findLatest(5));
        req.setAttribute("recentLostFound", lostFoundService.findLatest(5));
        forward(req, resp, "/WEB-INF/views/admin/dashboard.jsp");
    }
}
