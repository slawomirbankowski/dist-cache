package com.cache.test;

import com.cache.DistCacheFactory;
import com.cache.api.*;
import com.cache.managers.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheManagerSimpleTest {
    private static final Logger log = LoggerFactory.getLogger(CacheManagerSimpleTest.class);

    public static void main(String[] args) {
        log.info("START------");
        CacheConfig cfg = CacheConfig.buildEmptyConfig()
                .withName("GlobalCacheTest")
                .withStorageHashMap()
                .withMaxObjectAndItems(30, 100);
        log.info("Config GUID: " + cfg.getConfigGuid());
        log.info("Initializing cache");
        Cache cache = DistCacheFactory.createInstance(cfg);
        log.info("Cache GUID: " + cache.getCacheManagerGuid());
        log.info("Cache createdDateTime: " + cache.getCreatedDateTime());
        log.info("Cache getObjectsCount: " + cache.getObjectsCount());
        log.info("Cache getItemsCount: " + cache.getItemsCount());
        log.info("Cache getClosed: " + cache.getClosed());
        log.info("Cache GUID: " + cache.getCacheManagerGuid());
        log.info("Cache storages: " + cache.getStorageKeys());
        for (int i=0; i<30; i++) {
            // get 30 times the same value
            String v = cache.withCache("key", key -> {
                    CacheUtils.sleep(1000);
                    return "value ";
            }, CacheMode.modeTtlTenSeconds);
            log.info("Key=" + i + ", value= " + v);
            CacheUtils.sleep(500);
        }
        log.info("Cache getItemsCount: " + cache.getItemsCount());
        cache.close();
        log.info("END-----");
    }
}
