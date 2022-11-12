package com.cache.api;

import java.util.concurrent.atomic.AtomicLong;

/** class to be put to cache - it contains object caches AND many other statistics */
public class CacheObjectInfo {

    public CacheObjectInfo(String key, long createdTimeMs, long objectSeq, int objSize, long acquireTimeMs,
                           long usagesCount, int mode, long timeToLiveMs, long lastUseTime, long lastRefreshTime,
                           String objectClassName) {

    }

}
