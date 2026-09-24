package com.campus.dao.impl;

import com.campus.dao.BaseDao;
import com.campus.dao.RowMapper;
import com.campus.dao.UserDao;
import com.campus.entity.User;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户 DAO 实现类。
 */
public class UserDaoImpl extends BaseDao implements UserDao {

    /** 将结果集一行映射为 User 对象 */
    private final RowMapper<User> userMapper = rs -> {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setAccount(rs.getString("account"));
        u.setPassword(rs.getString("password"));
        u.setNickname(rs.getString("nickname"));
        u.setPhone(rs.getString("phone"));
        u.setRole(rs.getInt("role"));
        u.setStatus(rs.getInt("status"));
        u.setCreateTime(rs.getTimestamp("create_time"));
        return u;
    };

    @Override
    public User findByAccount(String account) {
        String sql = "SELECT * FROM `user` WHERE account = ?";
        return queryOne(sql, userMapper, account);
    }

    @Override
    public User findById(Integer id) {
        String sql = "SELECT * FROM `user` WHERE id = ?";
        return queryOne(sql, userMapper, id);
    }

    @Override
    public int insert(User user) {
        String sql = "INSERT INTO `user`(account, password, nickname, phone, role, status) VALUES(?,?,?,?,?,?)";
        return update(sql, user.getAccount(), user.getPassword(), user.getNickname(),
                user.getPhone(), user.getRole(), user.getStatus());
    }

    @Override
    public int update(User user) {
        String sql = "UPDATE `user` SET nickname = ?, phone = ? WHERE id = ?";
        return update(sql, user.getNickname(), user.getPhone(), user.getId());
    }

    @Override
    public int updatePassword(Integer id, String password) {
        String sql = "UPDATE `user` SET password = ? WHERE id = ?";
        return update(sql, password, id);
    }

    @Override
    public int updateStatus(Integer id, Integer status) {
        String sql = "UPDATE `user` SET status = ? WHERE id = ?";
        return update(sql, status, id);
    }

    @Override
    public int delete(Integer id) {
        String sql = "DELETE FROM `user` WHERE id = ?";
        return update(sql, id);
    }

    @Override
    public List<User> findByPage(String keyword, int offset, int limit) {
        StringBuilder sql = new StringBuilder("SELECT * FROM `user` WHERE 1 = 1");
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (account LIKE ? OR nickname LIKE ?)");
            String k = "%" + keyword.trim() + "%";
            params.add(k);
            params.add(k);
        }
        sql.append(" ORDER BY id DESC LIMIT ?, ?");
        params.add(offset);
        params.add(limit);
        return queryList(sql.toString(), userMapper, params.toArray());
    }

    @Override
    public int count(String keyword) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM `user` WHERE 1 = 1");
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (account LIKE ? OR nickname LIKE ?)");
            String k = "%" + keyword.trim() + "%";
            params.add(k);
            params.add(k);
        }
        return count(sql.toString(), params.toArray());
    }

    @Override
    public int countTotal() {
        // 显式调用 BaseDao 的通用 count，避免与本类的 count(String keyword) 重载混淆
        return super.count("SELECT COUNT(*) FROM `user`");
    }
}
