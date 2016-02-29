package com.huotu.tools.taobao.entity;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * 属性-值
 *
 * @author CJ
 */
public class PropertyValue implements DatabaseStorage<PropertyValue>, DatabaseStorageHolder<PropertyValue> {
    //vid
    private Long id;
    //cid
    private Long categoryId;
    /**
     * 名称
     */
    private String name;
    /**
     * 名称
     */
    private String nameAlias;

    private Long propertyId;
    //prop_name 属性名称 冗余量

    /**
     * 排序
     */
    //sort_order
    private int sortOrder;
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameAlias() {
        return nameAlias;
    }

    public void setNameAlias(String nameAlias) {
        this.nameAlias = nameAlias;
    }

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String insertSQL() {
        return "INSERT IGNORE INTO propertyValue(id,name,nameAlias,propertyId,sortOrder,status)" +
                " VALUES (?,?,?,?,?,?)";
    }

    @Override
    public void setValues(PreparedStatement ps, PropertyValue argument) throws SQLException {
        ps.setLong(1, argument.id);
        if (argument.name == null)
            ps.setNull(2, Types.VARCHAR);
        else
            ps.setString(2, argument.name);
        if (argument.nameAlias == null)
            ps.setNull(3, Types.VARCHAR);
        else
            ps.setString(3, argument.nameAlias);
        if (argument.propertyId == null)
            ps.setNull(4, Types.BIGINT);
        else
            ps.setLong(4, argument.propertyId);

        ps.setInt(5, argument.sortOrder);
        ps.setString(6, argument.status);
    }

    @Override
    public DatabaseStorage<PropertyValue> secondDatabaseStorage() {
        return new DatabaseStorage<PropertyValue>() {
            @Override
            public String insertSQL() {
                return "INSERT IGNORE INTO categoryPropertyValue(categoryId,propertyValueId) VALUES (?,?)";
            }

            @Override
            public void setValues(PreparedStatement ps, PropertyValue argument) throws SQLException {
                ps.setLong(1, argument.categoryId);
                ps.setLong(2, argument.id);
            }
        };
    }
}
