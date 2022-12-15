package com.cache.utils;

import java.util.concurrent.atomic.AtomicLong;

/** statistics about cache manager, objects, keys */
public class CacheStats {

    private long freeMemory;
    private long totalMemory;
    private long maxMemory;

    // TODO: add more cache stats per key, storage, ...

    private AtomicLong totalReads = new AtomicLong();
    private AtomicLong totalMissCount = new AtomicLong();
    private AtomicLong totalHitCount = new AtomicLong();

    public CacheStats() {
        refreshMemory();
    }

    public long getFreeMemory() {
        return freeMemory;
    }
    public long getTotalMemory() {
        return totalMemory;
    }
    public long getMaxMemory() {
        return maxMemory;
    }

    public void keyRead(String key) {
        totalReads.incrementAndGet();
    }
    public void keyMiss(String key) {
        totalMissCount.incrementAndGet();
    }
    public void keyHit(String key, String storage) {
        totalHitCount.incrementAndGet();
    }
    public void objectAcquire(String key, long ackTime) {
        totalReads.incrementAndGet();
    }
    /** refresh memory from Runtime  */
    public void refreshMemory() {
        Runtime rt = java.lang.Runtime.getRuntime();
        freeMemory = rt.freeMemory();
        totalMemory = rt.totalMemory();
        maxMemory = rt.maxMemory();
    }


}
