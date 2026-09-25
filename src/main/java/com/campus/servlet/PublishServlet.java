package com.campus.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 发布入口控制器。
 * 只负责展示一个选择页：让用户决定要发布闲置还是发布招领，
 * 具体的发布表单仍在 /goods/publish 与 /lostfound/publish。
 */
@WebServlet("/publish")
public class PublishServlet extends BaseServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        forward(req, resp, "/WEB-INF/views/publish/index.jsp");
    }
}
