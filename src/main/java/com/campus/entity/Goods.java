package com.campus.entity;

import java.util.Date;

/**
 * 二手商品实体类，对应数据库 goods 表。
 * status：1=在售，2=已售，0=下架。
 * categoryName、publisherName 为关联查询出的展示字段，不对应单独的列。
 */
public class Goods {

    private Integer id;
    private String title;
    private Integer categoryId;
    private Double price;
    private String quality;
    private String description;
    private String image;
    private String tradePlace;
    private String contact;
    private Integer status;
    private Integer userId;
    private Integer viewCount;
    private Date createTime;

    // 关联展示字段
    private String categoryName;
    private String publisherName;

    public Goods() {
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getQuality() { return quality; }
    public void setQuality(String quality) { this.quality = quality; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getTradePlace() { return tradePlace; }
    public void setTradePlace(String tradePlace) { this.tradePlace = tradePlace; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getPublisherName() { return publisherName; }
    public void setPublisherName(String publisherName) { this.publisherName = publisherName; }

    /** 状态中文描述，供页面显示 */
    public String getStatusText() {
        if (status == null) return "";
        switch (status) {
            case 1: return "在售";
            case 2: return "已售";
            case 0: return "下架";
            default: return "未知";
        }
    }
}
