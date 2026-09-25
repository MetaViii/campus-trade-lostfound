package com.campus.service;

import com.campus.entity.AiConfig;
import com.campus.entity.AiLog;
import com.campus.entity.AiProvider;
import com.campus.entity.AiSearchResult;
import com.campus.entity.User;
import com.campus.util.PageBean;

import java.util.List;

/**
 * AI 智能匹配服务接口。
 *
 * <p>用户用一句话描述想要找的东西，服务把描述转成向量，
 * 与库中已缓存条目的向量算余弦相似度，取最相似的若干条；
 * 若配置了对话模型，再让它据此生成一段自然语言答复。</p>
 *
 * <p>服务商配置分两类各自管理：向量模型与对话模型，
 * 每类下可以配多条，其中一条为「当前使用中」。</p>
 */
public interface AiService {

    /** 模块：失物招领 */
    int MODULE_LOST_FOUND = 1;
    /** 模块：二手商品 */
    int MODULE_GOODS = 2;

    // ------------------------------------------------------------------
    // 全局设置
    // ------------------------------------------------------------------

    /** 读取全局配置（永远不返回 null） */
    AiConfig getConfig();

    /** 保存全局设置（开关、返回条数）。开启时会校验向量模型是否可用 */
    void saveBasicConfig(AiConfig config);

    // ------------------------------------------------------------------
    // 服务商配置
    // ------------------------------------------------------------------

    /** 查询某类（向量 / 对话）下的全部配置 */
    List<AiProvider> findProviders(int kind);

    /** 按主键查询，不存在返回 null */
    AiProvider findProvider(int id);

    /** 新增或修改一条服务商配置；API Key 留空表示沿用原值 */
    void saveProvider(AiProvider provider);

    /** 删除一条配置；若删的是正在使用的那条，自动启用同类的下一条 */
    void deleteProvider(int id);

    /** 把指定配置设为该类下「当前使用中」 */
    void activateProvider(int id);

    /** 测试某条配置的接口连通性，返回给管理员看的提示文字 */
    String testProvider(AiProvider provider);

    // ------------------------------------------------------------------
    // 可用性
    // ------------------------------------------------------------------

    /** AI 功能当前是否真正可用 */
    boolean isReady();

    /**
     * 不可用的原因；可用时返回空串。
     * 页面用它告诉管理员到底缺什么，避免「保存成功却用不了」。
     */
    String unavailableReason();

    // ------------------------------------------------------------------
    // 检索与日志
    // ------------------------------------------------------------------

    /**
     * 执行一次 AI 匹配检索，并记录日志。
     * 检索失败时同样会写一条失败日志，然后把异常抛给上层。
     *
     * @param module 1=失物招领 2=二手商品
     * @param query  用户输入的描述
     * @param user   提问用户
     */
    AiSearchResult search(int module, String query, User user);

    /** 分页查询 AI 对话日志 */
    PageBean<AiLog> findLogs(Integer module, String keyword, int pageNum, int pageSize);

    /**
     * 重建某个模块的向量索引（清空后重新逐条计算）。
     * @return 处理成功的条数
     */
    int rebuildIndex(int module);

    /** 某个模块已建立索引的条数 */
    int cachedCount(int module);

    /** 某个模块参与匹配的条目总数 */
    int recordCount(int module);
}
