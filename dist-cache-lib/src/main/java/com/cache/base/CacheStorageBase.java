package com.cache.base;

import com.cache.api.CacheObject;
import com.cache.api.CacheableMethod;

/** base abstract class for storage to keep caches */
public abstract class CacheStorageBase {

    public abstract CacheObject getItem(String key);
    public abstract void setItem(CacheObject o);

    /** check if object is in cache for given key
     * if yes then get that object from cache
     * if no then run method to get item and put to cache to be add later */
    public Object withCache(String key, CacheableMethod m) {
        Object fromCache = getItem(key);
        if (fromCache != null) {
            return fromCache;
        } else {
            // Measure time of getting this object from cache
            Object objFromMethod = m.get(key);
            // TODO: add this object to cache

            return objFromMethod;

        }
    }

    // TODO: add more methods that could be via reflection String => Object


}
