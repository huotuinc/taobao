package com.huotu.tools.taobao.entity;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * 属性
 *
 * @author CJ
 */
public class Property implements DatabaseStorage<Property>, DatabaseStorageHolder<Property>, CategoryAware {

    /**
     * 属性名称
     */
    private String name;

    //"parent_pid": 3747979,
    //        "parent_vid": 33478,
    // 生效前提 目标属性存在并且值是指定值
    private Long validatedPropertyId;
    private Long validatedPropertyValueId;
    /**
     * 关键属性
     */
    //is_key_prop
    private boolean keyProperty;
    /**
     * 销售属性
     */
    //is_sale_prop
    private boolean saleProperty;
    //is_enum_prop
    private boolean enumProperty;
    /**
     * 是否允许自定义
     */
    //is_input_prop
    private boolean inputProperty;
    /**
     * 未知
     */
    //is_item_prop
    private boolean itemProperty;
    /**
     * 是否允许多选
     */
    //multi
    private boolean multiple;
    /**
     * 是否必选
     */
    //must
    private boolean required;
    //pid
    private Long id;
    /**
     * 自定义模板
     */
    //child_template
    private String customTemplate;
    /**
     * 排序
     */
    //sort_order
    private int sortOrder;
    private String status;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getValidatedPropertyId() {
        return validatedPropertyId;
    }

    public void setValidatedPropertyId(Long validatedPropertyId) {
        this.validatedPropertyId = validatedPropertyId;
    }

    public Long getValidatedPropertyValueId() {
        return validatedPropertyValueId;
    }

    public void setValidatedPropertyValueId(Long validatedPropertyValueId) {
        this.validatedPropertyValueId = validatedPropertyValueId;
    }

    public boolean isKeyProperty() {
        return keyProperty;
    }

    public void setKeyProperty(boolean keyProperty) {
        this.keyProperty = keyProperty;
    }

    public boolean isSaleProperty() {
        return saleProperty;
    }

    public void setSaleProperty(boolean saleProperty) {
        this.saleProperty = saleProperty;
    }

    public boolean isEnumProperty() {
        return enumProperty;
    }

    public void setEnumProperty(boolean enumProperty) {
        this.enumProperty = enumProperty;
    }

    public boolean isInputProperty() {
        return inputProperty;
    }

    public void setInputProperty(boolean inputProperty) {
        this.inputProperty = inputProperty;
    }

    public boolean isItemProperty() {
        return itemProperty;
    }

    public void setItemProperty(boolean itemProperty) {
        this.itemProperty = itemProperty;
    }

    public boolean isMultiple() {
        return multiple;
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomTemplate() {
        return customTemplate;
    }

    public void setCustomTemplate(String customTemplate) {
        this.customTemplate = customTemplate;
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
        return "INSERT IGNORE INTO property(id,name,validatedPropertyId,validatedPropertyValueId,keyProperty,saleProperty" +
                ",enumProperty,inputProperty,itemProperty,multiple,required,customTemplate,sortOrder,status)" +
                " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    }

    @Override
    public void setValues(PreparedStatement ps, Property argument) throws SQLException {
        ps.setLong(1, argument.id);
        if (argument.name == null)
            ps.setNull(2, Types.VARCHAR);
        else
            ps.setString(2, argument.name);
        if (argument.validatedPropertyId == null)
            ps.setNull(3, Types.BIGINT);
        else
            ps.setLong(3, argument.validatedPropertyId);
        if (argument.validatedPropertyValueId == null)
            ps.setNull(4, Types.BIGINT);
        else
            ps.setLong(4, argument.validatedPropertyValueId);
        ps.setBoolean(5, argument.keyProperty);
        ps.setBoolean(6, argument.saleProperty);
        ps.setBoolean(7, argument.enumProperty);
        ps.setBoolean(8, argument.inputProperty);
        ps.setBoolean(9, argument.itemProperty);
        ps.setBoolean(10, argument.multiple);
        ps.setBoolean(11, argument.required);
        if (argument.customTemplate == null)
            ps.setNull(12, Types.VARCHAR);
        else
            ps.setString(12, argument.customTemplate);
        ps.setInt(13, argument.sortOrder);
        ps.setString(14, argument.status);

    }

    @Override
    public DatabaseStorage<Property> secondDatabaseStorage() {
        return new DatabaseStorage<Property>() {
            @Override
            public String insertSQL() {
                return "INSERT IGNORE INTO categoryProperty(categoryId,propertyId)" +
                        " VALUES (?,?)";
            }

            @Override
            public void setValues(PreparedStatement ps, Property argument) throws SQLException {
                ps.setLong(1, argument.category.getId());
                ps.setLong(2, argument.id);
            }
        };
    }

    private Category category;

    @Override
    public void setCategory(Category category) {
        this.category = category;
    }
}
