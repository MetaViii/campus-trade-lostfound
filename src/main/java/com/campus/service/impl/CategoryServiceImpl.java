package com.campus.service.impl;

import com.campus.dao.CategoryDao;
import com.campus.dao.impl.CategoryDaoImpl;
import com.campus.entity.Category;
import com.campus.service.CategoryService;
import com.campus.util.ServiceException;

import java.util.List;

/**
 * 分类业务实现类。
 */
public class CategoryServiceImpl implements CategoryService {

    private final CategoryDao categoryDao = new CategoryDaoImpl();

    @Override
    public List<Category> findEnabledByType(Integer type) {
        return categoryDao.findEnabledByType(type);
    }

    @Override
    public List<Category> findAllByType(Integer type) {
        return categoryDao.findAllByType(type);
    }

    @Override
    public List<Category> findAll() {
        return categoryDao.findAll();
    }

    @Override
    public Category findById(Integer id) {
        return categoryDao.findById(id);
    }

    @Override
    public void add(String name, Integer type, Integer status) {
        if (name == null || name.trim().isEmpty()) {
            throw new ServiceException("分类名称不能为空");
        }
        if (type == null || (type != 1 && type != 2)) {
            throw new ServiceException("分类类型不正确");
        }
        Category category = new Category();
        category.setName(name.trim());
        category.setType(type);
        category.setStatus(status == null ? 1 : status);
        categoryDao.insert(category);
    }

    @Override
    public void modify(Integer id, String name, Integer type, Integer status) {
        if (name == null || name.trim().isEmpty()) {
            throw new ServiceException("分类名称不能为空");
        }
        Category category = categoryDao.findById(id);
        if (category == null) {
            throw new ServiceException("分类不存在");
        }
        category.setName(name.trim());
        category.setType(type);
        category.setStatus(status == null ? 1 : status);
        categoryDao.update(category);
    }

    @Override
    public void updateStatus(Integer id, Integer status) {
        categoryDao.updateStatus(id, status);
    }

    @Override
    public void delete(Integer id) {
        categoryDao.delete(id);
    }
}
