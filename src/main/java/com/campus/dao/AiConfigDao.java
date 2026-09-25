package com.campus.dao;

import com.campus.entity.AiConfig;

/**
 * AI 服务配置数据访问接口。
 */
public interface AiConfigDao {

    /** 读取配置（全站只有一行） */
    AiConfig find();

    /** 新增配置行 */
    int insert(AiConfig config);

    /** 更新配置行 */
    int update(AiConfig config);
}
