package com.campus.service;

import com.campus.entity.Goods;
import com.campus.util.PageBean;

import java.util.List;

/**
 * 二手商品业务接口。
 */
public interface GoodsService {

    /** 多条件分页查询 */
    PageBean<Goods> findByPage(String keyword, Integer categoryId, Integer status,
                               Integer userId, int pageNum, int pageSize);

    /** 最新在售商品（首页） */
    List<Goods> findLatest(int limit);

    /** 按主键查询 */
    Goods findById(Integer id);

    /** 查看详情：浏览量 +1 并返回商品 */
    Goods viewDetail(Integer id);

    /** 发布商品 */
    void publish(Goods goods);

    /** 修改商品 */
    void modify(Goods goods);

    /** 修改状态（在售 / 已售 / 下架） */
    void updateStatus(Integer id, Integer status);

    /** 删除商品 */
    void delete(Integer id);

    /** 商品总数（后台统计） */
    int countTotal();
}
