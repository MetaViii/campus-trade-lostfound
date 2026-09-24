package com.campus.service.impl;

import com.campus.dao.UserDao;
import com.campus.dao.impl.UserDaoImpl;
import com.campus.entity.User;
import com.campus.service.UserService;
import com.campus.util.PageBean;
import com.campus.util.PasswordUtil;
import com.campus.util.ServiceException;

import java.util.List;

/**
 * 用户业务实现类。
 * 负责登录、注册、资料维护等业务规则校验，并调用 DAO 完成持久化。
 */
public class UserServiceImpl implements UserService {

    private final UserDao userDao = new UserDaoImpl();

    @Override
    public User login(String account, String password) {
        if (isBlank(account) || isBlank(password)) {
            throw new ServiceException("账号和密码不能为空");
        }
        User user = userDao.findByAccount(account.trim());
        if (user == null || !PasswordUtil.verify(password, user.getPassword())) {
            throw new ServiceException("账号或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new ServiceException("该账号已被禁用，请联系管理员");
        }
        return user;
    }

    @Override
    public void register(String account, String password, String confirm, String nickname, String phone) {
        if (isBlank(account) || isBlank(password) || isBlank(nickname)) {
            throw new ServiceException("账号、密码和昵称不能为空");
        }
        account = account.trim();
        if (account.length() < 3 || account.length() > 20) {
            throw new ServiceException("账号长度应在 3~20 个字符之间");
        }
        if (password.length() < 6) {
            throw new ServiceException("密码长度不能少于 6 位");
        }
        if (!password.equals(confirm)) {
            throw new ServiceException("两次输入的密码不一致");
        }
        if (userDao.findByAccount(account) != null) {
            throw new ServiceException("该账号已被注册，请更换");
        }
        User user = new User();
        user.setAccount(account);
        user.setPassword(PasswordUtil.md5(password));
        user.setNickname(nickname.trim());
        user.setPhone(phone == null ? null : phone.trim());
        user.setRole(0);
        user.setStatus(1);
        userDao.insert(user);
    }

    @Override
    public void updateProfile(Integer id, String nickname, String phone) {
        if (isBlank(nickname)) {
            throw new ServiceException("昵称不能为空");
        }
        User user = userDao.findById(id);
        if (user == null) {
            throw new ServiceException("用户不存在");
        }
        user.setNickname(nickname.trim());
        user.setPhone(phone == null ? null : phone.trim());
        userDao.update(user);
    }

    @Override
    public void changePassword(Integer id, String oldPwd, String newPwd, String confirm) {
        User user = userDao.findById(id);
        if (user == null) {
            throw new ServiceException("用户不存在");
        }
        if (!PasswordUtil.verify(oldPwd == null ? "" : oldPwd, user.getPassword())) {
            throw new ServiceException("原密码不正确");
        }
        if (isBlank(newPwd) || newPwd.length() < 6) {
            throw new ServiceException("新密码长度不能少于 6 位");
        }
        if (!newPwd.equals(confirm)) {
            throw new ServiceException("两次输入的新密码不一致");
        }
        userDao.updatePassword(id, PasswordUtil.md5(newPwd));
    }

    @Override
    public PageBean<User> findByPage(String keyword, int pageNum, int pageSize) {
        if (pageSize < 1) pageSize = 10;
        int total = userDao.count(keyword);
        int totalPages = Math.max(1, (total + pageSize - 1) / pageSize);
        if (pageNum < 1) pageNum = 1;
        if (pageNum > totalPages) pageNum = totalPages;
        int offset = (pageNum - 1) * pageSize;
        List<User> list = userDao.findByPage(keyword, offset, pageSize);
        return new PageBean<>(pageNum, pageSize, total, list);
    }

    @Override
    public User findById(Integer id) {
        return userDao.findById(id);
    }

    @Override
    public void updateStatus(Integer id, Integer status) {
        userDao.updateStatus(id, status);
    }

    @Override
    public void delete(Integer id) {
        userDao.delete(id);
    }

    @Override
    public int countTotal() {
        return userDao.countTotal();
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
