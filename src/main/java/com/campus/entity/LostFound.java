package com.campus.entity;

import java.util.Date;

/**
 * 失物招领实体类，对应数据库 lost_found 表。
 * type：1=失物(寻物)，2=拾物(招领)。
 * status：1=寻找中，2=已找回，3=待认领，4=已认领，0=已关闭。
 * categoryName、publisherName 为关联查询出的展示字段。
 */
public class LostFound {

    private Integer id;
    private Integer type;
    private String name;
    private Integer categoryId;
    private String place;
    private Date happenTime;
    private String feature;
    private String contact;
    private String image;
    private Integer status;
    private Integer userId;
    private Integer viewCount;
    private Date createTime;

    // 关联展示字段
    private String categoryName;
    private String publisherName;

    public LostFound() {
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public String getPlace() { return place; }
    public void setPlace(String place) { this.place = place; }

    public Date getHappenTime() { return happenTime; }
    public void setHappenTime(Date happenTime) { this.happenTime = happenTime; }

    public String getFeature() { return feature; }
    public void setFeature(String feature) { this.feature = feature; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

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

    /** 类型中文描述 */
    public String getTypeText() {
        if (type == null) return "";
        return type == 1 ? "失物" : "招领";
    }

    /** 状态中文描述 */
    public String getStatusText() {
        if (status == null) return "";
        switch (status) {
            case 1: return "寻找中";
            case 2: return "已找回";
            case 3: return "待认领";
            case 4: return "已认领";
            case 0: return "已关闭";
            default: return "未知";
        }
    }
}
