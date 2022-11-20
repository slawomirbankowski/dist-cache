package com.cache.api;

import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/** class to be put to cache - it contains object caches AND many other statistics
 * this cache is representing internal cache with object stored */
public class CacheObject {

    /** sequence of object in this JVM */
    public static AtomicLong globalObjectSeq = new AtomicLong();
    /** sequence of created object in this JVM */
    public final long objectSeq = globalObjectSeq.incrementAndGet();
    /** created time of this object in cache */
    private final long createdTimeMs = System.currentTimeMillis();
    /** last use time of this object in cache */
    private long lastUseTime = System.currentTimeMillis();
    private long lastRefreshTime = System.currentTimeMillis();
    /** key of this object stored in cache */
    private final String key;
    /** object to be push to cache */
    private Object objectInCache;
    /** method to refresh this object in local cache */
    private final CacheableMethod methodToAcquire;
    /** size of object */
    private int objSize = 1;
    /** time of get this object from external sources, time to acquire in milliseconds */
    private long acquireTimeMs;

    /** counter of usages for current object in cache */
    private AtomicLong usages = new AtomicLong();
    /** counter for refreshes */
    private AtomicLong refreshes = new AtomicLong();
    /** cache mode */
    private CacheMode mode;
    /** set of groups to identify cache with, these groups are helpful with clearing caches */
    private Set<String> groups;

    public CacheObject(String key, Object o, long ackTimeMs, CacheableMethod method, CacheMode mode, Set<String> groups) {
        this.key = key;
        this.objSize = CacheUtils.estimateSize(o);
        this.objectInCache = o;
        this.acquireTimeMs = ackTimeMs;
        this.methodToAcquire = method;
        this.mode = mode;
        this.groups = groups;
        calculateSize();
    }
    /** get simple serializable information about this object in cache */
    public CacheObjectInfo getInfo() {
        return new CacheObjectInfo(key, createdTimeMs, objectSeq, objSize, acquireTimeMs,
                usages.get(), refreshes.get(),
                mode.getMode(), timeToLive(), lastUseTime, lastRefreshTime,
                objectInCache.getClass().getName());
    }
    /** serialize underlying object to String */
    public String serialize() {
        return objectInCache.toString();
    }
    /** try to calculate size of this object as estimated number of objects */
    private void calculateSize() {
        this.objSize = CacheUtils.estimateSize(objectInCache);
    }
    /** get key of object in cache */
    public String getKey() {
        return key;
    }
    /** get value of object in cache */
    public Object getValue() {
        return objectInCache;
    }
    /** get estimated size of object in cache - the size is counting objects in memory rather than bytes or real memory used */
    public int getSize() { return objSize; }
    /** get full name of class in cache */
    public String getClassName() {
        return objectInCache.getClass().getName();
    }
    /** get sequence of object in cache - sequence is created from 1 */
    public long getSeq() { return objectSeq; }
    /** get time of last use this cache */
    public long getLastUseTime() { return lastUseTime; }
    /** get acquire time of getting this object from aquire method */
    public long getAcquireTimeMs() { return acquireTimeMs; }
    /** release action for this cache object - by default there is no action for releasing
     * GC should normally dispose this object */
    public void releaseObject() {
    }

    /** check if key of this object contains given string */
    public boolean keyContains(String str) {
        return key.contains(str);
    }
    /** use of this cache object */
    public long use() {
        lastUseTime = System.currentTimeMillis();
        return usages.incrementAndGet();
    }
    /** get actual live time for this object */
    public long liveTime() {
        return System.currentTimeMillis() - createdTimeMs;
    }
    public long timeToLive() {
        return mode.getTimeToLiveMs() - (System.currentTimeMillis() - createdTimeMs);
    }
    /** if this object is old - it means that is TTL mode and live time is longer than declared time to live */
    public boolean isOld() {
        return mode.isTtl() && (liveTime() > mode.getTimeToLiveMs());
    }
    /** check if this object should be refreshed
     * it means that object has REFRESH mode and last refresh time is longer than time to live */
    public boolean shouldBeRefreshed() {
        return mode.isRefresh() && (System.currentTimeMillis()- lastRefreshTime>mode.getTimeToLiveMs());
    }
    /** refresh object using acquire method if object should be refreshed */
    public void refreshIfNeeded() {
        if (shouldBeRefreshed()) {
            lastRefreshTime = System.currentTimeMillis();
            try {
                long startAckTime = System.currentTimeMillis();
                refreshes.incrementAndGet();
                objectInCache = methodToAcquire.get(getKey());
                acquireTimeMs = System.currentTimeMillis()-startAckTime;
                calculateSize();
            } catch (Exception ex) {
            }
        }
    }

}
