package com.huotu.tools.taobao.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.jdbc.core.PreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * 类目
 *
 * @author CJ
 */
public class Category implements PreparedStatementSetter {
    @JsonProperty("cid")
    private Long id;
    @JsonProperty("is_parent")
    private boolean isParent;
    @JsonProperty("parent_cid")
    private Long parentId;
    private String name;
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isParent() {
        return isParent;
    }

    public void setParent(boolean parent) {
        isParent = parent;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public void setValues(PreparedStatement ps) throws SQLException {
        ps.setLong(1, id);
        ps.setBoolean(2, isParent);
        if (parentId == null || parentId == 0)
            ps.setNull(3, Types.BIGINT);
        else
            ps.setLong(3, parentId);
        if (name==null)
            ps.setNull(4,Types.VARCHAR);
        else
            ps.setString(4,name);
        if (status==null)
            ps.setNull(5,Types.VARCHAR);
        else
            ps.setString(5,status);
    }
}
