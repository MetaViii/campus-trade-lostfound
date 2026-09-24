package com.campus.dao.impl;

import com.campus.dao.BaseDao;
import com.campus.dao.GoodsDao;
import com.campus.dao.RowMapper;
import com.campus.entity.Goods;

import java.util.ArrayList;
import java.util.List;

/**
 * 二手商品 DAO 实现类。
 * 列表查询采用动态 SQL 拼接，支持关键字、分类、状态、发布者多条件组合。
 */
public class GoodsDaoImpl extends BaseDao implements GoodsDao {

    private static final String BASE_SELECT =
            "SELECT g.*, c.name AS category_name, u.nickname AS publisher_name " +
            "FROM goods g " +
            "LEFT JOIN category c ON g.category_id = c.id " +
            "LEFT JOIN `user` u ON g.user_id = u.id ";

    private final RowMapper<Goods> goodsMapper = rs -> {
        Goods g = new Goods();
        g.setId(rs.getInt("id"));
        g.setTitle(rs.getString("title"));
        g.setCategoryId(rs.getInt("category_id"));
        g.setPrice(rs.getDouble("price"));
        g.setQuality(rs.getString("quality"));
        g.setDescription(rs.getString("description"));
        g.setImage(rs.getString("image"));
        g.setTradePlace(rs.getString("trade_place"));
        g.setContact(rs.getString("contact"));
        g.setStatus(rs.getInt("status"));
        g.setUserId(rs.getInt("user_id"));
        g.setViewCount(rs.getInt("view_count"));
        g.setCreateTime(rs.getTimestamp("create_time"));
        g.setCategoryName(rs.getString("category_name"));
        g.setPublisherName(rs.getString("publisher_name"));
        return g;
    };

    /** 拼接公共查询条件 */
    private void appendConditions(StringBuilder sql, List<Object> params, String keyword,
                                  Integer categoryId, Integer status, Integer userId) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (g.title LIKE ? OR g.description LIKE ?)");
            String k = "%" + keyword.trim() + "%";
            params.add(k);
            params.add(k);
        }
        if (categoryId != null) {
            sql.append(" AND g.category_id = ?");
            params.add(categoryId);
        }
        if (status != null) {
            sql.append(" AND g.status = ?");
            params.add(status);
        }
        if (userId != null) {
            sql.append(" AND g.user_id = ?");
            params.add(userId);
        }
    }

    @Override
    public List<Goods> findByPage(String keyword, Integer categoryId, Integer status,
                                  Integer userId, int offset, int limit) {
        StringBuilder sql = new StringBuilder(BASE_SELECT).append(" WHERE 1 = 1");
        List<Object> params = new ArrayList<>();
        appendConditions(sql, params, keyword, categoryId, status, userId);
        sql.append(" ORDER BY g.create_time DESC LIMIT ?, ?");
        params.add(offset);
        params.add(limit);
        return queryList(sql.toString(), goodsMapper, params.toArray());
    }

    @Override
    public int count(String keyword, Integer categoryId, Integer status, Integer userId) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM goods g WHERE 1 = 1");
        List<Object> params = new ArrayList<>();
        appendConditions(sql, params, keyword, categoryId, status, userId);
        return count(sql.toString(), params.toArray());
    }

    @Override
    public List<Goods> findLatest(int limit) {
        String sql = BASE_SELECT + " WHERE g.status = 1 ORDER BY g.create_time DESC LIMIT ?";
        return queryList(sql, goodsMapper, limit);
    }

    @Override
    public Goods findById(Integer id) {
        String sql = BASE_SELECT + " WHERE g.id = ?";
        return queryOne(sql, goodsMapper, id);
    }

    @Override
    public int insert(Goods g) {
        String sql = "INSERT INTO goods(title, category_id, price, quality, description, image, " +
                "trade_place, contact, status, user_id) VALUES(?,?,?,?,?,?,?,?,?,?)";
        return update(sql, g.getTitle(), g.getCategoryId(), g.getPrice(), g.getQuality(),
                g.getDescription(), g.getImage(), g.getTradePlace(), g.getContact(),
                g.getStatus(), g.getUserId());
    }

    @Override
    public int update(Goods g) {
        String sql = "UPDATE goods SET title = ?, category_id = ?, price = ?, quality = ?, " +
                "description = ?, image = ?, trade_place = ?, contact = ?, status = ? WHERE id = ?";
        return update(sql, g.getTitle(), g.getCategoryId(), g.getPrice(), g.getQuality(),
                g.getDescription(), g.getImage(), g.getTradePlace(), g.getContact(),
                g.getStatus(), g.getId());
    }

    @Override
    public int updateStatus(Integer id, Integer status) {
        return update("UPDATE goods SET status = ? WHERE id = ?", status, id);
    }

    @Override
    public int delete(Integer id) {
        return update("DELETE FROM goods WHERE id = ?", id);
    }

    @Override
    public int increaseView(Integer id) {
        return update("UPDATE goods SET view_count = view_count + 1 WHERE id = ?", id);
    }

    @Override
    public int countTotal() {
        return count("SELECT COUNT(*) FROM goods");
    }
}
