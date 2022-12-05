package com.cache.test;

import com.cache.DistFactory;
import com.cache.interfaces.Cache;
import com.cache.api.CacheMode;
import com.cache.utils.CacheUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

public class CacheManagerStorageLocalDiskTest {
    private static final Logger log = LoggerFactory.getLogger(CacheManagerStorageLocalDiskTest.class);

    AtomicLong seq = new AtomicLong();
    @Test
    public void modeRefreshTest() {
        log.info("START ------ clean test");
        Cache cache = DistFactory.buildDefaultFactory()
                .withName("GlobalCacheTest")
                .withStorageLocalDisk("")
                .withObjectTimeToLive(CacheMode.TIME_ONE_DAY)
                .withTimer(CacheMode.TIME_ONE_HOUR, CacheMode.TIME_ONE_HOUR)
                .withMaxObjectAndItems(30, 10000000)
                .createCacheInstance();

        assertNotNull(cache, "Created cache should not be null");
        assertEquals(cache.getObjectsCount(), 0, "There should be no objects in cache");
        for (int i=0; i<1000; i++) {
            int keyNum = CacheUtils.randomInt(10);
            String v = cache.withCache("key"+keyNum, key -> getNextValue(key), CacheMode.modeKeep);
            log.info("Objects in cache: " + cache.getObjectsCount() + ", keys: " + cache.getCacheKeys(""));
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
