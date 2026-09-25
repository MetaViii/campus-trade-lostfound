package com.campus.dao.impl;

import com.campus.dao.AiProviderDao;
import com.campus.dao.BaseDao;
import com.campus.dao.RowMapper;
import com.campus.entity.AiProvider;

import java.util.List;

/**
 * AI 服务商配置 DAO 实现类。
 */
public class AiProviderDaoImpl extends BaseDao implements AiProviderDao {

    private final RowMapper<AiProvider> mapper = rs -> {
        AiProvider p = new AiProvider();
        p.setId(rs.getInt("id"));
        p.setKind(rs.getInt("kind"));
        p.setName(rs.getString("name"));
        p.setBaseUrl(rs.getString("base_url"));
        p.setApiKey(rs.getString("api_key"));
        p.setModel(rs.getString("model"));
        p.setIsActive(rs.getInt("is_active"));
        p.setCreateTime(rs.getTimestamp("create_time"));
        p.setUpdateTime(rs.getTimestamp("update_time"));
        return p;
    };

    @Override
    public List<AiProvider> findByKind(Integer kind) {
        return queryList("SELECT * FROM ai_provider WHERE kind = ? "
                + "ORDER BY is_active DESC, id ASC", mapper, kind);
    }

    @Override
    public AiProvider findById(Integer id) {
        return queryOne("SELECT * FROM ai_provider WHERE id = ?", mapper, id);
    }

    @Override
    public AiProvider findActive(Integer kind) {
        return queryOne("SELECT * FROM ai_provider WHERE kind = ? AND is_active = 1 "
                + "ORDER BY id LIMIT 1", mapper, kind);
    }

    @Override
    public int insert(AiProvider p) {
        return update("INSERT INTO ai_provider(kind, name, base_url, api_key, model, is_active) "
                        + "VALUES (?, ?, ?, ?, ?, ?)",
                p.getKind(), p.getName(), p.getBaseUrl(), p.getApiKey(),
                p.getModel(), p.getIsActive());
    }

    @Override
    public int update(AiProvider p) {
        return update("UPDATE ai_provider SET name = ?, base_url = ?, api_key = ?, model = ? "
                        + "WHERE id = ?",
                p.getName(), p.getBaseUrl(), p.getApiKey(), p.getModel(), p.getId());
    }

    @Override
    public int delete(Integer id) {
        return update("DELETE FROM ai_provider WHERE id = ?", id);
    }

    @Override
    public int clearActive(Integer kind) {
        return update("UPDATE ai_provider SET is_active = 0 WHERE kind = ?", kind);
    }

    @Override
    public int setActive(Integer id) {
        return update("UPDATE ai_provider SET is_active = 1 WHERE id = ?", id);
    }

    @Override
    public int countByKind(Integer kind) {
        return count("SELECT COUNT(*) FROM ai_provider WHERE kind = ?", kind);
    }
}
