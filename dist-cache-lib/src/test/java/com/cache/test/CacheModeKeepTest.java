package com.cache.test;

import com.cache.DistFactory;
import com.cache.interfaces.Cache;
import com.cache.api.CacheMode;
import com.cache.utils.DistUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

public class CacheModeKeepTest {
    private static final Logger log = LoggerFactory.getLogger(CacheModeKeepTest.class);

    @Test
    public void modeKeepTest() {
        log.info("START ------ clean test");
        Cache cache = DistFactory.buildDefaultFactory()
                .withName("GlobalCacheTest")
                .withCacheStorageHashMap()
                .withCacheObjectTimeToLive(CacheMode.TIME_FIVE_SECONDS)
                .withTimerStorageClean(CacheMode.TIME_FIVE_SECONDS)
                .withCacheMaxObjectsAndItems(30, 100)
                .createCacheInstance();

        assertNotNull(cache, "Created cache should not be null");
        assertEquals(cache.getObjectsCount(), 0, "There should be no objects in cache");
        for (int i=0; i<30; i++) {
            String v = cache.withCache("key"+i, key -> "value", CacheMode.modeKeep);
            log.info("Objects in cache: " + cache.getObjectsCount() + ", keys: " + cache.getCacheKeys(""));
        }
        DistUtils.sleep(3000);
        assertEquals(cache.getObjectsCount(), 30, "There should be 30 objets in cache");
        DistUtils.sleep(3000);
        assertEquals(cache.getObjectsCount(), 30, "There should be 30 objets in cache");
        cache.close();
        assertTrue(cache.getClosed(), "Cache should be closed");
        assertEquals(cache.getObjectsCount(), 0, "There should be no objects in cache after close");
        log.info("END-----");
    }
}
