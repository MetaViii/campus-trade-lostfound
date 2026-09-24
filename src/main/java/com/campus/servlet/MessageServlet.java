package com.campus.servlet;

import com.campus.entity.Message;
import com.campus.entity.User;
import com.campus.service.MessageService;
import com.campus.service.impl.MessageServiceImpl;
import com.campus.util.ServiceException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * 留言控制器。
 * 处理在商品/失物招领详情页下发表与删除留言，操作完成后跳回对应详情页。
 */
@WebServlet("/message/*")
public class MessageServlet extends BaseServlet {

    private final MessageService messageService = new MessageServiceImpl();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getPathInfo() == null ? "/" : req.getPathInfo();
        switch (path) {
            case "/add":
                doAdd(req, resp);
                break;
            case "/delete":
                delete(req, resp);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void doAdd(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = getLoginUser(req);
        Integer targetType = getInteger(req, "targetType");
        Integer targetId = getInteger(req, "targetId");
        String content = req.getParameter("content");
        String error = null;
        try {
            Message message = new Message();
            message.setTargetType(targetType);
            message.setTargetId(targetId);
            message.setUserId(user.getId());
            message.setContent(content);
            messageService.add(message);
        } catch (ServiceException e) {
            error = e.getMessage();
        }
        resp.sendRedirect(buildDetailUrl(req, targetType, targetId, error));
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = getLoginUser(req);
        Integer id = getInteger(req, "id");
        Integer targetType = getInteger(req, "targetType");
        Integer targetId = getInteger(req, "targetId");
        Message message = id == null ? null : messageService.findById(id);
        if (message != null) {
            boolean isOwner = message.getUserId().equals(user.getId());
            boolean isAdmin = user.getRole() != null && user.getRole() == 1;
            if (isOwner || isAdmin) {
                messageService.delete(id);
            }
            targetType = message.getTargetType();
            targetId = message.getTargetId();
        }
        resp.sendRedirect(buildDetailUrl(req, targetType, targetId, null));
    }

    /** 根据目标类型拼接返回的详情页地址 */
    private String buildDetailUrl(HttpServletRequest req, Integer targetType, Integer targetId, String error)
            throws IOException {
        String base = (targetType != null && targetType == 1)
                ? "/goods/detail?id=" + targetId
                : "/lostfound/detail?id=" + targetId;
        if (error != null) {
            base += "&msgError=" + URLEncoder.encode(error, "UTF-8");
        }
        return req.getContextPath() + base;
    }
}
