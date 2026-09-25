package com.campus.dao;

import com.campus.entity.AiProvider;

import java.util.List;

/**
 * AI 服务商配置数据访问接口。
 */
public interface AiProviderDao {

    /** 查询某个类型（向量 / 对话）下的全部配置，使用中的排在最前 */
    List<AiProvider> findByKind(Integer kind);

    /** 按主键查询 */
    AiProvider findById(Integer id);

    /** 查询某个类型下当前使用中的那条，没有则返回 null */
    AiProvider findActive(Integer kind);

    /** 新增 */
    int insert(AiProvider provider);

    /** 修改 */
    int update(AiProvider provider);

    /** 删除 */
    int delete(Integer id);

    /** 把某个类型下的启用状态全部清掉 */
    int clearActive(Integer kind);

    /** 把指定的一条置为启用 */
    int setActive(Integer id);

    /** 某个类型下的配置条数 */
    int countByKind(Integer kind);
}
