package com.campus.service;

import com.campus.entity.LostFound;
import com.campus.entity.MatchResult;
import com.campus.util.PageBean;

import java.util.List;

/**
 * 失物招领业务接口。
 */
public interface LostFoundService {

    /** 多条件分页查询 */
    PageBean<LostFound> findByPage(String keyword, Integer type, Integer categoryId,
                                   Integer status, Integer userId, int pageNum, int pageSize);

    /** 最新信息（首页） */
    List<LostFound> findLatest(int limit);

    /** 按主键查询 */
    LostFound findById(Integer id);

    /** 查看详情：浏览量 +1 并返回信息 */
    LostFound viewDetail(Integer id);

    /** 发布失物 / 招领信息 */
    void publish(LostFound lostFound);

    /** 修改信息 */
    void modify(LostFound lostFound);

    /** 修改状态 */
    void updateStatus(Integer id, Integer status);

    /** 删除信息 */
    void delete(Integer id);

    /** 信息总数（后台统计） */
    int countTotal();

    /**
     * 智能匹配：为指定失物/拾物信息，从相反类型的可匹配信息中
     * 按相似度算法找出最可能对应的若干条结果（按得分降序）。
     *
     * @param id   源信息编号
     * @param topN 返回的最大条数
     * @return 相似度达到阈值的匹配结果列表，可能为空
     */
    List<MatchResult> smartMatch(Integer id, int topN);
}
