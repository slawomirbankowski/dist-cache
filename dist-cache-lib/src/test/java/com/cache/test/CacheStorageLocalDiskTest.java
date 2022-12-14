package com.cache.test;

import com.cache.DistFactory;
import com.cache.api.CacheMode;
import com.cache.api.CacheObject;
import com.cache.interfaces.Cache;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CacheStorageLocalDiskTest {
    private static final Logger log = LoggerFactory.getLogger(CacheStorageLocalDiskTest.class);

    @Test
    public void storageLocalDiskTest() {
        log.info("START------");
        Cache cache = DistFactory.buildEmptyFactory()
                .withName("GlobalCacheTest")
                .withCacheStorageLocalDisk("../../")
                .withSerializerDefault()
                .withSerializer("java.lang.String=StringSerializer,default=ObjectStreamSerializer")
                .withRegistrationJdbc("jdbc:postgresql://localhost:5432/cache01", "org.postgresql.Driver",
                        "cache_user", "cache_password123")
                .withCacheMaxObjectsAndItems(30, 100)
                .createCacheInstance();

        var storageKeys = cache.getStorageKeys();
        log.info("Storage keys:" + storageKeys);
        assertEquals(1, storageKeys.size(), "There should be exactly one storage");

        cache.clearCacheContains("");
        assertEquals(0, cache.getObjectsCount(), "There should be 0 objects in cache");

        Object[] objs = new Object[] {
                "value5",
                Map.of("aaaa", "1111", "bbbb", "2222"),
                new BasicTestObject("aaaa", 1111),
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                List.of("aaaaa", "bbbbbb", "cccccc", "dddddd"),
                new ComplexTestObject(10)
        };

        for (int i=0; i< objs.length; i++) {
            cache.setCacheObject("key" + i, objs[i], CacheMode.modeTtlOneHour);
        }
        assertEquals(6, cache.getObjectsCount(), "There should be 6 objects in cache");

        for (int i=0; i< objs.length; i++) {
            var co = cache.getCacheObject("key" + i);
            if (co.isPresent()) {
                CacheObject o = co.get();
                log.info("-----------------------------------------------------" + i);
                log.info("OBJECT:" + i + ", type=" + objs[i].getClass().getName() + ", VALUE=" + objs[i]);
                log.info("CACHE =" + i + ", type=" + o.getClassName() + ", VALUE=" + o.getValue() + ", size=" + o.getSize() + ", priority=" + o.getPriority());
            } else {
                log.info("OBJECT[" + i + ", key: key" + i + ", original: " + objs[i].getClass().getName() + ", NO OBJ IN CACHE[" + i + "]");
            }
        }

        var allKeys = cache.getCacheKeys("", true);
        log.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! Cache keys:" + allKeys);
        assertEquals(6, allKeys.size(), "There should be 6 keys in cache");

        cache.clearCacheContains("key3");

        var allButOneKeys = cache.getCacheKeys("", true);
        log.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! Cache keys after delete key3:" + allButOneKeys);
        assertEquals(5, allButOneKeys.size(), "There should be 5 keys in cache after deleting 1 from 6");

        var cacheValuesAll = cache.getCacheInfos("", true);
        cacheValuesAll.stream().forEach(cv -> {
            log.info("CACHE =" + cv.getKey() + ", type=" + cv.getObjectClassName() + ", size=" + cv.getObjSize() + ", ackTime=" + cv.getAcquireTimeMs() + ", ttl=" + cv.getTimeToLiveMs());
        });

        log.info("-----------------------------------------------------");
        log.info("Cache getItemsCount: " + cache.getItemsCount());
        cache.close();
        log.info("END-----");
    }
}
