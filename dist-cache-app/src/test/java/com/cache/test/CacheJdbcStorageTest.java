package com.cache.test;

import com.cache.DistFactory;
import com.cache.interfaces.Cache;
import com.cache.api.CacheMode;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CacheJdbcStorageTest {
    private static final Logger log = LoggerFactory.getLogger(CacheJdbcStorageTest.class);

    @Test
    public void simpleTest() {
        log.info("START------");
        Cache cache = DistFactory.buildDefaultFactory()
                .withName("GlobalCacheTest")
                .withStorageJdbc("jdbc:postgresql://localhost:5432/cache01", "org.postgresql.Driver",
                        "cache_user", "cache_password123")
                .withMaxObjectAndItems(30, 100)
                .createCacheInstance();

        cache.setCacheObject("key4", "value444444");
        cache.setCacheObject("key5", "value5", CacheMode.modeTtlOneMinute);

        //String value = cache.withCache("key1", x -> "value1");
        //log.info("Cache value: " + value);
        var co = cache.getCacheObject("key3");

        if (co.isPresent()) {
            log.info("OBJECT_IN_CACHE===" + co.get().serializedFullCacheObject(cache.getCacheSerializer()));
        } else {
            log.info("NO OBJ IN CACHE");
        }

        cache.clearCacheContains("key1");

        log.info("Cache getItemsCount: " + cache.getItemsCount());
        cache.close();
        log.info("END-----");
    }
}
