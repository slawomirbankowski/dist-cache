package com.cache.app;

import com.cache.app.controllers.ObjectController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication
public class DistCacheApp {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(DistCacheApp.class);
    public static void main(String[] args) {
        SpringApplication.run(DistCacheApp.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            log.info("Let's inspect the beans provided by Spring Boot:");
            /*
            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (String beanName : beanNames) {
                System.out.println(beanName);
            }
             */
        };
    }

}
