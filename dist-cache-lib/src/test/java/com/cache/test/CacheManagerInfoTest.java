package com.cache.test;

import com.cache.DistCacheFactory;
import com.cache.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CacheManagerInfoTest {
    private static final Logger log = LoggerFactory.getLogger(CacheManagerInfoTest.class);

    public static void main(String[] args) {
        log.info("START------");
        Cache cache = DistCacheFactory.buildDefaultFactory()
                .withName("GlobalCacheTest")
                .withStorageHashMap()
                .withMaxObjectAndItems(30, 100)
                .createInstance();
        for (int i=0; i<50; i++) {
            // get 30 times the same value
            String currKey = "key" + CacheUtils.randomInt(10);
            String v = cache.withCache(currKey, key -> {
                CacheUtils.sleep(10+CacheUtils.randomInt(10));
                    return "value ";
            }, CacheMode.modeTtlFiveMinutes);
        }
        List<CacheObjectInfo> objs = cache.getCacheValues("");
        objs.stream().forEach(x -> {
            log.info("OBJ IN CACHE: " + x);
        });
        log.info("Cache getItemsCount: " + cache.getItemsCount());
        cache.close();
        log.info("END-----");
    }
}
