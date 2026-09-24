package com.campus.dao;

import com.campus.entity.Goods;

import java.util.List;

/**
 * 二手商品数据访问接口。
 */
public interface GoodsDao {

    /**
     * 多条件分页查询商品。
     * 各条件为 null 时表示不限制该条件。
     */
    List<Goods> findByPage(String keyword, Integer categoryId, Integer status,
                           Integer userId, int offset, int limit);

    /** 统计多条件下的商品总数 */
    int count(String keyword, Integer categoryId, Integer status, Integer userId);

    /** 查询最新的若干在售商品（首页展示） */
    List<Goods> findLatest(int limit);

    /** 按主键查询商品（含分类名、发布者昵称） */
    Goods findById(Integer id);

    /** 新增商品 */
    int insert(Goods goods);

    /** 修改商品 */
    int update(Goods goods);

    /** 修改商品状态 */
    int updateStatus(Integer id, Integer status);

    /** 删除商品 */
    int delete(Integer id);

    /** 浏览量 +1 */
    int increaseView(Integer id);

    /** 商品总数（后台统计） */
    int countTotal();
}
