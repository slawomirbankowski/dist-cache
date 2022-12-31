package com.cache.test;

import com.cache.utils.DistUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GlobalUtilsTest {
    private static final Logger log = LoggerFactory.getLogger(GlobalUtilsTest.class);

    @Test
    public void agentRegisterSimpleTest() {
        log.info("START ------ agent register test test");

        Set<Class> classes = DistUtils.findAllClassesUsingClassLoader("com.cache.utils", ClassLoader.getSystemClassLoader());

        log.info(" Date time: " + DistUtils.getDateTimeYYYYMMDDHHmmss());

        log.info(" Classes count: " + classes.size());
        for (Class cl: classes) {
            log.info(" CLASS: " + cl.getName() + ", package: " + cl.getPackageName());
        }
        log.info("END-----");
    }
}
