package com.cache.api;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/** object to get back after set new object in cache */
public class CacheSetBack {

    /** previous object in cache */
    private List<CacheObject> prevObjects;
    /** current object added to cache */
    private CacheObject currentObject;

    /** */
    public CacheSetBack(List<CacheObject> prevObjects, CacheObject currentObject) {
        this.prevObjects = prevObjects;
        this.currentObject = currentObject;
    }
    /** */
    public List<CacheObject> getPrevObjects() {
        return prevObjects;
    }
    /** */
    public CacheObject getCurrentObject() {
        return currentObject;
    }
    /** replace this to info structure to be returned to user via JSON from dist-cache application */
    public CacheSetBackInfo toInfo() {
        var prevs = prevObjects.stream().map(x -> x.getInfo()).collect(Collectors.toList());
        var curr = currentObject.getInfo();
        return new CacheSetBackInfo(prevs, curr);
    }
}
