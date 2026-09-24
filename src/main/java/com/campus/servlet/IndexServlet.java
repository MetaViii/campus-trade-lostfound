package com.campus.servlet;

import com.campus.service.GoodsService;
import com.campus.service.LostFoundService;
import com.campus.service.impl.GoodsServiceImpl;
import com.campus.service.impl.LostFoundServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 首页控制器。
 * 加载最新二手商品与失物招领信息并展示首页。
 */
@WebServlet("/index")
public class IndexServlet extends BaseServlet {

    private final GoodsService goodsService = new GoodsServiceImpl();
    private final LostFoundService lostFoundService = new LostFoundServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("latestGoods", goodsService.findLatest(8));
        req.setAttribute("latestLostFound", lostFoundService.findLatest(8));
        forward(req, resp, "/WEB-INF/views/index.jsp");
    }
}
