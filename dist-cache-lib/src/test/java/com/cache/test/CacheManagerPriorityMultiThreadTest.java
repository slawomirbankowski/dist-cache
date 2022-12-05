package com.cache.test;

import com.cache.DistFactory;
import com.cache.interfaces.Cache;
import com.cache.api.CacheMode;
import com.cache.utils.CacheUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CacheManagerPriorityMultiThreadTest {
    private static final Logger log = LoggerFactory.getLogger(CacheManagerPriorityMultiThreadTest.class);

    @Test
    public void testMultiThread() {
        log.info("START------");
        Cache cache = DistFactory.buildEmptyFactory()
                .withName("GlobalCacheTest")
                .withStoragePriorityQueue()
                .withObjectTimeToLive(CacheMode.TIME_ONE_HOUR)
                .withMaxObjectAndItems(100, 1000)
                .createCacheInstance();
        log.info("Cache storages: " + cache.getStorageKeys());
        int maxThreads = 10;
        log.info("CURRENT OBJECTS BEFORE: " + cache.getObjectsCount());
        ReadingWritingPriorityThread[] threaads = new ReadingWritingPriorityThread[maxThreads];
        for (int th=0; th<maxThreads; th++) {
            ReadingWritingPriorityThread thread = new ReadingWritingPriorityThread();
            thread.cache = cache;
            thread.maxKeys = 200;
            thread.start();
            threaads[th] = thread;
        }
        log.info("CURRENT OBJECTS AFTER INSERTING: " + cache.getObjectsCount());
        // test should take 10 seconds
        CacheUtils.sleep(20000);
        for (int th=0; th<maxThreads; th++) {
            threaads[th].working = false;
        }
        log.info("CURRENT OBJECTS AFTER 10 seconds: " + cache.getObjectsCount());
        assertTrue(cache.getObjectsCount() <= 1000);
        // wait 1 second to finish all tests
        CacheUtils.sleep(1000);
        cache.close();
        log.info("END-----");
    }
}
class ReadingWritingPriorityThread extends Thread {
    private static final Logger log = LoggerFactory.getLogger(ReadingWritingPriorityThread.class);
    public Cache cache;
    public int maxKeys = 500;
    public boolean working = true;

    public void run() {
        while (working) {
            try {
                long startTime = System.currentTimeMillis();
                for (int i=0; i<50; i++) {
                    String key = "key" + CacheUtils.randomInt(maxKeys);
                    int priority = CacheUtils.randomInt(10);
                    String v = cache.withCache(key, k -> {
                        CacheUtils.sleep(1);
                        return "value for " + k;
                    }, CacheMode.modePriority(priority));
                    //String keyToClear = "key" + CacheUtils.randomInt(5000);
                    //cache.clearCacheContains(keyToClear);
                }
                long totalTime = System.currentTimeMillis() - startTime;
            } catch (Exception ex) {
                log.error("Error while testing multi-thread cache, reason: " + ex.getMessage(), ex);
            }
            CacheUtils.sleep(1);
        }
    }

}