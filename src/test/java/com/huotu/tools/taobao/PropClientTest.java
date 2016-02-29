package com.huotu.tools.taobao;

import com.huotu.tools.taobao.entity.Category;
import com.huotu.tools.taobao.entity.DatabaseStorage;
import com.huotu.tools.taobao.jdbc.TheJdbcTemplate;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.jdbc.datasource.DataSourceUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;

/**
 * @author CJ
 */
public class PropClientTest {

    private PropClient propClient = new PropClient();

    @Test
    public void testTopProp() throws Exception {

        TheJdbcTemplate theJdbcTemplate = new TheJdbcTemplate();

        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUrl("jdbc:mysql://120.24.243.104:3307/propstest?useUnicode=true&characterEncoding=UTF8");
        dataSource.setUser("dbuser");
        dataSource.setPassword("ddbbuusseerr");

        theJdbcTemplate.prepareDatasource(dataSource,true);


        List<Category> list = propClient.topProp(false);
        Assert.assertTrue(!list.isEmpty());
        List<Category> children = propClient.listChildren(list.get(2));
        System.out.println(children);
        children.forEach(prop -> {
            if (!prop.isParent()){
                try {
                    Set<DatabaseStorage> set= propClient.detailProp(prop);
                    if (set!=null){
                        System.out.println(set);
                        theJdbcTemplate.batchInsert(set,prop);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Test
    public void jdbcTest() throws IOException, SQLException {
        // propstest
        // props

        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUrl("jdbc:mysql://120.24.243.104:3307/propstest?useUnicode=true&characterEncoding=UTF8");
        dataSource.setUser("dbuser");
        dataSource.setPassword("ddbbuusseerr");

        Main main = new Main(dataSource);

        List<Category> list = propClient.topProp(false);
        list.forEach(prop -> prop.setParent(false));

        main.accept(list);

        /// assert it
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet resultSet = statement.executeQuery("SELECT count(*) from props")) {
                    resultSet.next();
                    int x = resultSet.getInt(1);
                    assertTrue(x > 0);
                }
            }
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

}