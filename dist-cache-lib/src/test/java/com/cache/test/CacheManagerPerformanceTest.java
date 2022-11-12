package com.cache.test;

import com.cache.DistCacheFactory;
import com.cache.api.CacheConfig;
import com.cache.api.CacheMode;
import com.cache.api.CacheUtils;
import com.cache.api.CacheableMethod;
import com.cache.managers.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheManagerPerformanceTest {
    private static final Logger log = LoggerFactory.getLogger(CacheManagerPerformanceTest.class);

    public static void main(String[] args) {
        System.out.println("START------");

        CacheConfig cfg = CacheConfig.buildEmptyConfig()
                .withName("GlobalCacheTest")
                .withPort(9999)
                .withCacheApp("https://localhost:9999/")
                .withServers("localhost:9095")
                .withStorageHashMap()
                .withMaxObjectAndItems(30, 100);
        CacheManager cache = DistCacheFactory.getInstance(cfg);
        log.info("Cache storages: " + cache.getStorageKeys());

        for (int test=0; test<50; test++) {
            long startTime = System.currentTimeMillis();
            for (int i=0; i<10+test*5; i++) {
                String v = cache.withCache("key"+i, new CacheableMethod<String>() {
                    @Override
                    public String get(String key) {
                        CacheUtils.sleep(80);
                        return "value for " + key;
                    }
                }, CacheMode.modeTtlThirtySeconds);
                //log.info("Test=" + test + ", i=" + i + ", value= " + v);
            }
            long totalTime = System.currentTimeMillis() - startTime;
            log.info("TEST " + test + ", TIME: " + totalTime + ", objectsInCache: " + cache.getObjectsCount());
            CacheUtils.sleep(1000);
        }
        log.info("Cache getObjectsCount: " + cache.getObjectsCount());
        cache.close();
        System.out.println("END-----");
    }
}
