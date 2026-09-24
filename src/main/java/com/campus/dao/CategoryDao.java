package com.campus.dao;

import com.campus.entity.Category;

import java.util.List;

/**
 * 分类数据访问接口。
 */
public interface CategoryDao {

    /** 查询某类型下所有启用的分类（type：1商品 2失物招领） */
    List<Category> findEnabledByType(Integer type);

    /** 查询某类型下全部分类（后台用） */
    List<Category> findAllByType(Integer type);

    /** 查询全部分类 */
    List<Category> findAll();

    /** 按主键查询 */
    Category findById(Integer id);

    /** 新增分类 */
    int insert(Category category);

    /** 修改分类 */
    int update(Category category);

    /** 修改启用状态 */
    int updateStatus(Integer id, Integer status);

    /** 删除分类 */
    int delete(Integer id);
}
