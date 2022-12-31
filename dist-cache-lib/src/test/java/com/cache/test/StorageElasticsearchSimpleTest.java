package com.cache.test;

import com.cache.utils.HttpConnectionHelper;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class StorageElasticsearchSimpleTest {
    private static final Logger log = LoggerFactory.getLogger(StorageElasticsearchSimpleTest.class);

    @Test
    public void elasticsearchStorageSimpleTest() {
        log.info("START ------ ");

        String elasticUrl = "https://localhost:9200";

        HttpConnectionHelper conn = new HttpConnectionHelper(elasticUrl);

        conn.callHttpGet("/_cat/indices?format=json&pretty");


/*
        Cache cache = DistFactory.buildEmptyFactory()
                .withName("GlobalCacheTest")
                .withStorageElasticsearch(elasticUrl, "elastic", "elastic")
                .withObjectTimeToLive(CacheMode.TIME_ONE_DAY)
                .withTimer(CacheMode.TIME_ONE_HOUR, CacheMode.TIME_ONE_HOUR)
                .withMaxObjectAndItems(3000, 10000000)
                .createCacheInstance();

        cache.clearCacheContains("");
        assertEquals(0, cache.getObjectsCount(), "There should be NO objects in cache");

        for (int i=0; i<10; i++) {
            cache.withCache("key" + i, key -> "value", CacheMode.modeTtlOneMinute);
            assertEquals(i+1, cache.getObjectsCount(), "There should be " + (i+1) + " values in cache");
            var value = cache.getCacheObject("key" + i);
            assertTrue(value.isPresent(), "There should be value for key: key" + i);
            assertEquals("value", value.get().getValue());
            log.info("Objects in cache: " + cache.getObjectsCount() + ", keys: " + cache.getCacheKeys("") + ", current:" + value.get().getInfo());
        }
        for (int i=0; i<100; i++) {
            int keyNum = CacheUtils.randomInt(10);
            String v = cache.withCache("key"+keyNum, key -> "value" + keyNum, CacheMode.modeTtlOneMinute);
            log.info("Objects in cache: " + cache.getObjectsCount());
        }
        assertEquals(10, cache.getObjectsCount(), "There should be 10 values in cache");

        assertEquals(1, cache.getCacheKeys("key7", true).size(), "There should be 1 key in cache that contains key7");

        cache.clearCacheContains("key");

        assertEquals(0, cache.getObjectsCount(), "There should be NO objects in cache");

        cache.clearCacheContains("");

        cache.close();


 */
        log.info("END-----");
    }
}
