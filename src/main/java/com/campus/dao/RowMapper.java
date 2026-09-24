package com.campus.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 行映射接口：将结果集当前行转换为一个实体对象。
 * 配合 BaseDao 的通用查询方法使用。
 */
public interface RowMapper<T> {
    T mapRow(ResultSet rs) throws SQLException;
}
