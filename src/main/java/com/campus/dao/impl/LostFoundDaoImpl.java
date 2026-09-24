package com.campus.dao.impl;

import com.campus.dao.BaseDao;
import com.campus.dao.LostFoundDao;
import com.campus.dao.RowMapper;
import com.campus.entity.LostFound;

import java.util.ArrayList;
import java.util.List;

/**
 * 失物招领 DAO 实现类。
 * 列表查询采用动态 SQL 拼接，支持类型、关键字、分类、状态、发布者多条件组合。
 */
public class LostFoundDaoImpl extends BaseDao implements LostFoundDao {

    private static final String BASE_SELECT =
            "SELECT lf.*, c.name AS category_name, u.nickname AS publisher_name " +
            "FROM lost_found lf " +
            "LEFT JOIN category c ON lf.category_id = c.id " +
            "LEFT JOIN `user` u ON lf.user_id = u.id ";

    private final RowMapper<LostFound> mapper = rs -> {
        LostFound lf = new LostFound();
        lf.setId(rs.getInt("id"));
        lf.setType(rs.getInt("type"));
        lf.setName(rs.getString("name"));
        lf.setCategoryId(rs.getInt("category_id"));
        lf.setPlace(rs.getString("place"));
        lf.setHappenTime(rs.getTimestamp("happen_time"));
        lf.setFeature(rs.getString("feature"));
        lf.setContact(rs.getString("contact"));
        lf.setImage(rs.getString("image"));
        lf.setStatus(rs.getInt("status"));
        lf.setUserId(rs.getInt("user_id"));
        lf.setViewCount(rs.getInt("view_count"));
        lf.setCreateTime(rs.getTimestamp("create_time"));
        lf.setCategoryName(rs.getString("category_name"));
        lf.setPublisherName(rs.getString("publisher_name"));
        return lf;
    };

    private void appendConditions(StringBuilder sql, List<Object> params, String keyword,
                                  Integer type, Integer categoryId, Integer status, Integer userId) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (lf.name LIKE ? OR lf.feature LIKE ?)");
            String k = "%" + keyword.trim() + "%";
            params.add(k);
            params.add(k);
        }
        if (type != null) {
            sql.append(" AND lf.type = ?");
            params.add(type);
        }
        if (categoryId != null) {
            sql.append(" AND lf.category_id = ?");
            params.add(categoryId);
        }
        if (status != null) {
            sql.append(" AND lf.status = ?");
            params.add(status);
        }
        if (userId != null) {
            sql.append(" AND lf.user_id = ?");
            params.add(userId);
        }
    }

    @Override
    public List<LostFound> findByPage(String keyword, Integer type, Integer categoryId,
                                      Integer status, Integer userId, int offset, int limit) {
        StringBuilder sql = new StringBuilder(BASE_SELECT).append(" WHERE 1 = 1");
        List<Object> params = new ArrayList<>();
        appendConditions(sql, params, keyword, type, categoryId, status, userId);
        sql.append(" ORDER BY lf.create_time DESC LIMIT ?, ?");
        params.add(offset);
        params.add(limit);
        return queryList(sql.toString(), mapper, params.toArray());
    }

    @Override
    public int count(String keyword, Integer type, Integer categoryId, Integer status, Integer userId) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM lost_found lf WHERE 1 = 1");
        List<Object> params = new ArrayList<>();
        appendConditions(sql, params, keyword, type, categoryId, status, userId);
        return count(sql.toString(), params.toArray());
    }

    @Override
    public List<LostFound> findLatest(int limit) {
        String sql = BASE_SELECT + " WHERE lf.status <> 0 ORDER BY lf.create_time DESC LIMIT ?";
        return queryList(sql, mapper, limit);
    }

    @Override
    public List<LostFound> findCandidates(Integer type) {
        // 仅取仍可匹配的信息：失物“寻找中”(1) 或 拾物“待认领”(3)
        String sql = BASE_SELECT + " WHERE lf.type = ? AND lf.status IN (1, 3) ORDER BY lf.create_time DESC";
        return queryList(sql, mapper, type);
    }

    @Override
    public LostFound findById(Integer id) {
        String sql = BASE_SELECT + " WHERE lf.id = ?";
        return queryOne(sql, mapper, id);
    }

    @Override
    public int insert(LostFound lf) {
        String sql = "INSERT INTO lost_found(type, name, category_id, place, happen_time, feature, " +
                "contact, image, status, user_id) VALUES(?,?,?,?,?,?,?,?,?,?)";
        return update(sql, lf.getType(), lf.getName(), lf.getCategoryId(), lf.getPlace(),
                lf.getHappenTime(), lf.getFeature(), lf.getContact(), lf.getImage(),
                lf.getStatus(), lf.getUserId());
    }

    @Override
    public int update(LostFound lf) {
        String sql = "UPDATE lost_found SET type = ?, name = ?, category_id = ?, place = ?, " +
                "happen_time = ?, feature = ?, contact = ?, image = ?, status = ? WHERE id = ?";
        return update(sql, lf.getType(), lf.getName(), lf.getCategoryId(), lf.getPlace(),
                lf.getHappenTime(), lf.getFeature(), lf.getContact(), lf.getImage(),
                lf.getStatus(), lf.getId());
    }

    @Override
    public int updateStatus(Integer id, Integer status) {
        return update("UPDATE lost_found SET status = ? WHERE id = ?", status, id);
    }

    @Override
    public int delete(Integer id) {
        return update("DELETE FROM lost_found WHERE id = ?", id);
    }

    @Override
    public int increaseView(Integer id) {
        return update("UPDATE lost_found SET view_count = view_count + 1 WHERE id = ?", id);
    }

    @Override
    public int countTotal() {
        return count("SELECT COUNT(*) FROM lost_found");
    }
}
