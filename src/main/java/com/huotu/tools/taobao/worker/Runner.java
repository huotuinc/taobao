package com.huotu.tools.taobao.worker;

import com.huotu.tools.taobao.PropClient;
import com.huotu.tools.taobao.PropsConsumer;
import com.huotu.tools.taobao.entity.Category;
import com.huotu.tools.taobao.entity.DatabaseStorage;
import com.huotu.tools.taobao.entity.Property;
import com.huotu.tools.taobao.entity.PropertyValue;
import com.huotu.tools.taobao.jdbc.TheJdbcTemplate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 负责某一个大类目
 *
 * @author CJ
 */
public class Runner implements Runnable, PropsConsumer, ParameterizedPreparedStatementSetter<Category> {

    public static boolean SILENT = false;

    private static final Log log = LogFactory.getLog(Runner.class);
    private final Category category;
    private final PropClient client = new PropClient();
    private final TheJdbcTemplate template;
    private final AsyncTaskExecutor taskExecutor;

    public Runner(AsyncTaskExecutor taskExecutor, Category category, TheJdbcTemplate theJdbcTemplate) {
        this.taskExecutor = taskExecutor;
        this.category = category;
        this.template = theJdbcTemplate;
    }

    @Override
    public void run() {
        try {
            accept(Collections.singletonList(category));
        } catch (Throwable e) {
            log.error("re submit this runnable", e);
            taskExecutor.submit(this);
        }
    }


    @Override
    public void accept(List<Category> categoryList) throws IOException {
        System.gc();
        if (categoryList.isEmpty())
            return;
        if (!SILENT)
            log.debug("begin batch category for size:" + categoryList.size());
        template.batchUpdate("INSERT IGNORE INTO category(cid,is_parent,parent_cid,name,status) VALUES (?,?,?,?,?)", categoryList, 20, this);

        for (Category category : categoryList) {
            if (category.isParent()) {
                for (Category sub : client.listChildren(category)) {
                    taskExecutor.submit(new Runner(taskExecutor, sub, template));
                }
            } else {
                Set<DatabaseStorage> set = client.detailProp(category);
                // 分类  先保存Property 再保存PropertyValue

                if (set != null) {
                    HashSet<Property> properties = new HashSet<>();
                    HashSet<PropertyValue> propertyValues = new HashSet<>();
                    set.forEach(databaseStorage -> {
                        if (databaseStorage instanceof Property)
                            properties.add((Property) databaseStorage);
                        else if (databaseStorage instanceof PropertyValue) {
                            propertyValues.add((PropertyValue) databaseStorage);
                        }
                    });
                    if (!SILENT)
                        log.debug("begin batch property for size:" + properties.size());
                    template.batchInsert(properties, category);
                    if (!SILENT)
                        log.debug("begin batch property value for size:" + propertyValues.size());
                    template.batchInsert(propertyValues, category);
                }

            }
        }
    }

    @Override
    public void setValues(PreparedStatement ps, Category argument) throws SQLException {
        argument.setValues(ps);
    }
}
