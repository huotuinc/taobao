package com.huotu.tools.taobao.entity.support;

import com.huotu.tools.taobao.entity.Category;
import com.huotu.tools.taobao.entity.CategoryAware;
import com.huotu.tools.taobao.entity.DatabaseStorage;
import com.huotu.tools.taobao.entity.Property;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author CJ
 */
public class CategoryProperty implements DatabaseStorage<CategoryProperty>,CategoryAware {

    private Category category;
    private final Property property;

    public CategoryProperty(Property property) {
        this.property = property;
    }

    @Override
    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public String insertSQL() {
        return "INSERT IGNORE INTO categoryProperty(categoryId,propertyId)" +
                " VALUES (?,?)";
    }

    @Override
    public void setValues(PreparedStatement ps, CategoryProperty argument) throws SQLException {
        ps.setLong(1, category.getId());
        ps.setLong(2, argument.property.getId());
    }
}
