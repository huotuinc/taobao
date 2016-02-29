package com.huotu.tools.taobao.worker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.io.IOException;

/**
 * @author CJ
 */
public class Starter {

    private static final Log log = LogFactory.getLog(Starter.class);
    private final static String[] CONTEXT_PATH = { "classpath*:/META-INF/spring/spring-shell-plugin.xml" };


    public static void main(String[] args) throws IOException, InterruptedException {
        GenericApplicationContext ctx = new GenericApplicationContext();
        ctx.registerShutdownHook();
//        configureApplicationContext(ctx);

        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(ctx);
        reader.loadBeanDefinitions(CONTEXT_PATH);
        ctx.refresh();


        ShellHelper shellHelper = ctx.getBean(ShellHelper.class);

        shellHelper.setPoolSize(60);

        ThreadPoolTaskScheduler threadPoolTaskScheduler = ctx.getBean(ThreadPoolTaskScheduler.class);

        while (true){
            Thread.sleep(60000);
            log.info("current active:"+threadPoolTaskScheduler.getActiveCount());
//            if (threadPoolTaskScheduler.getActiveCount()==0){
//                ctx.stop();
//                return;
//            }
        }
    }
}
