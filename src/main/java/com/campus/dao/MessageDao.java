package com.campus.dao;

import com.campus.entity.Message;

import java.util.List;

/**
 * 留言数据访问接口。
 */
public interface MessageDao {

    /** 查询某条目标信息下的全部留言（含留言人昵称，按时间倒序） */
    List<Message> findByTarget(Integer targetType, Integer targetId);

    /** 按主键查询留言（用于删除权限校验） */
    Message findById(Integer id);

    /** 新增留言 */
    int insert(Message message);

    /** 删除留言 */
    int delete(Integer id);
}
