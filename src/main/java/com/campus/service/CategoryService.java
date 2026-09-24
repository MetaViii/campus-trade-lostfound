package com.campus.service;

import com.campus.entity.Category;

import java.util.List;

/**
 * 分类业务接口。
 */
public interface CategoryService {

    /** 某类型下启用的分类（前台下拉框、筛选用） */
    List<Category> findEnabledByType(Integer type);

    /** 某类型下全部分类（后台管理） */
    List<Category> findAllByType(Integer type);

    /** 全部分类 */
    List<Category> findAll();

    /** 按主键查询 */
    Category findById(Integer id);

    /** 新增分类 */
    void add(String name, Integer type, Integer status);

    /** 修改分类 */
    void modify(Integer id, String name, Integer type, Integer status);

    /** 修改启用状态 */
    void updateStatus(Integer id, Integer status);

    /** 删除分类 */
    void delete(Integer id);
}
