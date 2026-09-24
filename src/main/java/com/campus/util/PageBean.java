package com.campus.util;

import java.util.List;

/**
 * 分页封装类。
 * 保存当前页数据及分页相关信息，供列表页与后台分页使用。
 *
 * @param <T> 列表中元素的类型
 */
public class PageBean<T> {

    private int pageNum;     // 当前页码（从 1 开始）
    private int pageSize;    // 每页记录数
    private int total;       // 总记录数
    private int totalPages;  // 总页数
    private List<T> list;    // 当前页数据

    public PageBean() {
    }

    public PageBean(int pageNum, int pageSize, int total, List<T> list) {
        this.pageSize = pageSize;
        this.total = total;
        this.list = list;
        this.totalPages = (total + pageSize - 1) / pageSize; // 向上取整
        if (this.totalPages == 0) {
            this.totalPages = 1;
        }
        // 修正页码边界
        if (pageNum < 1) {
            pageNum = 1;
        }
        if (pageNum > this.totalPages) {
            pageNum = this.totalPages;
        }
        this.pageNum = pageNum;
    }

    public boolean isHasPrev() {
        return pageNum > 1;
    }

    public boolean isHasNext() {
        return pageNum < totalPages;
    }

    public int getPrevPage() {
        return isHasPrev() ? pageNum - 1 : 1;
    }

    public int getNextPage() {
        return isHasNext() ? pageNum + 1 : totalPages;
    }

    public int getPageNum() { return pageNum; }
    public void setPageNum(int pageNum) { this.pageNum = pageNum; }

    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }

    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public List<T> getList() { return list; }
    public void setList(List<T> list) { this.list = list; }
}
