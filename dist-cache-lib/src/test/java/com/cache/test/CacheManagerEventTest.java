package com.cache.test;

import com.cache.DistCacheFactory;
import com.cache.api.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CacheManagerEventTest {
    private static final Logger log = LoggerFactory.getLogger(CacheManagerEventTest.class);

    @Test
    public void eventsTest() {
        log.info("START------");
        AtomicLong cleanEvents = new AtomicLong();
        AtomicLong storageEvents = new AtomicLong();
        Cache cache = DistCacheFactory.buildDefaultFactory()
                .withName("GlobalCacheTest")
                .withStorageHashMap()
                .withObjectTimeToLive(CacheMode.TIME_TEN_SECONDS)
                .withCallback(CacheEvent.EVENT_CACHE_START, x -> {
                    log.info("::::::::: EVENT START CALLBACK");

                    return "OK";
                })
                .withCallback(CacheEvent.EVENT_TIMER_CLEAN, x -> {
                    log.info("::::::::: EVENT TIMER CLEAN");
                    cleanEvents.incrementAndGet();
                    return "OK";
                })
                .withCallback(CacheEvent.EVENT_INITIALIZE_STORAGE, x -> {
                    log.info("::::::::: STORAGE NEW");
                    storageEvents.incrementAndGet();
                    return "OK";
                })
                .withTimer(1000L, 1000L)
                .withMaxEvents(100)
                .withMaxObjectAndItems(30, 100)
                .createInstance();
        CacheUtils.sleep(15000);
        assertTrue(storageEvents.get() >= 1, "There should be 1 event for storage");
        assertTrue(cleanEvents.get() >= 13, "There should be at least 13 events for clean");
        cache.close();
        log.info("END-----");
    }
}
