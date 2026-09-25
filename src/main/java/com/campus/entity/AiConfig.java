package com.campus.entity;

import java.util.Date;

/**
 * AI 全局配置。
 * 对应 ai_config 表，只保留一行，存与具体服务商无关的设置：
 * 功能开关与每次返回的条数。各服务商的地址 / Key / 模型在 ai_provider 表。
 */
public class AiConfig {

    private Integer id;
    /** 1=开启 AI 功能 0=关闭 */
    private Integer enabled;
    /** 每次返回的匹配条数 */
    private Integer topN;
    private Date updateTime;

    /** AI 功能是否开启 */
    public boolean isOn() {
        return enabled != null && enabled == 1;
    }

    public String getEnabledText() {
        return isOn() ? "已开启" : "已关闭";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public Integer getTopN() {
        return topN;
    }

    public void setTopN(Integer topN) {
        this.topN = topN;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
