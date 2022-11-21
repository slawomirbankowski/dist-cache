package com.cache.test;

import com.cache.DistCacheFactory;
import com.cache.api.Cache;
import com.cache.api.CacheMode;
import com.cache.utils.CacheUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CacheManagerMultiThreadTest {
    private static final Logger log = LoggerFactory.getLogger(CacheManagerMultiThreadTest.class);

    public static void main(String[] args) {
        log.info("START------");
        Cache cache = DistCacheFactory.buildDefaultFactory()
                .withName("GlobalCacheTest")
                .withStorageHashMap()
                .withObjectTimeToLive(CacheMode.TIME_ONE_HOUR)
                .withMaxObjectAndItems(1000, 3000)
                .createInstance();
        log.info("Cache storages: " + cache.getStorageKeys());
        int maxThreads = 10;
        ReadingWritingThread[] threaads = new ReadingWritingThread[maxThreads];
        for (int th=0; th<maxThreads; th++) {
            ReadingWritingThread thread = new ReadingWritingThread();
            thread.cache = cache;
            thread.start();
            threaads[th] = thread;
        }
        // test should take 10 seconds
        CacheUtils.sleep(10000);
        for (int th=0; th<maxThreads; th++) {
            threaads[th].working = false;
        }
        assertTrue(cache.getCacheValues("").size() > 100);
        // wait 1 second to finish all tests
        CacheUtils.sleep(1000);
        cache.close();
        log.info("END-----");
    }
}

class ReadingWritingThread extends Thread {
    private static final Logger log = LoggerFactory.getLogger(ReadingWritingThread.class);
    public Cache cache;
    public boolean working = true;

    public void run() {
        while (working) {
            try {
                long startTime = System.currentTimeMillis();
                for (int i=0; i<50; i++) {
                    String key = "key" + CacheUtils.randomInt(500);
                    String v = cache.withCache(key, k -> {
                        CacheUtils.sleep(1);
                        return "value for " + k;
                    }, CacheMode.modeTtlOneHour);
                    String keyToClear = "key" + CacheUtils.randomInt(5000);
                    cache.clearCacheContains(keyToClear);
                }
                long totalTime = System.currentTimeMillis() - startTime;
            } catch (Exception ex) {
                log.error("Error while testing multi-thread cache, reason: " + ex.getMessage(), ex);
            }
            CacheUtils.sleep(1);
        }
    }

}