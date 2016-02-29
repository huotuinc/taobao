package com.huotu.tools.taobao.worker;

import com.huotu.tools.taobao.PropClient;
import com.huotu.tools.taobao.PropsConsumer;
import com.huotu.tools.taobao.entity.Category;
import com.huotu.tools.taobao.entity.DatabaseStorage;
import com.huotu.tools.taobao.entity.Property;
import com.huotu.tools.taobao.entity.support.CategoryProperty;
import com.huotu.tools.taobao.jdbc.TheJdbcTemplate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 只是获取所有属性并且设置关联
 *
 * @author CJ
 */
public class PropertyRunner implements Runnable, PropsConsumer {
    private static final Log log = LogFactory.getLog(PropertyRunner.class);

    private final Category category;
    private final TheJdbcTemplate template;
    private final PropClient client = new PropClient();

    public PropertyRunner(Category category, TheJdbcTemplate jdbcTemplate) {
        this.category = category;
        this.template = jdbcTemplate;
    }


    @Override
    public void run() {
        try {
            accept(Collections.singletonList(category));
        } catch (IOException e) {
            log.error("IO", e);
        }
    }


    @Override
    public void accept(List<Category> categoryList) throws IOException {
        if (categoryList.isEmpty())
            return;
        if (!Runner.SILENT)
            log.info("begin batch category for size:" + categoryList.size());

        for (Category category : categoryList) {
            if (category.isParent())
                accept(client.listChildren(category));
            else {
                Set<DatabaseStorage> set = client.detailProp(category);

                if (set != null) {

                    Set<CategoryProperty> properties = new HashSet<>();
//                    set.forEach(databaseStorage -> {
//                        if (databaseStorage instanceof Property)
//                            properties.add(((Property) databaseStorage).secondDatabaseStorage());
//                    });

                    properties.forEach(categoryProperty -> categoryProperty.setCategory(category));

                    if (!Runner.SILENT)
                        log.info("begin batch property for size:" + properties.size());

                    template.batchInsert(properties,category);
                }

            }
        }
    }

}
