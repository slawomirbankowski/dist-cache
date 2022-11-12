package com.cache.api;

import java.util.concurrent.atomic.AtomicLong;

/** class to be put to cache - it contains object caches AND many other statistics */
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
    /** cache mode */
    private CacheMode mode;

    public CacheObject(String key, Object o, long ackTimeMs, CacheableMethod method, CacheMode mode) {
        this.key = key;
        if (o instanceof CacheableObject) {
            this.objSize = ((CacheableObject) o).getSize();
        } else {
            this.objSize = CacheUtils.estimateSize(o);
        }
        this.objectInCache = o;
        this.acquireTimeMs = ackTimeMs;
        this.methodToAcquire = method;
        this.mode = mode;
        calculateSize();
    }
    public CacheObjectInfo getInfo() {
        return new CacheObjectInfo(key, createdTimeMs, objectSeq, objSize, acquireTimeMs,
                usages.get(), mode.getMode(), mode.getTimeToLiveMs(), lastUseTime, lastRefreshTime,
                objectInCache.getClass().getName());
    }
    private void calculateSize() {
        if (objectInCache instanceof CacheableObject) {
            this.objSize = ((CacheableObject) objectInCache).getSize();
        } else {
            this.objSize = CacheUtils.estimateSize(objectInCache);
        }
    }
    public String getKey() {
        return key;
    }
    public Object getValue() {
        return objectInCache;
    }
    public int getSize() { return objSize; }
    public String getClassName() {
        return objectInCache.getClass().getName();
    }
    public long getSeq() { return objectSeq; }
    public long getLastUseTime() { return lastUseTime; }
    public long getAcquireTimeMs() { return acquireTimeMs; }
    /** release action for this cache object */
    public void releaseObject() {
        if (objectInCache instanceof CacheableObject) {
            ((CacheableObject)objectInCache).releaseObject();
        }
    }

    public boolean keyContains(String str) {
        return key.contains(str);
    }
    /** use of this cache object */
    public long use() {
        lastUseTime = System.currentTimeMillis();
        return usages.incrementAndGet();
    }
    public long liveTime() {
        return System.currentTimeMillis() - createdTimeMs;
    }
    public boolean isOld() {
        return mode.isTtl() && (liveTime() > mode.getTimeToLiveMs());
    }
    public boolean shouldBeRefreshed() {
        return mode.isRefresh() && (System.currentTimeMillis()- lastRefreshTime>mode.getTimeToLiveMs());
    }
    /** refresh object using acquire method */
    public void refreshIfNeeded() {
        if (shouldBeRefreshed()) {
            lastRefreshTime = System.currentTimeMillis();
            try {
                long startAckTime = System.currentTimeMillis();
                objectInCache = methodToAcquire.get(getKey());
                acquireTimeMs = System.currentTimeMillis()-startAckTime;
                calculateSize();
            } catch (Exception ex) {
            }
        }
    }

}
