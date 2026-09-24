package com.campus.service;

import com.campus.entity.Message;

import java.util.List;

/**
 * 留言业务接口。
 */
public interface MessageService {

    /** 查询某条目标信息下的全部留言 */
    List<Message> findByTarget(Integer targetType, Integer targetId);

    /** 按主键查询留言 */
    Message findById(Integer id);

    /** 新增留言 */
    void add(Message message);

    /** 删除留言 */
    void delete(Integer id);
}
