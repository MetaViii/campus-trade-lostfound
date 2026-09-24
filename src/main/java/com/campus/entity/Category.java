package com.campus.entity;

/**
 * 分类实体类，对应数据库 category 表。
 * type：1=二手商品分类，2=失物招领分类；status：1=启用，0=停用。
 */
public class Category {

    private Integer id;
    private String name;
    private Integer type;
    private Integer status;

    public Category() {
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
