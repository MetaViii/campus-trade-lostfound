package com.campus.dao;

import com.campus.entity.AiLog;

import java.util.List;

/**
 * AI 对话日志数据访问接口。
 */
public interface AiLogDao {

    /**
     * 分页查询对话日志，按时间倒序。
     * 各条件为 null 时表示不限制该条件。
     */
    List<AiLog> findByPage(Integer module, String keyword, int offset, int limit);

    /** 统计多条件下的日志总数 */
    int count(Integer module, String keyword);

    /** 新增日志 */
    int insert(AiLog log);

    /** 按主键查询（含提问用户昵称） */
    AiLog findById(Integer id);

    /** 日志总数（后台统计） */
    int countTotal();
}
