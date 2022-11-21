package com.cache.test;

import com.cache.DistCacheFactory;
import com.cache.api.*;
import com.cache.utils.CacheUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

public class CacheManagerSimpleTest {
    private static final Logger log = LoggerFactory.getLogger(CacheManagerSimpleTest.class);

    @Test
    public void simpleTest() {
        log.info("START------");
        Cache cache = DistCacheFactory.buildDefaultFactory()
                .withName("GlobalCacheTest")
                .withStorageHashMap()
                .withMaxObjectAndItems(30, 100)
                .createInstance();
        assertNotNull(cache, "Created cache should not be null");
        assertNotNull( cache.getCacheConfig().getConfigGuid(), "Config GUID should not be null");
        assertNotNull( cache.getCacheGuid(), "Cache GUID should not be null");
        assertEquals(cache.getObjectsCount(), 0, "There should be 0 objects in cache");
        assertEquals(cache.getItemsCount(), 0, "There should be 0 objects in cache");
        assertEquals(cache.getClosed(), false, "Cache should not be closed");
        assertEquals(cache.getStorageKeys().size(), 1, "There should be 1 storage in cache");
        log.info("Config GUID: " + cache.getCacheConfig().getConfigGuid());
        log.info("Cache GUID: " + cache.getCacheGuid());
        log.info("Cache createdDateTime: " + cache.getCreatedDateTime());
        log.info("Cache getObjectsCount: " + cache.getObjectsCount());
        log.info("Cache getItemsCount: " + cache.getItemsCount());
        log.info("Cache getClosed: " + cache.getClosed());
        log.info("Cache storages: " + cache.getStorageKeys());
        for (int i=0; i<30; i++) {
            // get 30 times the same value
            String v = cache.withCache("key", key -> {
                CacheUtils.sleep(100);
                return "value ";
            }, CacheMode.modeTtlTenSeconds);
            log.info("Key=" + i + ", value= " + v);
            CacheUtils.sleep(50);
        }
        assertEquals(cache.getObjectsCount(), 1, "There should be 1 objects in cache");
        assertEquals(cache.getItemsCount(), 1, "There should be 1 objects in cache");
        log.info("Cache getItemsCount: " + cache.getItemsCount());
        cache.close();
        log.info("END-----");
    }
}
