package com.campus.servlet;

import com.campus.entity.Goods;
import com.campus.entity.User;
import com.campus.service.CategoryService;
import com.campus.service.GoodsService;
import com.campus.service.MessageService;
import com.campus.service.impl.CategoryServiceImpl;
import com.campus.service.impl.GoodsServiceImpl;
import com.campus.service.impl.MessageServiceImpl;
import com.campus.util.ServiceException;
import com.campus.util.UploadUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;

/**
 * 二手商品控制器。
 * 处理商品的浏览、搜索、查看详情、发布、修改、状态变更与删除。
 */
@WebServlet("/goods/*")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 5 * 1024 * 1024, maxRequestSize = 10 * 1024 * 1024)
public class GoodsServlet extends BaseServlet {

    private final GoodsService goodsService = new GoodsServiceImpl();
    private final CategoryService categoryService = new CategoryServiceImpl();
    private final MessageService messageService = new MessageServiceImpl();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getPathInfo() == null ? "/list" : req.getPathInfo();
        boolean get = "GET".equalsIgnoreCase(req.getMethod());
        switch (path) {
            case "/list":    list(req, resp); break;
            case "/detail":  detail(req, resp); break;
            case "/publish": if (get) showPublish(req, resp); else doPublish(req, resp); break;
            case "/edit":    if (get) showEdit(req, resp); else doEdit(req, resp); break;
            case "/delete":  delete(req, resp); break;
            case "/status":  doStatus(req, resp); break;
            case "/my":      myList(req, resp); break;
            default:         resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /** 商品列表（多条件搜索 + 分页） */
    private void list(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String keyword = req.getParameter("keyword");
        Integer categoryId = getInteger(req, "categoryId");
        Integer status = getInteger(req, "status");
        int pageNum = getInt(req, "page", 1);
        req.setAttribute("page", goodsService.findByPage(keyword, categoryId, status, null, pageNum, 9));
        req.setAttribute("categories", categoryService.findEnabledByType(1));
        req.setAttribute("keyword", keyword);
        req.setAttribute("categoryId", categoryId);
        req.setAttribute("status", status);
        forward(req, resp, "/WEB-INF/views/goods/list.jsp");
    }

    /** 商品详情（含留言） */
    private void detail(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Integer id = getInteger(req, "id");
        Goods goods = id == null ? null : goodsService.viewDetail(id);
        if (goods == null) {
            req.setAttribute("error", "商品不存在或已被删除");
            forward(req, resp, "/WEB-INF/views/common/error.jsp");
            return;
        }
        req.setAttribute("goods", goods);
        req.setAttribute("messages", messageService.findByTarget(1, id));
        forward(req, resp, "/WEB-INF/views/goods/detail.jsp");
    }

    private void showPublish(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("categories", categoryService.findEnabledByType(1));
        forward(req, resp, "/WEB-INF/views/goods/publish.jsp");
    }

    private void doPublish(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = getLoginUser(req);
        Goods goods = readForm(req, new Goods());
        goods.setUserId(user.getId());
        try {
            Part part = req.getPart("image");
            String uploadDir = getServletContext().getRealPath("/static/uploads");
            goods.setImage(UploadUtil.save(part, uploadDir));
            goodsService.publish(goods);
            redirect(req, resp, "/goods/my");
        } catch (ServiceException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("goods", goods);
            req.setAttribute("categories", categoryService.findEnabledByType(1));
            forward(req, resp, "/WEB-INF/views/goods/publish.jsp");
        }
    }

    private void showEdit(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = getLoginUser(req);
        Integer id = getInteger(req, "id");
        Goods goods = id == null ? null : goodsService.findById(id);
        if (!canManage(user, goods)) {
            req.setAttribute("error", "您无权编辑该商品");
            forward(req, resp, "/WEB-INF/views/common/error.jsp");
            return;
        }
        req.setAttribute("goods", goods);
        req.setAttribute("categories", categoryService.findEnabledByType(1));
        forward(req, resp, "/WEB-INF/views/goods/edit.jsp");
    }

    private void doEdit(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = getLoginUser(req);
        Integer id = getInteger(req, "id");
        Goods existing = id == null ? null : goodsService.findById(id);
        if (!canManage(user, existing)) {
            req.setAttribute("error", "您无权编辑该商品");
            forward(req, resp, "/WEB-INF/views/common/error.jsp");
            return;
        }
        Goods goods = readForm(req, existing);
        goods.setId(id);
        goods.setStatus(getInt(req, "status", existing.getStatus()));
        try {
            Part part = req.getPart("image");
            String uploadDir = getServletContext().getRealPath("/static/uploads");
            String newImage = UploadUtil.save(part, uploadDir);
            if (newImage != null) {
                goods.setImage(newImage); // 上传了新图则替换，否则保留原图
            }
            goodsService.modify(goods);
            redirect(req, resp, "/goods/detail?id=" + id);
        } catch (ServiceException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("goods", goods);
            req.setAttribute("categories", categoryService.findEnabledByType(1));
            forward(req, resp, "/WEB-INF/views/goods/edit.jsp");
        }
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = getLoginUser(req);
        Integer id = getInteger(req, "id");
        Goods goods = id == null ? null : goodsService.findById(id);
        if (canManage(user, goods)) {
            goodsService.delete(id);
        }
        redirect(req, resp, "/goods/my");
    }

    private void doStatus(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = getLoginUser(req);
        Integer id = getInteger(req, "id");
        Integer status = getInteger(req, "status");
        Goods goods = id == null ? null : goodsService.findById(id);
        if (canManage(user, goods) && status != null) {
            goodsService.updateStatus(id, status);
        }
        redirect(req, resp, "/goods/my");
    }

    /** 我发布的商品 */
    private void myList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = getLoginUser(req);
        int pageNum = getInt(req, "page", 1);
        req.setAttribute("page", goodsService.findByPage(null, null, null, user.getId(), pageNum, 9));
        forward(req, resp, "/WEB-INF/views/goods/my.jsp");
    }

    /** 从表单读取商品字段（不含图片、发布者、状态） */
    private Goods readForm(HttpServletRequest req, Goods goods) {
        goods.setTitle(req.getParameter("title"));
        goods.setCategoryId(getInteger(req, "categoryId"));
        goods.setPrice(getDouble(req, "price"));
        goods.setQuality(req.getParameter("quality"));
        goods.setDescription(req.getParameter("description"));
        goods.setTradePlace(req.getParameter("tradePlace"));
        goods.setContact(req.getParameter("contact"));
        return goods;
    }

    /** 当前用户是否可管理该商品（发布者本人或管理员） */
    private boolean canManage(User user, Goods goods) {
        if (user == null || goods == null) {
            return false;
        }
        return goods.getUserId().equals(user.getId()) || (user.getRole() != null && user.getRole() == 1);
    }
}
