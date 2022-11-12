package com.cache.test;

import com.cache.DistCacheFactory;
import com.cache.api.*;
import com.cache.managers.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;

public class CacheManagerPerformanceTest {
    private static final Logger log = LoggerFactory.getLogger(CacheManagerPerformanceTest.class);

    public static void main(String[] args) {
        log.info("START------");
        CacheConfig cfg = DistCacheFactory.createEmptyConfig()
                .withName("GlobalCacheTest")
                .withStorageHashMap()
                .withObjectTimeToLive(CacheMode.TIME_ONE_HOUR)
                .withMaxObjectAndItems(500, 3000);
        Cache cache = DistCacheFactory.createInstance(cfg);
        log.info("Cache storages: " + cache.getStorageKeys());
        LinkedList<Object[]> testResults = new LinkedList<>();
        // run 50 tests and 500 random keys get
        // each key is taking 10ms
        // each test is trying to get 50 keys
        for (int test=0; test<50; test++) {
            long startTime = System.currentTimeMillis();
            for (int i=0; i<50; i++) {
                String key = "key" + CacheUtils.randomInt(500);
                String v = cache.withCache(key, k -> {
                        CacheUtils.sleep(10);
                        return "value for " + k;
                }, CacheMode.modeTtlOneHour);
                //log.info("Test=" + test + ", i=" + i + ", value= " + v);
            }
            long totalTime = System.currentTimeMillis() - startTime;
            testResults.add(new Object[] { test, totalTime, cache.getObjectsCount() });
        }
        // show all test results
        testResults.stream().forEach(tr -> {
            log.info("!!!!!!!!!!!!!!!!!! Test " + tr[0] + ", time: " + tr[1] + ", objectsInCache: " + tr[2]);
        });
        cache.close();
        log.info("END-----");
    }
}
