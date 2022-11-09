package com.cache.base;

import com.cache.api.CacheObject;
import com.cache.api.CacheableMethod;
import com.cache.api.CacheableObject;
import com.cache.api.CacheableWrapper;

import java.util.Optional;
import java.util.UUID;

/** base abstract class for storage to keep caches */
public abstract class CacheStorageBase {

    /** unique identifier of this storage */
    private String storageUid = UUID.randomUUID().toString();
    /** check if object has given key, optional with specific type */
    public abstract boolean contains(String key);
    /** get CacheObject item from cache by full key */
    public abstract Optional<CacheObject> getItem(String key);
    /** set item to cache and get previous item in cache for the same key */
    public abstract Optional<CacheObject> setItem(CacheObject o);
    /** returns true if storage is internal and cache objects are kept in local memory
     * false if storage is external and cache objects are kept in any storages like Redis, Elasticsearch, DB*/
    public abstract boolean isInternal();
    /** dispose this storage if needed */
    public void disposeStorage() {
        // by default no dispose - it could be overriden by any storage
    }
    /** check if object is in cache for given key
     * if yes then get that object from cache
     * if no then run method to get item and put to cache to be add later */
    public <T> T withCache(String key, CacheableMethod<T> m) {
        Optional<CacheObject> fromCache = getItem(key);
        if (fromCache.isPresent()) {
            try {
                CacheObject co = fromCache.get();
                co.use();
                // TODO: if this is not internal cache - need to increase usage and lastUseDate ???
                return (T)co.getValue();
            } catch (Exception ex) {
                // TODO: report incorrect type expected from cache for that key - maybe callback method
                return acquireObject(key, m);
            }
        } else {
            return acquireObject(key, m);
        }
    }
    private <T> T acquireObject(String key, CacheableMethod<T> m) {
        // Measure time of getting this object from cache
        long startActTime = System.currentTimeMillis();
        T objFromMethod = m.get(key);
        long acquireTimeMs = System.currentTimeMillis()-startActTime; // this is time of getting this object from method
        // TODO: add this object to cache
        CacheObject co = new CacheObject(key, new CacheableWrapper(objFromMethod), acquireTimeMs, m);
        Optional<CacheObject> prev = setItem(co);
        prev.ifPresent(CacheObject::releaseObject);
        return objFromMethod;
    }
    // TODO: add more methods that could be via reflection String => Object


}
