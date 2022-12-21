package com.cache.test;

import com.cache.DistFactory;
import com.cache.api.*;
import com.cache.interfaces.Agent;
import com.cache.interfaces.Cache;
import com.cache.utils.CacheUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CacheManagerCleanTest {
    private static final Logger log = LoggerFactory.getLogger(CacheManagerCleanTest.class);

    @Test
    public void cleanTest() {
        log.info("START ------ clean test");
        Cache cache = DistFactory.buildDefaultFactory()
                .withName("GlobalCacheTest")
                .withStorageHashMap()
                .withObjectTimeToLive(CacheMode.TIME_FIVE_SECONDS)
                .withTimer(1000L, 1000L)
                .withMaxObjectAndItems(30, 100)
                .createCacheInstance();

        cache.getAgent();

        Agent agent = DistFactory.buildDefaultFactory()
                .withName("GlobalCacheTest")
                .withStorageHashMap()
                .withObjectTimeToLive(CacheMode.TIME_FIVE_SECONDS)
                .withTimer(1000L, 1000L)
                .withMaxObjectAndItems(30, 100)
                .createAgentInstance();


        //agent.createCacheService();
        //agent.createFlowService();

        assertNotNull(cache, "Created cache should not be null");
        // key_keep should be still kept in
        cache.withCache("key_keep", key -> "value", CacheMode.modeKeep);
        assertEquals(cache.getObjectsCount(), 1, "There should be 1 object in cache");
        // key_refresh should be refreshed
        cache.withCache("key_refresh", key -> ("value"+ CacheUtils.randomInt(100000)), CacheMode.modeRefreshTenSeconds);
        for (int i=0; i<30; i++) {
            String v = cache.withCache("key"+i, key -> "value");
            log.info("Objects in cache: " + cache.getObjectsCount() + ", keys: " + cache.getCacheKeys("") + ", key_refresh: " + cache.getObject("key_refresh"));
            CacheUtils.sleep(300);
        }
        assertTrue(cache.getObjectsCount() > 10, "There should be at least 10 objets in cache");
        assertTrue(cache.contains("key_keep"));
        for (int i=0; i<11; i++) {
            log.info("Objects in cache: " + cache.getObjectsCount() + ", keys: " + cache.getCacheKeys("" + ", key_refresh: " + cache.getObject("key_refresh")));
            CacheUtils.sleep(300);
        }
        List<CacheObjectInfo> objs = cache.getCacheInfos("");
        assertTrue(objs.size() > 20, "There should be at least 20 objects in cache");
        assertTrue(objs.size() < 30, "There should be at most 30 objects in cache");
        log.info("Cache getItemsCount: " + cache.getItemsCount() + ", keys: " + cache.getCacheKeys("") + ", key_refresh: " + cache.getObject("key_refresh"));
        cache.close();
        assertTrue(cache.getClosed(), "Cache should be closed");
        assertEquals(cache.getObjectsCount(), 0, "There should be no objects in cache after close");
        log.info("END-----");
    }
}
