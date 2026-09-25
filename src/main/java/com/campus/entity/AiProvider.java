package com.campus.entity;

import java.util.Date;

/**
 * AI 服务商配置。
 * 对应 ai_provider 表，一条记录代表一个服务商的接入信息。
 * 向量模型与对话模型各可以有若干条，其中一条为「当前使用中」。
 */
public class AiProvider {

    /** 向量模型 */
    public static final int KIND_EMBED = 1;
    /** 对话模型 */
    public static final int KIND_CHAT = 2;

    private Integer id;
    /** 1=向量模型 2=对话模型 */
    private Integer kind;
    /** 配置名称，如「硅基流动」 */
    private String name;
    private String baseUrl;
    private String apiKey;
    private String model;
    /** 1=当前使用中 */
    private Integer isActive;
    private Date createTime;
    private Date updateTime;

    public String getKindText() {
        if (kind == null) {
            return "-";
        }
        return kind == KIND_EMBED ? "向量模型" : "对话模型";
    }

    /** 是否当前使用中 */
    public boolean isActiveOn() {
        return isActive != null && isActive == 1;
    }

    /** 接口地址与模型名是否都填了，没填齐就无法调用 */
    public boolean isComplete() {
        return notEmpty(baseUrl) && notEmpty(model);
    }

    /** Key 只显示后 4 位，避免后台页面直接暴露完整密钥 */
    public String getApiKeyMasked() {
        if (apiKey == null || apiKey.isEmpty()) {
            return "（未填写）";
        }
        if (apiKey.length() <= 4) {
            return "****";
        }
        return "****" + apiKey.substring(apiKey.length() - 4);
    }

    private static boolean notEmpty(String s) {
        return s != null && !s.trim().isEmpty();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getKind() {
        return kind;
    }

    public void setKind(Integer kind) {
        this.kind = kind;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
