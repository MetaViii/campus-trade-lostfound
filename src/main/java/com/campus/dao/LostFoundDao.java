package com.campus.dao;

import com.campus.entity.LostFound;

import java.util.List;

/**
 * 失物招领数据访问接口。
 */
public interface LostFoundDao {

    /**
     * 多条件分页查询失物招领信息。
     * 各条件为 null 时表示不限制该条件。
     */
    List<LostFound> findByPage(String keyword, Integer type, Integer categoryId,
                               Integer status, Integer userId, int offset, int limit);

    /** 统计多条件下的信息总数 */
    int count(String keyword, Integer type, Integer categoryId, Integer status, Integer userId);

    /** 查询最新的若干条信息（首页展示） */
    List<LostFound> findLatest(int limit);

    /**
     * 查询指定类型、且仍可匹配（状态为寻找中 / 待认领）的信息，作为智能匹配的候选集。
     * @param type 1=失物，2=拾物
     */
    List<LostFound> findCandidates(Integer type);

    /** 按主键查询（含分类名、发布者昵称） */
    LostFound findById(Integer id);

    /** 新增 */
    int insert(LostFound lostFound);

    /** 修改 */
    int update(LostFound lostFound);

    /** 修改状态 */
    int updateStatus(Integer id, Integer status);

    /** 删除 */
    int delete(Integer id);

    /** 浏览量 +1 */
    int increaseView(Integer id);

    /** 信息总数（后台统计） */
    int countTotal();
}
