package com.cache.test;

import com.cache.DistCacheFactory;
import com.cache.api.Cache;
import com.cache.api.CacheMode;
import com.cache.api.CacheObjectInfo;
import com.cache.utils.CacheUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CacheManagerKeepTest {
    private static final Logger log = LoggerFactory.getLogger(CacheManagerKeepTest.class);

    @Test
    public void modeKeepTest() {
        log.info("START ------ clean test");
        Cache cache = DistCacheFactory.buildDefaultFactory()
                .withName("GlobalCacheTest")
                .withStorageHashMap()
                //.withStoragePriorityQueue()
                //.withStoragePriorityQueue()
                .withObjectTimeToLive(CacheMode.TIME_FIVE_SECONDS)
                .withTimer(CacheMode.TIME_FIVE_SECONDS, CacheMode.TIME_FIVE_SECONDS)
                .withMaxObjectAndItems(30, 100)
                .createInstance();

        assertNotNull(cache, "Created cache should not be null");
        assertEquals(cache.getObjectsCount(), 0, "There should be no objects in cache");
        for (int i=0; i<30; i++) {
            String v = cache.withCache("key"+i, key -> "value", CacheMode.modeKeep);
            log.info("Objects in cache: " + cache.getObjectsCount() + ", keys: " + cache.getCacheKeys(""));
        }
        CacheUtils.sleep(10000);
        assertEquals(cache.getObjectsCount(), 30, "There should be 30 objets in cache");
        CacheUtils.sleep(10000);
        assertEquals(cache.getObjectsCount(), 30, "There should be 30 objets in cache");
        cache.close();
        assertTrue(cache.getClosed(), "Cache should be closed");
        assertEquals(cache.getObjectsCount(), 0, "There should be no objects in cache after close");
        log.info("END-----");
    }
}
