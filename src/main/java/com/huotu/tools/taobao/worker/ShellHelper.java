package com.huotu.tools.taobao.worker;

import com.huotu.tools.taobao.PropClient;
import com.huotu.tools.taobao.entity.Category;
import com.huotu.tools.taobao.jdbc.TheJdbcTemplate;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.shell.plugin.PromptProvider;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author CJ
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ShellHelper implements PromptProvider, CommandMarker {


    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;
    //    @Autowired
    private TheJdbcTemplate theJdbcTemplate = new TheJdbcTemplate();

    @Override
    public String getPrompt() {
        return "huotu>";
    }

    @Override
    public String getProviderName() {
        return "huotu";
    }

    @CliCommand(value = "task", help = "更换多线程环境")
    public String setPoolSize(@CliOption(key = "poolSize", unspecifiedDefaultValue = "20", help = "线程数") int poolSize) {
        threadPoolTaskScheduler.setPoolSize(poolSize);
        return "active:" + threadPoolTaskScheduler.getActiveCount();
    }

    @CliCommand(value = "status")
    public String status() {
        return "active:" + threadPoolTaskScheduler.getActiveCount();
    }

    @CliCommand(value = "silent", help = "切换静音状态")
    public String silent() {
        Runner.SILENT = !Runner.SILENT;
        return Runner.SILENT ? "Yes" : "No";
    }


    @CliCommand(value = "propertyStart", help = "开始抓取数据")
    public String propertyStart(
            @CliOption(key = "password", mandatory = true, help = "密码") String password
            , @CliOption(key = "local", mandatory = false, unspecifiedDefaultValue = "false", help = "是否本地测试") boolean local
    ) throws IOException {
        setupTemplate(password, local, false);

        PropClient client = new PropClient();
        List<Category> categories = client.topProp(false);
        Collections.reverse(categories);
        categories.forEach(category -> threadPoolTaskScheduler.submit(new PropertyRunner(category, theJdbcTemplate)));

        return "started";
    }

    private void setupTemplate(String password, boolean local, boolean dropFirst) {
        MysqlDataSource dataSource = new MysqlDataSource();
        if (local) {
            dataSource.setUrl("jdbc:mysql://120.24.243.104:3307/props?useUnicode=true&characterEncoding=UTF8");
            dataSource.setUser("dbuser");
            dataSource.setPassword("ddbbuusseerr");
        } else {
            dataSource.setUrl("jdbc:mysql://rds9161v8kv61r608m49.mysql.rds.aliyuncs.com:3306/prop3?useUnicode=true&characterEncoding=UTF8");
            dataSource.setUser("jc");
            dataSource.setPassword(password);
        }

        theJdbcTemplate.prepareDatasource(dataSource, dropFirst);
    }

    @CliCommand(value = "start", help = "开始抓取数据")
    public String start(
            @CliOption(key = "password", mandatory = true, help = "密码") String password
            , @CliOption(key = "recreate", mandatory = true, help = "是否重建数据表") boolean recreate
            , @CliOption(key = "local", mandatory = false, unspecifiedDefaultValue = "false", help = "是否本地测试") boolean local
            , @CliOption(key = "sandbox", unspecifiedDefaultValue = "false", help = "是否开启沙盒模式") boolean sandbox
    ) throws IOException {
        setupTemplate(password, local, recreate);

        PropClient client = new PropClient();
        List<Category> categories = client.topProp(sandbox);
        categories.forEach(category -> threadPoolTaskScheduler.submit(new Runner(threadPoolTaskScheduler, category, theJdbcTemplate)));
        return "started";
    }

}
