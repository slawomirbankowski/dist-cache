package com.cache.test;

import com.cache.DistCacheFactory;
import com.cache.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheManagerCleanTest {
    private static final Logger log = LoggerFactory.getLogger(CacheManagerCleanTest.class);

    public static void main(String[] args) {
        log.info("START------");
        CacheConfig cfg = CacheConfig.buildEmptyConfig()
                .withName("GlobalCacheTest")
                .withStorageHashMap()
                .withObjectTimeToLive(CacheMode.TIME_TEN_SECONDS)
                .withTimer(1000L, 1000L)
                .withMaxObjectAndItems(30, 100);
        Cache cache = DistCacheFactory.createInstance(cfg);
        // key_keep should be still kept in
        cache.withCache("key_keep", key -> "value", CacheMode.modeKeep);
        // key_refresh should be refreshed
        cache.withCache("key_refresh", key -> ("value"+CacheUtils.randomInt(100000)), CacheMode.modeRefreshTenSeconds);
        for (int i=0; i<30; i++) {
            String v = cache.withCache("key"+i, key -> "value");
            log.info("Objects in cache: " + cache.getObjectsCount() + ", keys: " + cache.getCacheKeys("") + ", key_refresh: " + cache.getObject("key_refresh"));
            CacheUtils.sleep(1000);
        }
        for (int i=0; i<11; i++) {
            log.info("Objects in cache: " + cache.getObjectsCount() + ", keys: " + cache.getCacheKeys("" + ", key_refresh: " + cache.getObject("key_refresh")));
            CacheUtils.sleep(1000);
        }
        log.info("Cache getItemsCount: " + cache.getItemsCount() + ", keys: " + cache.getCacheKeys("") + ", key_refresh: " + cache.getObject("key_refresh"));
        cache.close();
        log.info("END-----");
    }
}
