package com.cache.test;

import com.cache.DistFactory;
import com.cache.api.*;
import com.cache.interfaces.Cache;
import com.cache.utils.CacheUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheManagerDistributeTest {
    private static final Logger log = LoggerFactory.getLogger(CacheManagerDistributeTest.class);

    @Test
    public void distributeAgentsTest() {
        log.info("START------");
        Cache cache = DistFactory.buildDefaultFactory()
                .withName("GlobalCacheTest") // set friendly cache name
                .withDefaultSocketPort() // open TCP port for listening to commands and external cache agents
                .withCacheApp("https://localhost:8080/") // connect to cache standalone application to synchronize cache agents
                .withServers("localhost:9095") // try to connect to cache agents
                //.withStorageKafka("") // connect to Kafka brokers as storage
                //.withStorageElasticsearch("", "", "") // connect to Elasticsearch storage
                .withStorageHashMap() // add storage build of HashMap manager by cache
                .withObjectTimeToLive(100000) // set default time to live objects in cache for 100 seconds
                .withMaxObjectAndItems(30, 100) // set maximum number of objects to 30 and items to 100
                .createCacheInstance();

        log.info("Cache storages: " + cache.getStorageKeys());

        for (int test=0; test<50; test++) {
            long startTime = System.currentTimeMillis();
            for (int i=0; i<10+test*5; i++) {
                String v = cache.withCache("key"+i, key -> {
                    CacheUtils.sleep(80);
                    return "value for " + key;
                }, CacheMode.modeTtlThirtySeconds);
                //log.info("Test=" + test + ", i=" + i + ", value= " + v);
            }
            long totalTime = System.currentTimeMillis() - startTime;
            log.info("TEST " + test + ", TIME: " + totalTime + ", objectsInCache: " + cache.getObjectsCount());
            CacheUtils.sleep(1000);
        }
        log.info("Cache getObjectsCount: " + cache.getObjectsCount());
        cache.close();
        log.info("END-----");

    }
}
