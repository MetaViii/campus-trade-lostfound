package com.campus.servlet;

import com.campus.entity.LostFound;
import com.campus.entity.User;
import com.campus.service.CategoryService;
import com.campus.service.LostFoundService;
import com.campus.service.MessageService;
import com.campus.service.impl.CategoryServiceImpl;
import com.campus.service.impl.LostFoundServiceImpl;
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
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 失物招领控制器。
 * 处理失物/招领信息的浏览、搜索、查看详情、发布、修改、状态变更与删除。
 */
@WebServlet("/lostfound/*")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 5 * 1024 * 1024, maxRequestSize = 10 * 1024 * 1024)
public class LostFoundServlet extends BaseServlet {

    private final LostFoundService lostFoundService = new LostFoundServiceImpl();
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

    private void list(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String keyword = req.getParameter("keyword");
        Integer type = getInteger(req, "type");
        Integer categoryId = getInteger(req, "categoryId");
        Integer status = getInteger(req, "status");
        int pageNum = getInt(req, "page", 1);
        req.setAttribute("page", lostFoundService.findByPage(keyword, type, categoryId, status, null, pageNum, 9));
        req.setAttribute("categories", categoryService.findEnabledByType(2));
        req.setAttribute("keyword", keyword);
        req.setAttribute("type", type);
        req.setAttribute("categoryId", categoryId);
        req.setAttribute("status", status);
        forward(req, resp, "/WEB-INF/views/lostfound/list.jsp");
    }

    private void detail(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Integer id = getInteger(req, "id");
        LostFound lf = id == null ? null : lostFoundService.viewDetail(id);
        if (lf == null) {
            req.setAttribute("error", "该信息不存在或已被删除");
            forward(req, resp, "/WEB-INF/views/common/error.jsp");
            return;
        }
        req.setAttribute("lf", lf);
        req.setAttribute("messages", messageService.findByTarget(2, id));
        // 特色功能：为当前信息智能匹配相反类型的疑似对应信息（取相似度最高的前 5 条）
        req.setAttribute("matches", lostFoundService.smartMatch(id, 5));
        forward(req, resp, "/WEB-INF/views/lostfound/detail.jsp");
    }

    private void showPublish(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("categories", categoryService.findEnabledByType(2));
        forward(req, resp, "/WEB-INF/views/lostfound/publish.jsp");
    }

    private void doPublish(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = getLoginUser(req);
        LostFound lf = readForm(req, new LostFound());
        lf.setUserId(user.getId());
        try {
            Part part = req.getPart("image");
            String uploadDir = getServletContext().getRealPath("/static/uploads");
            lf.setImage(UploadUtil.save(part, uploadDir));
            lostFoundService.publish(lf);
            redirect(req, resp, "/lostfound/my");
        } catch (ServiceException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("lf", lf);
            req.setAttribute("categories", categoryService.findEnabledByType(2));
            forward(req, resp, "/WEB-INF/views/lostfound/publish.jsp");
        }
    }

    private void showEdit(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = getLoginUser(req);
        Integer id = getInteger(req, "id");
        LostFound lf = id == null ? null : lostFoundService.findById(id);
        if (!canManage(user, lf)) {
            req.setAttribute("error", "您无权编辑该信息");
            forward(req, resp, "/WEB-INF/views/common/error.jsp");
            return;
        }
        req.setAttribute("lf", lf);
        req.setAttribute("categories", categoryService.findEnabledByType(2));
        forward(req, resp, "/WEB-INF/views/lostfound/edit.jsp");
    }

    private void doEdit(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = getLoginUser(req);
        Integer id = getInteger(req, "id");
        LostFound existing = id == null ? null : lostFoundService.findById(id);
        if (!canManage(user, existing)) {
            req.setAttribute("error", "您无权编辑该信息");
            forward(req, resp, "/WEB-INF/views/common/error.jsp");
            return;
        }
        LostFound lf = readForm(req, existing);
        lf.setId(id);
        lf.setStatus(getInt(req, "status", existing.getStatus()));
        try {
            Part part = req.getPart("image");
            String uploadDir = getServletContext().getRealPath("/static/uploads");
            String newImage = UploadUtil.save(part, uploadDir);
            if (newImage != null) {
                lf.setImage(newImage);
            }
            lostFoundService.modify(lf);
            redirect(req, resp, "/lostfound/detail?id=" + id);
        } catch (ServiceException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("lf", lf);
            req.setAttribute("categories", categoryService.findEnabledByType(2));
            forward(req, resp, "/WEB-INF/views/lostfound/edit.jsp");
        }
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = getLoginUser(req);
        Integer id = getInteger(req, "id");
        LostFound lf = id == null ? null : lostFoundService.findById(id);
        if (canManage(user, lf)) {
            lostFoundService.delete(id);
        }
        redirect(req, resp, "/lostfound/my");
    }

    private void doStatus(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = getLoginUser(req);
        Integer id = getInteger(req, "id");
        Integer status = getInteger(req, "status");
        LostFound lf = id == null ? null : lostFoundService.findById(id);
        if (canManage(user, lf) && status != null) {
            lostFoundService.updateStatus(id, status);
        }
        redirect(req, resp, "/lostfound/my");
    }

    private void myList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = getLoginUser(req);
        int pageNum = getInt(req, "page", 1);
        req.setAttribute("page", lostFoundService.findByPage(null, null, null, null, user.getId(), pageNum, 9));
        forward(req, resp, "/WEB-INF/views/lostfound/my.jsp");
    }

    private LostFound readForm(HttpServletRequest req, LostFound lf) {
        lf.setType(getInteger(req, "type"));
        lf.setName(req.getParameter("name"));
        lf.setCategoryId(getInteger(req, "categoryId"));
        lf.setPlace(req.getParameter("place"));
        lf.setHappenTime(parseDateTime(req.getParameter("happenTime")));
        lf.setFeature(req.getParameter("feature"));
        lf.setContact(req.getParameter("contact"));
        return lf;
    }

    /** 解析 datetime-local 输入框提交的时间 */
    private Date parseDateTime(String s) {
        if (s == null || s.trim().isEmpty()) {
            return null;
        }
        try {
            s = s.trim().replace('T', ' ');
            SimpleDateFormat f = s.length() <= 16
                    ? new SimpleDateFormat("yyyy-MM-dd HH:mm")
                    : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return f.parse(s);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean canManage(User user, LostFound lf) {
        if (user == null || lf == null) {
            return false;
        }
        return lf.getUserId().equals(user.getId()) || (user.getRole() != null && user.getRole() == 1);
    }
}
