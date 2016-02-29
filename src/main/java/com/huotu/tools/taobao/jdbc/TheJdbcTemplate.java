package com.huotu.tools.taobao.jdbc;

import com.huotu.tools.taobao.entity.Category;
import com.huotu.tools.taobao.entity.CategoryAware;
import com.huotu.tools.taobao.entity.DatabaseStorage;
import com.huotu.tools.taobao.entity.DatabaseStorageHolder;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.nativejdbc.NativeJdbcExtractorAdapter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author CJ
 */
public class TheJdbcTemplate extends JdbcTemplate {

    public void prepareDatasource(DataSource dataSource, boolean dropFirst) {
        setDataSource(dataSource);
        setNativeJdbcExtractor(new NativeJdbcExtractorAdapter() {


            @Override
            protected Connection doGetNativeConnection(Connection con) throws SQLException {
                con.setAutoCommit(true);
                return con;
            }
        });

        if (dropFirst) {
            execute("DROP TABLE IF EXISTS category");
            execute("DROP TABLE IF EXISTS property");
            execute("DROP TABLE IF EXISTS propertyValue");
            execute("DROP TABLE IF EXISTS categoryPropertyValue");
        }
        execute("CREATE TABLE IF NOT EXISTS category(cid BIGINT PRIMARY KEY, is_parent BIT, parent_cid BIGINT NULL" +
                " ,name VARCHAR(255) NULL ,status VARCHAR(20) NULL )");

        execute("CREATE TABLE IF NOT EXISTS property(id BIGINT PRIMARY KEY,name VARCHAR(200) NULL,validatedPropertyId BIGINT NULL" +
                ",validatedPropertyValueId BIGINT NULL" +
                ", keyProperty BIT NOT NULL ,saleProperty BIT NOT NULL ,enumProperty BIT NOT NULL, inputProperty BIT NOT NULL " +
                ", itemProperty BIT NOT NULL ,multiple BIT NOT NULL,required BIT NOT NULL,customTemplate VARCHAR(100) NULL " +
                ", sortOrder INT NOT NULL ,status VARCHAR(20) NOT NULL )");


        execute("CREATE TABLE IF NOT EXISTS propertyValue(id BIGINT PRIMARY KEY,name VARCHAR(200) NULL" +
                ", nameAlias VARCHAR(200) NULL" +
                ", propertyId BIGINT NULL" +
                ", sortOrder INT NOT NULL ,status VARCHAR(20) NOT NULL )");

        execute("CREATE TABLE IF NOT EXISTS categoryPropertyValue(categoryId BIGINT NOT NULL ,propertyValueId BIGINT NOT NULL" +
                ", PRIMARY KEY (categoryId,propertyValueId))");

        execute("CREATE TABLE IF NOT EXISTS categoryProperty(categoryId BIGINT NOT NULL ,propertyId BIGINT NOT NULL" +
                ", PRIMARY KEY (categoryId,propertyId))");

        try {
            execute("ALTER TABLE category  ADD CONSTRAINT fk1 FOREIGN KEY (parent_cid) REFERENCES category(cid)");

        } catch (NonTransientDataAccessException ignored) {

        }
        try {
            execute("ALTER TABLE propertyValue  ADD CONSTRAINT fk2 FOREIGN KEY (propertyId) REFERENCES property(id)");

        } catch (NonTransientDataAccessException ignored) {

        }
        try {
            execute("ALTER TABLE categoryProperty  ADD CONSTRAINT fk3 FOREIGN KEY (categoryId) REFERENCES category(cid)");

        } catch (NonTransientDataAccessException ignored) {

        }
        try {
            execute("ALTER TABLE categoryProperty  ADD CONSTRAINT fk4 FOREIGN KEY (propertyId) REFERENCES property(id)");
        } catch (NonTransientDataAccessException ignored) {

        }
        try {
            execute("ALTER TABLE categoryPropertyValue  ADD CONSTRAINT fk5 FOREIGN KEY (categoryId) REFERENCES category(cid)");
        } catch (NonTransientDataAccessException ignored) {

        }
        try {
            execute("ALTER TABLE categoryPropertyValue  ADD CONSTRAINT fk6 FOREIGN KEY (propertyValueId) REFERENCES propertyValue(id)");
        } catch (NonTransientDataAccessException ignored) {

        }

        // 属性是固定的东西 而属性值是一个不同类目 可以关联到不同的东西
        // 设计上可以根据属性值id和类目id 获取不同的属性值
        // 既有一个关联表 关联属性id和类目id


    }

    public void batchInsert(Collection<? extends DatabaseStorage> databaseStorages, Category category) {
        Map<Class, Collection<DatabaseStorage>> filters = new HashMap<>();
        databaseStorages.forEach(databaseStorage -> {
            Collection<DatabaseStorage> collection = filters.get(databaseStorage.getClass());
            if (collection == null) {
                collection = new HashSet<>();
                filters.put(databaseStorage.getClass(), collection);
            }
            collection.add(databaseStorage);
        });

        filters.values().forEach(entities -> {


            entities.forEach(databaseStorage -> {
                if (databaseStorage instanceof CategoryAware) {
                    ((CategoryAware) databaseStorage).setCategory(category);
                }
            });

            DatabaseStorage firstOne = entities.stream().findAny().get();
            //noinspection unchecked
            batchUpdate(firstOne.insertSQL(), entities, 20, firstOne);
            if (firstOne instanceof DatabaseStorageHolder) {
                firstOne = ((DatabaseStorageHolder) firstOne).secondDatabaseStorage();
//                HashSet<DatabaseStorage> seconds = new HashSet<>();
//                entities.forEach(databaseStorage -> {
//                    DatabaseStorage storage = ((DatabaseStorageHolder)databaseStorage).secondDatabaseStorage();
//                    if (storage instanceof CategoryAware){
//                        ((CategoryAware)storage).setCategory(category);
//                    }
//                    seconds.add(storage);
//                });
                //noinspection unchecked
                batchUpdate(firstOne.insertSQL(), entities, 20, firstOne);
            }
        });

    }
}
