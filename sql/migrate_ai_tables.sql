-- =====================================================================
--  增量迁移脚本：补齐 AI 相关数据表
--  适用场景：数据库里已有 user / category / goods / lost_found / message，
--            但缺少 ai_config / ai_provider / ai_log / ai_embedding
--            （旧版 schema.sql 建库后才加入 AI 模块，导致 AI 页面报 500）。
--  特点：不删库、不删表、不清空数据，可重复执行。
--  用法：mysql -uroot -p < sql/migrate_ai_tables.sql
-- =====================================================================

USE campus_system;

-- ---------------------------------------------------------------------
-- 6. AI 全局配置表 ai_config（只保留一行，id=1）
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `ai_config` (
    id          INT          NOT NULL AUTO_INCREMENT COMMENT '配置编号',
    enabled     TINYINT      NOT NULL DEFAULT 0 COMMENT 'AI功能开关:1开启 0关闭',
    top_n       INT          NOT NULL DEFAULT 5 COMMENT '每次返回的匹配条数',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
                             ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 全局配置表';

-- 默认配置行：AI 关闭，每次返回 5 条
INSERT INTO `ai_config` (id, enabled, top_n)
SELECT 1, 0, 5 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `ai_config`);

-- ---------------------------------------------------------------------
-- 7. AI 服务商配置表 ai_provider
--    kind: 1=向量模型  2=对话模型    is_active: 1=当前使用中
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `ai_provider` (
    id          INT          NOT NULL AUTO_INCREMENT COMMENT '配置编号',
    kind        TINYINT      NOT NULL COMMENT '类型:1向量模型 2对话模型',
    name        VARCHAR(50)  NOT NULL COMMENT '配置名称，如「硅基流动」',
    base_url    VARCHAR(255) NOT NULL DEFAULT '' COMMENT '接口地址',
    api_key     VARCHAR(255) NOT NULL DEFAULT '' COMMENT 'API Key',
    model       VARCHAR(100) NOT NULL DEFAULT '' COMMENT '模型名',
    is_active   TINYINT      NOT NULL DEFAULT 0 COMMENT '1=当前使用中',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
                             ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (id),
    KEY idx_provider_kind (kind, is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 服务商配置表';

-- 预置两条示例配置（Key 留空，到后台补填），仅在表为空时写入
INSERT INTO `ai_provider` (kind, name, base_url, api_key, model, is_active)
SELECT * FROM (
    SELECT 1 AS kind, '硅基流动' AS name, 'https://api.siliconflow.cn/v1' AS base_url,
           '' AS api_key, 'BAAI/bge-m3' AS model, 1 AS is_active
    UNION ALL
    SELECT 2, 'DeepSeek', 'https://api.deepseek.com/v1', '', 'deepseek-chat', 1
) AS seed
WHERE NOT EXISTS (SELECT 1 FROM `ai_provider`);

-- ---------------------------------------------------------------------
-- 8. AI 对话日志表 ai_log
--    module: 1=失物招领 2=二手商品     status: 1=成功 0=失败
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `ai_log` (
    id           INT          NOT NULL AUTO_INCREMENT COMMENT '日志编号',
    user_id      INT          NOT NULL COMMENT '提问用户编号',
    module       TINYINT      NOT NULL COMMENT '模块:1失物招领 2二手商品',
    query_text   VARCHAR(500) NOT NULL COMMENT '用户输入的描述',
    reply_text   TEXT         NULL COMMENT 'AI 生成的答复',
    result_ids   VARCHAR(500) NULL COMMENT '命中的记录编号，逗号分隔',
    result_count INT          NOT NULL DEFAULT 0 COMMENT '命中条数',
    cost_ms      INT          NOT NULL DEFAULT 0 COMMENT '耗时毫秒',
    status       TINYINT      NOT NULL DEFAULT 1 COMMENT '状态:1成功 0失败',
    error_msg    VARCHAR(500) NULL COMMENT '失败原因',
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提问时间',
    PRIMARY KEY (id),
    KEY idx_ailog_user (user_id),
    KEY idx_ailog_time (create_time),
    CONSTRAINT fk_ailog_user FOREIGN KEY (user_id) REFERENCES `user` (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 对话日志表';

-- ---------------------------------------------------------------------
-- 9. 条目向量缓存表 ai_embedding
--    entity_type: 1=失物招领 2=二手商品
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `ai_embedding` (
    id          INT          NOT NULL AUTO_INCREMENT COMMENT '编号',
    entity_type TINYINT      NOT NULL COMMENT '条目类型:1失物招领 2二手商品',
    entity_id   INT          NOT NULL COMMENT '条目编号',
    model       VARCHAR(100) NOT NULL COMMENT '生成向量的模型名',
    dim         INT          NOT NULL COMMENT '向量维度',
    vec         MEDIUMTEXT   NOT NULL COMMENT '向量，JSON 数组格式',
    text_hash   CHAR(32)     NOT NULL COMMENT '生成向量时文本的 MD5',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
                             ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_ai_emb (entity_type, entity_id),
    KEY idx_ai_emb_model (model)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 向量缓存表';

-- 迁移结果自检：以下 4 行应全部输出
SELECT table_name AS `已就绪的 AI 表`
FROM information_schema.tables
WHERE table_schema = DATABASE() AND table_name LIKE 'ai\_%'
ORDER BY table_name;
