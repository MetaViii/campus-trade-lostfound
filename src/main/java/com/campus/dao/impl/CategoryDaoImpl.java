package com.campus.dao.impl;

import com.campus.dao.BaseDao;
import com.campus.dao.CategoryDao;
import com.campus.dao.RowMapper;
import com.campus.entity.Category;

import java.util.List;

/**
 * 分类 DAO 实现类。
 */
public class CategoryDaoImpl extends BaseDao implements CategoryDao {

    private final RowMapper<Category> categoryMapper = rs -> {
        Category c = new Category();
        c.setId(rs.getInt("id"));
        c.setName(rs.getString("name"));
        c.setType(rs.getInt("type"));
        c.setStatus(rs.getInt("status"));
        return c;
    };

    @Override
    public List<Category> findEnabledByType(Integer type) {
        String sql = "SELECT * FROM category WHERE type = ? AND status = 1 ORDER BY id";
        return queryList(sql, categoryMapper, type);
    }

    @Override
    public List<Category> findAllByType(Integer type) {
        String sql = "SELECT * FROM category WHERE type = ? ORDER BY id";
        return queryList(sql, categoryMapper, type);
    }

    @Override
    public List<Category> findAll() {
        String sql = "SELECT * FROM category ORDER BY type, id";
        return queryList(sql, categoryMapper);
    }

    @Override
    public Category findById(Integer id) {
        String sql = "SELECT * FROM category WHERE id = ?";
        return queryOne(sql, categoryMapper, id);
    }

    @Override
    public int insert(Category category) {
        String sql = "INSERT INTO category(name, type, status) VALUES(?,?,?)";
        return update(sql, category.getName(), category.getType(), category.getStatus());
    }

    @Override
    public int update(Category category) {
        String sql = "UPDATE category SET name = ?, type = ?, status = ? WHERE id = ?";
        return update(sql, category.getName(), category.getType(), category.getStatus(), category.getId());
    }

    @Override
    public int updateStatus(Integer id, Integer status) {
        String sql = "UPDATE category SET status = ? WHERE id = ?";
        return update(sql, status, id);
    }

    @Override
    public int delete(Integer id) {
        String sql = "DELETE FROM category WHERE id = ?";
        return update(sql, id);
    }
}
