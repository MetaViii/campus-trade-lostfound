package com.campus.dao.impl;

import com.campus.dao.AiEmbeddingDao;
import com.campus.dao.BaseDao;
import com.campus.dao.RowMapper;
import com.campus.entity.AiEmbedding;

import java.util.List;

/**
 * 条目向量缓存 DAO 实现类。
 * save 用 INSERT ... ON DUPLICATE KEY UPDATE，以 (entity_type, entity_id) 唯一键实现「有则覆盖」。
 */
public class AiEmbeddingDaoImpl extends BaseDao implements AiEmbeddingDao {

    private final RowMapper<AiEmbedding> mapper = rs -> {
        AiEmbedding e = new AiEmbedding();
        e.setId(rs.getInt("id"));
        e.setEntityType(rs.getInt("entity_type"));
        e.setEntityId(rs.getInt("entity_id"));
        e.setModel(rs.getString("model"));
        e.setDim(rs.getInt("dim"));
        e.setVec(rs.getString("vec"));
        e.setTextHash(rs.getString("text_hash"));
        e.setUpdateTime(rs.getTimestamp("update_time"));
        return e;
    };

    @Override
    public AiEmbedding findByEntity(Integer entityType, Integer entityId) {
        return queryOne("SELECT * FROM ai_embedding WHERE entity_type = ? AND entity_id = ?",
                mapper, entityType, entityId);
    }

    @Override
    public List<AiEmbedding> findByType(Integer entityType) {
        return queryList("SELECT * FROM ai_embedding WHERE entity_type = ?", mapper, entityType);
    }

    @Override
    public int save(AiEmbedding e) {
        return update("INSERT INTO ai_embedding(entity_type, entity_id, model, dim, vec, text_hash) "
                        + "VALUES (?, ?, ?, ?, ?, ?) "
                        + "ON DUPLICATE KEY UPDATE model = VALUES(model), dim = VALUES(dim), "
                        + "vec = VALUES(vec), text_hash = VALUES(text_hash), "
                        + "update_time = CURRENT_TIMESTAMP",
                e.getEntityType(), e.getEntityId(), e.getModel(),
                e.getDim(), e.getVec(), e.getTextHash());
    }

    @Override
    public int deleteByType(Integer entityType) {
        return update("DELETE FROM ai_embedding WHERE entity_type = ?", entityType);
    }

    @Override
    public int countByType(Integer entityType) {
        return count("SELECT COUNT(*) FROM ai_embedding WHERE entity_type = ?", entityType);
    }
}
