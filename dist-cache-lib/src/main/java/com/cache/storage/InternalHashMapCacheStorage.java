package com.cache.storage;

import com.cache.api.CacheObject;
import com.cache.api.StorageInitializeParameter;
import com.cache.base.CacheStorageBase;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** cache with internal HashMap */
public class InternalHashMapCacheStorage extends CacheStorageBase {

    /** objects in cache */
    private final java.util.HashMap<String, CacheObject> localCache = new HashMap<>();

    public InternalHashMapCacheStorage(StorageInitializeParameter p) {
    }
    /** HashMap is internal storage */
    public  boolean isInternal() { return true; }
    /** check if object has given key, optional with specific type */
    public boolean contains(String key) {
        return false;
    }
    /** get item from cache */
    public Optional<CacheObject> getItem(String key) {
        return Optional.of(localCache.get(key));
    }
    /** add item into cache  */
    public Optional<CacheObject> setItem(CacheObject o) {
        CacheObject prev = localCache.put(o.getKey(), o);
        if (prev != null) {
            prev.releaseObject();
        }
        // TODO: need to dispose object after removing from cache - this would be based on policy
        return Optional.empty();
    }

    public void onTime() {
        for (Map.Entry<String, CacheObject> e: localCache.entrySet()) {

            e.getKey();
            e.getValue().releaseObject();

        }
    }

}
