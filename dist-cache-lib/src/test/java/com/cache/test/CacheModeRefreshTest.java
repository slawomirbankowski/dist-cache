package com.cache.test;

import com.cache.DistFactory;
import com.cache.interfaces.Cache;
import com.cache.api.CacheMode;
import com.cache.utils.DistUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

public class CacheModeRefreshTest {
    private static final Logger log = LoggerFactory.getLogger(CacheModeRefreshTest.class);

    AtomicLong seq = new AtomicLong();
    @Test
    public void modeRefreshTest() {
        log.info("START ------ clean test");
        Cache cache = DistFactory.buildDefaultFactory()
                .withName("GlobalCacheTest")
                .withCacheStorageHashMap()
                .withCacheObjectTimeToLive(CacheMode.TIME_FIVE_SECONDS)
                .withTimerStorageClean(1000)
                .withCacheMaxObjectsAndItems(30, 100)
                .createCacheInstance();
        assertNotNull(cache, "Created cache should not be null");
        assertEquals(cache.getObjectsCount(), 0, "There should be no objects in cache");
        for (int i=0; i<10; i++) {
            String v = cache.withCache("key"+i, key -> getNextValue(key), CacheMode.modeRefreshTenSeconds);
            log.info("Objects in cache: " + cache.getObjectsCount() + ", keys: " + cache.getCacheKeys(""));
        }

        DistUtils.sleep(11000);
        assertEquals(cache.getObjectsCount(), 10, "There should be 10 objets in cache");
        for (int i=0; i<10; i++) {
            log.info("Refreshed key= " + i + ", object: " + cache.getCacheObject("key"+i).get().getValue());
        }

        DistUtils.sleep(11000);
        for (int i=0; i<10; i++) {
            log.info("Refreshed key= " + i + ", object: " + cache.getCacheObject("key"+i).get().getValue());
        }
        cache.close();
        assertTrue(cache.getClosed(), "Cache should be closed");
        assertEquals(cache.getObjectsCount(), 0, "There should be no objects in cache after close");

        log.info("END-----");
    }
    public String getNextValue(String key) {
        return "value" + seq.incrementAndGet();
    }
}
