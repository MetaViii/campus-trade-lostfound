package com.campus.service.impl;

import com.campus.dao.GoodsDao;
import com.campus.dao.impl.GoodsDaoImpl;
import com.campus.entity.Goods;
import com.campus.service.GoodsService;
import com.campus.util.PageBean;
import com.campus.util.ServiceException;

import java.util.List;

/**
 * 二手商品业务实现类。
 */
public class GoodsServiceImpl implements GoodsService {

    private final GoodsDao goodsDao = new GoodsDaoImpl();

    @Override
    public PageBean<Goods> findByPage(String keyword, Integer categoryId, Integer status,
                                      Integer userId, int pageNum, int pageSize) {
        if (pageSize < 1) pageSize = 9;
        int total = goodsDao.count(keyword, categoryId, status, userId);
        int totalPages = Math.max(1, (total + pageSize - 1) / pageSize);
        if (pageNum < 1) pageNum = 1;
        if (pageNum > totalPages) pageNum = totalPages;
        int offset = (pageNum - 1) * pageSize;
        List<Goods> list = goodsDao.findByPage(keyword, categoryId, status, userId, offset, pageSize);
        return new PageBean<>(pageNum, pageSize, total, list);
    }

    @Override
    public List<Goods> findLatest(int limit) {
        return goodsDao.findLatest(limit);
    }

    @Override
    public Goods findById(Integer id) {
        return goodsDao.findById(id);
    }

    @Override
    public Goods viewDetail(Integer id) {
        goodsDao.increaseView(id);
        return goodsDao.findById(id);
    }

    @Override
    public void publish(Goods goods) {
        validate(goods);
        if (goods.getStatus() == null) {
            goods.setStatus(1); // 默认在售
        }
        goodsDao.insert(goods);
    }

    @Override
    public void modify(Goods goods) {
        validate(goods);
        goodsDao.update(goods);
    }

    @Override
    public void updateStatus(Integer id, Integer status) {
        goodsDao.updateStatus(id, status);
    }

    @Override
    public void delete(Integer id) {
        goodsDao.delete(id);
    }

    @Override
    public int countTotal() {
        return goodsDao.countTotal();
    }

    /** 商品字段合法性校验 */
    private void validate(Goods goods) {
        if (goods.getTitle() == null || goods.getTitle().trim().isEmpty()) {
            throw new ServiceException("商品标题不能为空");
        }
        if (goods.getCategoryId() == null) {
            throw new ServiceException("请选择商品分类");
        }
        if (goods.getPrice() == null || goods.getPrice() < 0) {
            throw new ServiceException("价格不能为空且不能为负数");
        }
        if (goods.getContact() == null || goods.getContact().trim().isEmpty()) {
            throw new ServiceException("联系方式不能为空");
        }
    }
}
