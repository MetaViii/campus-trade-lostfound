package com.campus.dao.impl;

import com.campus.dao.BaseDao;
import com.campus.dao.MessageDao;
import com.campus.dao.RowMapper;
import com.campus.entity.Message;

import java.util.List;

/**
 * 留言 DAO 实现类。
 */
public class MessageDaoImpl extends BaseDao implements MessageDao {

    private final RowMapper<Message> messageMapper = rs -> {
        Message m = new Message();
        m.setId(rs.getInt("id"));
        m.setTargetType(rs.getInt("target_type"));
        m.setTargetId(rs.getInt("target_id"));
        m.setUserId(rs.getInt("user_id"));
        m.setContent(rs.getString("content"));
        m.setCreateTime(rs.getTimestamp("create_time"));
        m.setUserNickname(rs.getString("user_nickname"));
        return m;
    };

    @Override
    public List<Message> findByTarget(Integer targetType, Integer targetId) {
        String sql = "SELECT m.*, u.nickname AS user_nickname " +
                "FROM message m LEFT JOIN `user` u ON m.user_id = u.id " +
                "WHERE m.target_type = ? AND m.target_id = ? " +
                "ORDER BY m.create_time DESC";
        return queryList(sql, messageMapper, targetType, targetId);
    }

    @Override
    public Message findById(Integer id) {
        String sql = "SELECT m.*, u.nickname AS user_nickname FROM message m " +
                "LEFT JOIN `user` u ON m.user_id = u.id WHERE m.id = ?";
        return queryOne(sql, messageMapper, id);
    }

    @Override
    public int insert(Message message) {
        String sql = "INSERT INTO message(target_type, target_id, user_id, content) VALUES(?,?,?,?)";
        return update(sql, message.getTargetType(), message.getTargetId(),
                message.getUserId(), message.getContent());
    }

    @Override
    public int delete(Integer id) {
        String sql = "DELETE FROM message WHERE id = ?";
        return update(sql, id);
    }
}
