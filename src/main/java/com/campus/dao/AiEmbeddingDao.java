package com.campus.dao;

import com.campus.entity.AiEmbedding;

import java.util.List;

/**
 * 条目向量缓存数据访问接口。
 */
public interface AiEmbeddingDao {

    /** 按条目查询已缓存的向量，没有则返回 null */
    AiEmbedding findByEntity(Integer entityType, Integer entityId);

    /** 查询某个模块下全部已缓存的向量 */
    List<AiEmbedding> findByType(Integer entityType);

    /**
     * 写入或覆盖某条目的向量。
     * 以 (entity_type, entity_id) 为唯一键，存在即更新。
     */
    int save(AiEmbedding embedding);

    /** 删除某个模块的全部缓存（换向量模型后重建索引用） */
    int deleteByType(Integer entityType);

    /** 统计某个模块已缓存的条数 */
    int countByType(Integer entityType);
}
