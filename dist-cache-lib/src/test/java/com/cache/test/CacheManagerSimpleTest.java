package com.cache.test;

import com.cache.DistCacheFactory;
import com.cache.api.CacheConfig;
import com.cache.api.CacheMode;
import com.cache.api.CacheUtils;
import com.cache.api.CacheableMethod;
import com.cache.managers.CacheManager;
import com.cache.test.dao.DatabaseCacheDao;
import com.cache.test.dao.DatabaseDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheManagerSimpleTest {
    private static final Logger log = LoggerFactory.getLogger(CacheManagerSimpleTest.class);

    public static void main(String[] args) {
        System.out.println("START------");

        CacheConfig cfg= CacheConfig.buildEmptyConfig()
                .withName("GlobalCacheTest")
                .withPort(9999)
                .withCacheApp("https://localhost:9999/")
                .withServers("localhost:9095")
                .withStorageHashMap()
                .withMaxObjectAndItems(100, 20000);

        log.info("Initializing cache");
        CacheManager cache = DistCacheFactory.getInstance(cfg);
        log.info("Cache GUID: " + cache.getCacheManagerGuid());
        log.info("Cache createdDateTime: " + cache.getCreatedDateTime());
        log.info("Cache getItemsCount: " + cache.getItemsCount());
        log.info("Cache getClosed: " + cache.getClosed());
        log.info("Cache GUID: " + cache.getCacheManagerGuid());

        log.info("Cache getItemsCount: " + cache.getItemsCount());
        for (int test=0; test<10; test++) {
            long startTime = System.currentTimeMillis();
            for (int i=0; i<100; i++) {
                String v = cache.withCache("key"+i, new CacheableMethod<String>() {
                    @Override
                    public String get(String key) {
                        CacheUtils.sleep(30);
                        return "value for " + key;
                    }
                }, CacheMode.modeTtlFiveMinutes);
                //log.info("Test=" + test + ", i=" + i + ", value= " + v);
            }
            long totalTime = System.currentTimeMillis() - startTime;
            log.info("TEST " + test + ", TIME: " + totalTime);
        }

        log.info("Cache getItemsCount: " + cache.getItemsCount());
        cache.close();
        System.out.println("END-----");
    }
}
