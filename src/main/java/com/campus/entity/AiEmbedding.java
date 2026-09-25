package com.campus.entity;

import java.util.Date;

/**
 * 条目向量缓存。
 * 对应 ai_embedding 表，保存某条商品/失物记录文本对应的向量，
 * 避免每次检索都重新调用向量接口。
 */
public class AiEmbedding {

    private Integer id;
    /** 1=失物招领 2=二手商品（与 AiLog.module 保持一致） */
    private Integer entityType;
    private Integer entityId;
    /** 生成该向量的模型名，换模型后需要重算 */
    private String model;
    /** 向量维度 */
    private Integer dim;
    /** 向量，JSON 数组字符串 */
    private String vec;
    /** 生成向量时所用文本的 MD5，文本变了说明需要重算 */
    private String textHash;
    private Date updateTime;

    public AiEmbedding() {
    }

    public AiEmbedding(Integer entityType, Integer entityId, String model,
                       Integer dim, String vec, String textHash) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.model = model;
        this.dim = dim;
        this.vec = vec;
        this.textHash = textHash;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getDim() {
        return dim;
    }

    public void setDim(Integer dim) {
        this.dim = dim;
    }

    public String getVec() {
        return vec;
    }

    public void setVec(String vec) {
        this.vec = vec;
    }

    public String getTextHash() {
        return textHash;
    }

    public void setTextHash(String textHash) {
        this.textHash = textHash;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
