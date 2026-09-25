package com.campus.entity;

import java.util.Date;

/**
 * AI 对话日志。
 * 对应 ai_log 表，记录每个用户每次使用 AI 匹配的输入、AI 的答复与命中条目，
 * 供管理员在后台「AI 对话记录」页查看。
 */
public class AiLog {

    private Integer id;
    private Integer userId;
    /** 1=失物招领 2=二手商品 */
    private Integer module;
    /** 用户输入的描述 */
    private String queryText;
    /** AI 生成的答复 */
    private String replyText;
    /** 命中的记录编号，逗号分隔 */
    private String resultIds;
    private Integer resultCount;
    private Integer costMs;
    /** 1=成功 0=失败 */
    private Integer status;
    private String errorMsg;
    private Date createTime;

    /** 关联查询带出的用户昵称 */
    private String userNickname;

    public String getModuleText() {
        if (module == null) {
            return "-";
        }
        return module == 1 ? "失物招领" : "二手商品";
    }

    /** 复制一份，只保留答复的前若干字符，供列表页预览 */
    public String getReplyBrief() {
        if (replyText == null || replyText.isEmpty()) {
            return "";
        }
        return replyText.length() <= 60 ? replyText : replyText.substring(0, 60) + "...";
    }

    public boolean isSuccess() {
        return status != null && status == 1;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getModule() {
        return module;
    }

    public void setModule(Integer module) {
        this.module = module;
    }

    public String getQueryText() {
        return queryText;
    }

    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }

    public String getReplyText() {
        return replyText;
    }

    public void setReplyText(String replyText) {
        this.replyText = replyText;
    }

    public String getResultIds() {
        return resultIds;
    }

    public void setResultIds(String resultIds) {
        this.resultIds = resultIds;
    }

    public Integer getResultCount() {
        return resultCount;
    }

    public void setResultCount(Integer resultCount) {
        this.resultCount = resultCount;
    }

    public Integer getCostMs() {
        return costMs;
    }

    public void setCostMs(Integer costMs) {
        this.costMs = costMs;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }
}
