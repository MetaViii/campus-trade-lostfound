package com.campus.entity;

import java.util.Date;

/**
 * 留言实体类，对应数据库 message 表。
 * targetType：1=商品，2=失物招领。
 * userNickname 为关联查询出的展示字段。
 */
public class Message {

    private Integer id;
    private Integer targetType;
    private Integer targetId;
    private Integer userId;
    private String content;
    private Date createTime;

    // 关联展示字段
    private String userNickname;

    public Message() {
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getTargetType() { return targetType; }
    public void setTargetType(Integer targetType) { this.targetType = targetType; }

    public Integer getTargetId() { return targetId; }
    public void setTargetId(Integer targetId) { this.targetId = targetId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public String getUserNickname() { return userNickname; }
    public void setUserNickname(String userNickname) { this.userNickname = userNickname; }
}
