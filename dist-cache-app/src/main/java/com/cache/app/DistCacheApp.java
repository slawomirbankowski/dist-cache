package com.cache.app;

import com.cache.utils.CacheUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;

/** entry for Spring application */
@SpringBootApplication
//@EnableSwagger2
public class DistCacheApp {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(DistCacheApp.class);
    private static String[] commandLineArguments;

    public static String[] getCommandLineArguments() {
        return commandLineArguments;
    }
    public static void main(String[] args) {
        commandLineArguments = args;
        log.info("STARTING DistCache REST application on host: " + CacheUtils.getCurrentHostName() + "/" + CacheUtils.getCurrentHostAddress() + ", GUID: " + CacheUtils.getCacheGuid());
        SpringApplication.run(DistCacheApp.class, args);
    }
    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            //ctx.getApplicationName();
            //ctx.getDisplayName();
            //log.info("");
            /*
            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (String beanName : beanNames) {
                log.info(beanName);
            }
             */
        };
    }

}
