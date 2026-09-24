package com.campus.service;

import com.campus.entity.User;
import com.campus.util.PageBean;

/**
 * 用户业务接口。
 */
public interface UserService {

    /** 登录校验，成功返回用户对象，失败抛出 ServiceException */
    User login(String account, String password);

    /** 用户注册 */
    void register(String account, String password, String confirm, String nickname, String phone);

    /** 修改个人资料（昵称、联系方式） */
    void updateProfile(Integer id, String nickname, String phone);

    /** 修改密码 */
    void changePassword(Integer id, String oldPwd, String newPwd, String confirm);

    /** 分页查询用户（后台） */
    PageBean<User> findByPage(String keyword, int pageNum, int pageSize);

    /** 按主键查询 */
    User findById(Integer id);

    /** 修改用户状态（禁用 / 恢复） */
    void updateStatus(Integer id, Integer status);

    /** 删除用户 */
    void delete(Integer id);

    /** 用户总数 */
    int countTotal();
}
