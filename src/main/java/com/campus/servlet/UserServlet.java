package com.campus.servlet;

import com.campus.entity.User;
import com.campus.service.UserService;
import com.campus.service.impl.UserServiceImpl;
import com.campus.util.ServiceException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用户控制器。
 * 处理注册、登录、退出、个人资料与密码修改。
 * 通过路径(/user/xxx)区分不同操作，GET 显示页面，POST 提交数据。
 */
@WebServlet("/user/*")
public class UserServlet extends BaseServlet {

    private static final String LOGIN_PAGE = "/WEB-INF/views/user/login.jsp";
    private static final String REGISTER_PAGE = "/WEB-INF/views/user/register.jsp";
    private static final String PROFILE_PAGE = "/WEB-INF/views/user/profile.jsp";

    private final UserService userService = new UserServiceImpl();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getPathInfo() == null ? "/" : req.getPathInfo();
        boolean get = "GET".equalsIgnoreCase(req.getMethod());
        switch (path) {
            case "/login":
                if (get) forward(req, resp, LOGIN_PAGE); else doLogin(req, resp);
                break;
            case "/register":
                if (get) forward(req, resp, REGISTER_PAGE); else doRegister(req, resp);
                break;
            case "/logout":
                doLogout(req, resp);
                break;
            case "/profile":
                if (get) forward(req, resp, PROFILE_PAGE); else doUpdateProfile(req, resp);
                break;
            case "/password":
                doChangePassword(req, resp);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void doLogin(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String account = req.getParameter("account");
        String password = req.getParameter("password");
        try {
            User user = userService.login(account, password);
            req.getSession().setAttribute("user", user);
            String returnUrl = req.getParameter("returnUrl");
            if (returnUrl != null && !returnUrl.trim().isEmpty()
                    && returnUrl.startsWith(req.getContextPath())) {
                resp.sendRedirect(returnUrl);
            } else {
                redirect(req, resp, "/index");
            }
        } catch (ServiceException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("account", account);
            forward(req, resp, LOGIN_PAGE);
        }
    }

    private void doRegister(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String account = req.getParameter("account");
        String password = req.getParameter("password");
        String confirm = req.getParameter("confirm");
        String nickname = req.getParameter("nickname");
        String phone = req.getParameter("phone");
        try {
            userService.register(account, password, confirm, nickname, phone);
            req.setAttribute("success", "注册成功，请使用账号登录！");
            req.setAttribute("account", account);
            forward(req, resp, LOGIN_PAGE);
        } catch (ServiceException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("account", account);
            req.setAttribute("nickname", nickname);
            req.setAttribute("phone", phone);
            forward(req, resp, REGISTER_PAGE);
        }
    }

    private void doLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (req.getSession(false) != null) {
            req.getSession(false).invalidate();
        }
        redirect(req, resp, "/index");
    }

    private void doUpdateProfile(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = getLoginUser(req);
        String nickname = req.getParameter("nickname");
        String phone = req.getParameter("phone");
        try {
            userService.updateProfile(user.getId(), nickname, phone);
            // 刷新会话中的用户信息，使页面显示最新昵称
            req.getSession().setAttribute("user", userService.findById(user.getId()));
            req.setAttribute("success", "个人资料修改成功！");
        } catch (ServiceException e) {
            req.setAttribute("error", e.getMessage());
        }
        forward(req, resp, PROFILE_PAGE);
    }

    private void doChangePassword(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = getLoginUser(req);
        String oldPwd = req.getParameter("oldPwd");
        String newPwd = req.getParameter("newPwd");
        String confirm = req.getParameter("confirm");
        try {
            userService.changePassword(user.getId(), oldPwd, newPwd, confirm);
            req.setAttribute("success", "密码修改成功，请牢记新密码！");
        } catch (ServiceException e) {
            req.setAttribute("error", e.getMessage());
        }
        forward(req, resp, PROFILE_PAGE);
    }
}
