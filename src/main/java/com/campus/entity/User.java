package com.campus.entity;

import java.util.Date;

/**
 * 用户实体类，对应数据库 user 表。
 * role：0=普通用户，1=管理员；status：1=正常，0=禁用。
 */
public class User {

    private Integer id;
    private String account;
    private String password;
    private String nickname;
    private String phone;
    private Integer role;
    private Integer status;
    private Date createTime;

    public User() {
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Integer getRole() { return role; }
    public void setRole(Integer role) { this.role = role; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    /** 是否为管理员（供 JSP 中 ${user.admin} 使用） */
    public boolean isAdmin() {
        return role != null && role == 1;
    }
}
