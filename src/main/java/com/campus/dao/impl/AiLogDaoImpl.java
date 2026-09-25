package com.campus.dao.impl;

import com.campus.dao.AiLogDao;
import com.campus.dao.BaseDao;
import com.campus.dao.RowMapper;
import com.campus.entity.AiLog;

import java.util.ArrayList;
import java.util.List;

/**
 * AI 对话日志 DAO 实现类。
 * 按时间倒序分页，支持按模块与关键字筛选，并带出提问用户昵称。
 */
public class AiLogDaoImpl extends BaseDao implements AiLogDao {

    private static final String BASE_SELECT =
            "SELECT l.*, u.nickname AS user_nickname FROM ai_log l "
                    + "LEFT JOIN `user` u ON l.user_id = u.id ";

    private final RowMapper<AiLog> mapper = rs -> {
        AiLog log = new AiLog();
        log.setId(rs.getInt("id"));
        log.setUserId(rs.getInt("user_id"));
        log.setModule(rs.getInt("module"));
        log.setQueryText(rs.getString("query_text"));
        log.setReplyText(rs.getString("reply_text"));
        log.setResultIds(rs.getString("result_ids"));
        log.setResultCount(rs.getInt("result_count"));
        log.setCostMs(rs.getInt("cost_ms"));
        log.setStatus(rs.getInt("status"));
        log.setErrorMsg(rs.getString("error_msg"));
        log.setCreateTime(rs.getTimestamp("create_time"));
        log.setUserNickname(rs.getString("user_nickname"));
        return log;
    };

    /** 拼接公共查询条件 */
    private void appendConditions(StringBuilder sql, List<Object> params,
                                  Integer module, String keyword) {
        if (module != null) {
            sql.append(" AND l.module = ?");
            params.add(module);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (l.query_text LIKE ? OR l.reply_text LIKE ?)");
            String k = "%" + keyword.trim() + "%";
            params.add(k);
            params.add(k);
        }
    }

    @Override
    public List<AiLog> findByPage(Integer module, String keyword, int offset, int limit) {
        StringBuilder sql = new StringBuilder(BASE_SELECT).append(" WHERE 1 = 1");
        List<Object> params = new ArrayList<>();
        appendConditions(sql, params, module, keyword);
        sql.append(" ORDER BY l.create_time DESC, l.id DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);
        return queryList(sql.toString(), mapper, params.toArray());
    }

    @Override
    public int count(Integer module, String keyword) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM ai_log l WHERE 1 = 1");
        List<Object> params = new ArrayList<>();
        appendConditions(sql, params, module, keyword);
        return count(sql.toString(), params.toArray());
    }

    @Override
    public int insert(AiLog log) {
        return update("INSERT INTO ai_log(user_id, module, query_text, reply_text, result_ids, "
                        + "result_count, cost_ms, status, error_msg) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                log.getUserId(), log.getModule(), log.getQueryText(), log.getReplyText(),
                log.getResultIds(), log.getResultCount(), log.getCostMs(),
                log.getStatus(), log.getErrorMsg());
    }

    @Override
    public AiLog findById(Integer id) {
        return queryOne(BASE_SELECT + " WHERE l.id = ?", mapper, id);
    }

    @Override
    public int countTotal() {
        return count("SELECT COUNT(*) FROM ai_log");
    }
}
