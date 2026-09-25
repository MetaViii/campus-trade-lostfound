package com.campus.entity;

/**
 * AI 匹配结果。
 * 同时用于二手商品与失物招领两种条目，因此不直接持有实体对象，
 * 只保留页面展示所需的字段，由 Service 层从各自实体搬运过来。
 */
public class AiMatchResult {

    /** 条目类型：1=二手商品 2=失物招领 */
    private Integer entityType;
    /** 条目编号 */
    private Integer entityId;
    /** 标题：商品标题 / 失物招领名称 */
    private String title;
    /** 分类名 */
    private String categoryName;
    /** 地点：交易地点 / 丢失或拾到地点 */
    private String place;
    /** 状态文字，如「在售」「失物」「拾物」 */
    private String typeText;
    /** 徽章样式类，如 badge-success */
    private String badgeClass;
    /** 余弦相似度，取值 -1~1 */
    private double similarity;
    /** 详情页相对路径，如 /goods/detail?id=3 */
    private String link;
    /** 简介摘要 */
    private String snippet;

    /** 相似度按百分比展示，页面上显示成「相似度 87%」 */
    public int getSimilarityPercent() {
        double v = similarity * 100;
        if (v < 0) {
            return 0;
        }
        return (int) Math.round(Math.min(v, 100));
    }

    public Integer getEntityType() {
        return entityType;
    }

    public void setEntityType(Integer entityType) {
        this.entityType = entityType;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getTypeText() {
        return typeText;
    }

    public void setTypeText(String typeText) {
        this.typeText = typeText;
    }

    public String getBadgeClass() {
        return badgeClass;
    }

    public void setBadgeClass(String badgeClass) {
        this.badgeClass = badgeClass;
    }

    public double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }
}
