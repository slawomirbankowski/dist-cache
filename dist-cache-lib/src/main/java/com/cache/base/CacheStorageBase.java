package com.cache.base;

import com.cache.api.CacheObject;
import com.cache.api.CacheableMethod;
import com.cache.api.CacheableObject;
import com.cache.api.CacheableWrapper;

import java.util.Optional;

/** base abstract class for storage to keep caches */
public abstract class CacheStorageBase {

    /** get CacheObject item from cache by full key */
    public abstract Optional<CacheObject> getItem(String key);
    /** set item to cache and get previous item in cache for the same key */
    public abstract Optional<CacheObject> setItem(CacheObject o);
    /** returns true if storage is internal and cache objects are kept in local memory
     * false if storage is external and cache objects are kept in any storages like Redis, Elasticsearch, DB*/
    public abstract boolean isInternal();

    /** check if object is in cache for given key
     * if yes then get that object from cache
     * if no then run method to get item and put to cache to be add later */
    public <T> T withCache(String key, CacheableMethod<T> m) {
        Optional<CacheObject> fromCache = getItem(key);
        if (fromCache.isPresent()) {
            CacheObject co = fromCache.get();
            co.use();
            // TODO: this is not working
            //if (co.getValue() instanceof T) {
            //} else {
            //}
            return null;

        } else {

            // Measure time of getting this object from cache
            long startActTime = System.currentTimeMillis();
            T objFromMethod = m.get(key);
            long acquireTimeMs = System.currentTimeMillis()-startActTime;

            // TODO: add this object to cache
            CacheObject co = new CacheObject(key, new CacheableWrapper(objFromMethod), acquireTimeMs, m);
            setItem(co);
            return objFromMethod;

        }
    }


    // TODO: add more methods that could be via reflection String => Object


}
