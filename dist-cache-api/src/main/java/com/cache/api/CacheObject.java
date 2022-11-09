package com.cache.api;

import java.util.concurrent.atomic.AtomicLong;

/** class to be put to cache - it contains object caches AND many other statistics */
public class CacheObject {

    /** created time of this object in cache */
    private final long createdTimeMs = System.currentTimeMillis();
    /** key of this object stored in cache */
    private final String key;
    /** object to be push to cache */
    private final CacheableObject objectInCache;
    /** method to refresh this object in local cache */
    private final CacheableMethod methodToAcquire;
    /** counter of usages for current object in cache */
    private AtomicLong usages = new AtomicLong();
    /** */
    private long acquireTimeMs;
    public CacheObject(String key, CacheableObject o, long ackTimeMs, CacheableMethod method) {
        this.key = key;
        this.objectInCache = o;
        //objectInCache.getMode()

        //objectInCache.getSize()
        this.acquireTimeMs = ackTimeMs;
        this.methodToAcquire = method;
    }
    public String getKey() {
        return key;
    }
    public CacheableObject getValue() {
        return objectInCache;
    }
    /** release action for this cache object */
    public void releaseObject() {

    }

    /** use of this cache object */
    public long use() {
        return usages.incrementAndGet();
    }
    public long liveTime() {
        return System.currentTimeMillis() - createdTimeMs;
    }

}
