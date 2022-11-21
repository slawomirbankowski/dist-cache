package com.cache.api;

import com.cache.utils.CacheUtils;

import java.io.*;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/** class to be put to cache - it contains object caches AND many other statistics
 * this cache is representing internal cache with object stored */
public class CacheObject {

    /** sequence of object in this JVM */
    public static AtomicLong globalObjectSeq = new AtomicLong();
    /** sequence of created object in this JVM */
    public long objectSeq = globalObjectSeq.incrementAndGet();
    /** created time of this object in cache */
    private long createdTimeMs = System.currentTimeMillis();
    /** last use time of this object in cache */
    private long lastUseTime = System.currentTimeMillis();
    private long lastRefreshTime = System.currentTimeMillis();
    /** key of this object stored in cache */
    private final String key;
    /** object to be push to cache */
    private Object objectInCache;
    /** method to refresh this object in local cache */
    private CacheableMethod methodToAcquire;
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

    /** creates object from deserialization */
    public CacheObject(long objectSeq, long createdTimeMs, long lastUseTime, long lastRefreshTime, String key,
                       Object objectInCache, CacheableMethod methodToAcquire, int objSize, long acquireTimeMs,
                       long usages, long refreshes, CacheMode mode, Set<String> groups) {
        this.objectSeq = objectSeq;
        this.createdTimeMs = createdTimeMs;
        this.lastUseTime = lastUseTime;
        this.lastRefreshTime = lastRefreshTime;
        this.key = key;
        this.objectInCache = objectInCache;
        this.methodToAcquire = methodToAcquire;
        this.objSize = objSize;
        this.acquireTimeMs = acquireTimeMs;
        this.usages.set(usages);
        this.refreshes.set(refreshes);
        this.mode = mode;
        this.groups = groups;
    }

    /** creates new object in memory */
    public CacheObject(String key, Object o, long acqTimeMs, CacheableMethod method, CacheMode mode, Set<String> groups) {
        this.key = key;
        this.objSize = CacheUtils.estimateSize(o);
        this.objectInCache = o;
        this.acquireTimeMs = acqTimeMs;
        this.methodToAcquire = method;
        this.mode = mode;
        this.groups = groups;
        calculateSize();
    }
    /** creates new object in memory */
    public CacheObject(String key, Object o, long acqTimeMs, CacheMode mode, Set<String> groups) {
        this(key, o, acqTimeMs, new CacheableMethod() {
            @Override
            public Object get(String key) {
                return o;
            }
        }, mode, groups);
    }
    /** creates new object in memory */
    public CacheObject(String key, Object o, long ackTimeMs, CacheMode mode) {
        this(key, o, ackTimeMs, mode, new HashSet<>());
    }
    /** creates new object in memory */
    public CacheObject(String key, Object o, long ackTimeMs) {
        this(key, o, ackTimeMs, CacheMode.modeTtlOneHour);
    }

    /** get simple serializable information about this object in cache */
    public CacheObjectInfo getInfo() {
        return new CacheObjectInfo(key, createdTimeMs, objectSeq, objSize, acquireTimeMs,
                usages.get(), refreshes.get(),
                mode.getMode(), timeToLive(), lastUseTime, lastRefreshTime,
                objectInCache.getClass().getName());
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
    /** refresh object using acquire method if object should be refreshed
     * returns size of object OR
     * -1 => Exception while acquiring
     * -2 => method to acquire is null
     * 0 => no need to refresh object
     * */
    public int refreshIfNeeded() {
        if (shouldBeRefreshed()) {
            lastRefreshTime = System.currentTimeMillis();
            if (methodToAcquire != null) {
                try {
                    long startAckTime = System.currentTimeMillis();
                    refreshes.incrementAndGet();
                    objectInCache = methodToAcquire.get(getKey());
                    acquireTimeMs = System.currentTimeMillis()-startAckTime;
                    calculateSize();
                    return objSize;
                } catch (Exception ex) {
                    return -1;
                }
            }
            return -2;
        }
        return 0;
    }
    /** serialize object in cache to byte[] */
    public byte[] serializeObjectInCache(CacheSerializer serializer) {
        return serializer.serialize(objectInCache);
    }
    /** serialize object in cache to String */
    public String serializeObjectInCacheToString(CacheSerializer serializer) {
        return new String(Base64.getEncoder().encode(serializer.serialize(objectInCache)));
    }
    /** get map with current values */
    public CacheObjectSerialized serializedFullCacheObject(CacheSerializer serializer) {
        byte[] serializedObj = serializeObjectInCache(serializer);
        return new CacheObjectSerialized(objectSeq, createdTimeMs, lastUseTime, lastRefreshTime, key, serializedObj,
                objSize, acquireTimeMs, usages.get(), refreshes.get(),
                mode.getMode(), mode.getTimeToLiveMs(), mode.getPriority(), mode.isAddToInternal(), mode.isAddToExternal(),
                groups);
    }
    /** write to Stream as blob, returns number of bytes written OR -1 if there is error while writing */
    public int writeToStream(CacheSerializer serializer, OutputStream outStream) {
        try {
            byte[] b = serializeObjectInCache(serializer);
            outStream.write(b);
            return b.length;
        } catch (IOException ex) {
            return -1;
        }
    }
    /** create cache object from serialized CacheObjectSerialized */
    public static CacheObject fromSerialized(CacheSerializer serializer, CacheObjectSerialized serialized) {
        return serialized.toCacheObject(serializer);
    }

}
