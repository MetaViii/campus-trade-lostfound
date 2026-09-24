package com.campus.service.impl;

import com.campus.dao.LostFoundDao;
import com.campus.dao.impl.LostFoundDaoImpl;
import com.campus.entity.LostFound;
import com.campus.entity.MatchResult;
import com.campus.service.LostFoundService;
import com.campus.util.MatchUtil;
import com.campus.util.PageBean;
import com.campus.util.ServiceException;

import java.util.ArrayList;
import java.util.List;

/**
 * 失物招领业务实现类。
 */
public class LostFoundServiceImpl implements LostFoundService {

    private final LostFoundDao lostFoundDao = new LostFoundDaoImpl();

    /** 智能匹配的最低相似度阈值，低于该分值视为弱相关、不予展示 */
    private static final int MATCH_THRESHOLD = 20;

    @Override
    public PageBean<LostFound> findByPage(String keyword, Integer type, Integer categoryId,
                                          Integer status, Integer userId, int pageNum, int pageSize) {
        if (pageSize < 1) pageSize = 9;
        int total = lostFoundDao.count(keyword, type, categoryId, status, userId);
        int totalPages = Math.max(1, (total + pageSize - 1) / pageSize);
        if (pageNum < 1) pageNum = 1;
        if (pageNum > totalPages) pageNum = totalPages;
        int offset = (pageNum - 1) * pageSize;
        List<LostFound> list = lostFoundDao.findByPage(keyword, type, categoryId, status, userId, offset, pageSize);
        return new PageBean<>(pageNum, pageSize, total, list);
    }

    @Override
    public List<LostFound> findLatest(int limit) {
        return lostFoundDao.findLatest(limit);
    }

    @Override
    public LostFound findById(Integer id) {
        return lostFoundDao.findById(id);
    }

    @Override
    public LostFound viewDetail(Integer id) {
        lostFoundDao.increaseView(id);
        return lostFoundDao.findById(id);
    }

    @Override
    public void publish(LostFound lostFound) {
        validate(lostFound);
        if (lostFound.getStatus() == null) {
            // 失物默认“寻找中”，拾物默认“待认领”
            lostFound.setStatus(lostFound.getType() == 1 ? 1 : 3);
        }
        lostFoundDao.insert(lostFound);
    }

    @Override
    public void modify(LostFound lostFound) {
        validate(lostFound);
        lostFoundDao.update(lostFound);
    }

    @Override
    public void updateStatus(Integer id, Integer status) {
        lostFoundDao.updateStatus(id, status);
    }

    @Override
    public void delete(Integer id) {
        lostFoundDao.delete(id);
    }

    @Override
    public int countTotal() {
        return lostFoundDao.countTotal();
    }

    @Override
    public List<MatchResult> smartMatch(Integer id, int topN) {
        List<MatchResult> results = new ArrayList<>();
        LostFound source = lostFoundDao.findById(id);
        if (source == null || source.getType() == null) {
            return results;
        }
        // 失物匹配拾物，拾物匹配失物
        int oppositeType = source.getType() == 1 ? 2 : 1;
        // 1. 逐条计算相似度，得分达到阈值的才作为候选
        for (LostFound candidate : lostFoundDao.findCandidates(oppositeType)) {
            int score = MatchUtil.score(source, candidate);
            if (score >= MATCH_THRESHOLD) {
                results.add(new MatchResult(candidate, score, MatchUtil.reason(source, candidate)));
            }
        }
        // 2. 选择排序：把得分高的依次交换到前面（从高到低）
        for (int i = 0; i < results.size(); i++) {
            int maxIndex = i;
            for (int j = i + 1; j < results.size(); j++) {
                if (results.get(j).getScore() > results.get(maxIndex).getScore()) {
                    maxIndex = j;
                }
            }
            MatchResult temp = results.get(i);
            results.set(i, results.get(maxIndex));
            results.set(maxIndex, temp);
        }
        // 3. 只保留前 topN 条
        List<MatchResult> top = new ArrayList<>();
        for (int i = 0; i < results.size() && i < topN; i++) {
            top.add(results.get(i));
        }
        return top;
    }

    /** 失物招领字段合法性校验 */
    private void validate(LostFound lf) {
        if (lf.getType() == null || (lf.getType() != 1 && lf.getType() != 2)) {
            throw new ServiceException("请选择信息类型（失物 / 招领）");
        }
        if (lf.getName() == null || lf.getName().trim().isEmpty()) {
            throw new ServiceException("物品名称不能为空");
        }
        if (lf.getCategoryId() == null) {
            throw new ServiceException("请选择物品分类");
        }
        if (lf.getContact() == null || lf.getContact().trim().isEmpty()) {
            throw new ServiceException("联系方式不能为空");
        }
    }
}
