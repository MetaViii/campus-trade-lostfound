package com.campus.servlet;

import com.campus.entity.AiSearchResult;
import com.campus.service.AiService;
import com.campus.service.impl.AiServiceImpl;
import com.campus.util.ServiceException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * AI 智能匹配控制器（前台）。
 * 用户在页面上用一句话描述要找的东西，系统做向量检索并给出答复。
 */
@WebServlet("/ai/*")
public class AiServlet extends BaseServlet {

    private final AiService aiService = new AiServiceImpl();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getPathInfo() == null ? "/index" : req.getPathInfo();
        switch (path) {
            case "/index":  index(req, resp); break;
            case "/search": search(req, resp); break;
            default:        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /** AI 页面：分失物招领与二手商品两块，各自一个描述输入框 */
    private void index(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        prepare(req);
        forward(req, resp, "/WEB-INF/views/ai/index.jsp");
    }

    /**
     * 执行匹配并把结果转发回同一个页面。
     * 用转发而不是重定向，是为了让用户在页面上直接看到结果与错误提示。
     */
    private void search(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int module = getInt(req, "module", AiService.MODULE_LOST_FOUND);
        String query = req.getParameter("query");

        prepare(req);
        req.setAttribute("activeModule", module);
        req.setAttribute("query", query);

        try {
            AiSearchResult result = aiService.search(module, query, getLoginUser(req));
            req.setAttribute("aiResult", result);
        } catch (ServiceException e) {
            req.setAttribute("error", e.getMessage());
        } catch (RuntimeException e) {
            req.setAttribute("error", "AI 匹配失败：" + e.getMessage());
        }
        forward(req, resp, "/WEB-INF/views/ai/index.jsp");
    }

    /** 页面渲染前统一准备的属性 */
    private void prepare(HttpServletRequest req) {
        req.setAttribute("config", aiService.getConfig());
        req.setAttribute("ready", aiService.isReady());
        req.setAttribute("unavailableReason", aiService.unavailableReason());
    }
}
