package com.campus.dao.impl;

import com.campus.dao.AiConfigDao;
import com.campus.dao.BaseDao;
import com.campus.dao.RowMapper;
import com.campus.entity.AiConfig;

/**
 * AI 全局配置 DAO 实现类。
 * 全站只有一行配置，find 取 id 最小的那条。
 */
public class AiConfigDaoImpl extends BaseDao implements AiConfigDao {

    private final RowMapper<AiConfig> mapper = rs -> {
        AiConfig c = new AiConfig();
        c.setId(rs.getInt("id"));
        c.setEnabled(rs.getInt("enabled"));
        c.setTopN(rs.getInt("top_n"));
        c.setUpdateTime(rs.getTimestamp("update_time"));
        return c;
    };

    @Override
    public AiConfig find() {
        return queryOne("SELECT * FROM ai_config ORDER BY id LIMIT 1", mapper);
    }

    @Override
    public int insert(AiConfig c) {
        return update("INSERT INTO ai_config(enabled, top_n) VALUES (?, ?)",
                c.getEnabled(), c.getTopN());
    }

    @Override
    public int update(AiConfig c) {
        return update("UPDATE ai_config SET enabled = ?, top_n = ? WHERE id = ?",
                c.getEnabled(), c.getTopN(), c.getId());
    }
}
