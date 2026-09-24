package com.campus.dao;

import com.campus.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 通用 DAO 基类。
 * 封装基于 PreparedStatement 的增删改查操作，统一使用预编译参数防止 SQL 注入。
 * 各具体 DAO 实现类继承本类以复用这些通用方法。
 */
public class BaseDao {

    /** 执行 INSERT / UPDATE / DELETE，返回受影响行数 */
    protected int update(String sql, Object... params) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            setParams(ps, params);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("数据库写入失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(null, ps, conn);
        }
    }

    /** 通用列表查询 */
    protected <T> List<T> queryList(String sql, RowMapper<T> mapper, Object... params) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<T> list = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            setParams(ps, params);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapper.mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("数据库查询失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, ps, conn);
        }
    }

    /** 通用单对象查询，无结果返回 null */
    protected <T> T queryOne(String sql, RowMapper<T> mapper, Object... params) {
        List<T> list = queryList(sql, mapper, params);
        return list.isEmpty() ? null : list.get(0);
    }

    /** 通用统计查询，返回第一行第一列的整型值 */
    protected int count(String sql, Object... params) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            setParams(ps, params);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("数据库统计失败：" + e.getMessage(), e);
        } finally {
            DBUtil.close(rs, ps, conn);
        }
    }

    /** 为预编译语句按顺序设置参数 */
    private void setParams(PreparedStatement ps, Object[] params) throws SQLException {
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
        }
    }
}
