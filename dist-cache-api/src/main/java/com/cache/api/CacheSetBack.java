package com.cache.api;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/** class to be put to cache - it contains object caches AND many other statistics
 * this cache is representing internal cache with object stored */
public class CacheSetBack {

    private List<CacheObject> prevObjects;
    private CacheObject currentObject;

    public CacheSetBack(List<CacheObject> prevObjects, CacheObject currentObject) {
        this.prevObjects = prevObjects;
        this.currentObject = currentObject;
    }
    public List<CacheObject> getPrevObjects() {
        return prevObjects;
    }
    public CacheObject getCurrentObject() {
        return currentObject;
    }
    public CacheSetBackInfo toInfo() {
        var prevs = prevObjects.stream().map(x -> x.getInfo()).collect(Collectors.toList());
        var curr = currentObject.getInfo();
        return new CacheSetBackInfo(prevs, curr);
    }
}
