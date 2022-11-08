package com.cache.api;

/** class to be put to cache - it contains object caches AND many other statistics */
public class CacheObject {

    /** created time of this object in cache */
    private long createdTimeMs = System.currentTimeMillis();
    /** key of this object stored in cache */
    private String key = "";
    /** object to be push to cache */
    private CacheableObject obj;

}
