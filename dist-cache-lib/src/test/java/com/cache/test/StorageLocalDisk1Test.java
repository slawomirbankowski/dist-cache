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

public class StorageLocalDisk1Test {
    private static final Logger log = LoggerFactory.getLogger(StorageLocalDisk1Test.class);

    AtomicLong seq = new AtomicLong();
    @Test
    public void storageLocalDiskTest() {
        log.info("START ------ Storage LocalDisk test");
        Cache cache = DistFactory.buildDefaultFactory()
                .withName("GlobalCacheTest")
                .withCacheStorageLocalDisk("../../")
                .withObjectTimeToLive(CacheMode.TIME_ONE_DAY)
                .withTimerStorageClean(CacheMode.TIME_ONE_HOUR)
                .withMaxObjectAndItems(30, 10000000)
                .createCacheInstance();

        assertNotNull(cache, "Created cache should not be null");
        assertEquals(cache.getObjectsCount(), 0, "There should be no objects in cache");
        for (int i=0; i<1000; i++) {
            int keyNum = DistUtils.randomInt(10);
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
