package com.campus.dao;

import com.campus.entity.User;

import java.util.List;

/**
 * 用户数据访问接口。
 */
public interface UserDao {

    /** 按账号查询用户（用于登录、注册查重） */
    User findByAccount(String account);

    /** 按主键查询用户 */
    User findById(Integer id);

    /** 新增用户 */
    int insert(User user);

    /** 修改昵称、联系方式 */
    int update(User user);

    /** 修改密码 */
    int updatePassword(Integer id, String password);

    /** 修改状态（禁用 / 恢复） */
    int updateStatus(Integer id, Integer status);

    /** 删除用户 */
    int delete(Integer id);

    /** 分页查询用户（按账号或昵称模糊匹配） */
    List<User> findByPage(String keyword, int offset, int limit);

    /** 统计符合条件的用户数 */
    int count(String keyword);

    /** 统计用户总数 */
    int countTotal();
}
