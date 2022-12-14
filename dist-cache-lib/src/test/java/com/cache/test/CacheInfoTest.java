package com.cache.test;

import com.cache.DistFactory;
import com.cache.api.*;
import com.cache.interfaces.Cache;
import com.cache.utils.DistUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CacheInfoTest {
    private static final Logger log = LoggerFactory.getLogger(CacheInfoTest.class);

    @Test
    public void infoTest() {
        log.info("START------");
        Cache cache = DistFactory.buildDefaultFactory()
                .withName("GlobalCacheTest")
                .withCacheStorageHashMap()
                .withCacheMaxObjectsAndItems(30, 100)
                .createCacheInstance();
        assertNotNull(cache, "Created cache should not be null");
        for (int i=0; i<50; i++) {
            // get 30 times the same value
            String currKey = "key" + DistUtils.randomInt(10);
            String v = cache.withCache(currKey, key -> {
                DistUtils.sleep(10);
                return "value ";
            }, CacheMode.modeTtlFiveMinutes);
        }
        List<CacheObjectInfo> objs = cache.getCacheInfos("");
        assertEquals(cache.getCacheValues("").size(), 10);
        objs.stream().forEach(x -> {
            log.info("OBJ IN CACHE: " + x);
        });
        log.info("Cache getItemsCount: " + cache.getObjectsCount());
        cache.close();
        assertTrue(cache.getClosed(), "Cache should be closed");
        log.info("END-----");
    }
}
