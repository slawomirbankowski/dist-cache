package com.cache.storage;

import com.cache.api.CacheObject;
import com.cache.api.StorageInitializeParameter;
import com.cache.base.CacheStorageBase;

import java.util.HashMap;
import java.util.Map;

/** cache with internal HashMap */
public class InternalHashMapCacheStorage extends CacheStorageBase {

    /** objects in cache */
    private java.util.HashMap<String, CacheObject> localCache = new HashMap<>();

    public InternalHashMapCacheStorage(StorageInitializeParameter p) {
    }
    /** HashMap is internal storage */
    public  boolean isInternal() { return true; }
    /** get item from cache */
    public CacheObject getItem(String key) {

        return localCache.get(key);
    }
    /** add item into cache  */
    public void setItem(CacheObject o) {
        CacheObject prev = localCache.put(o.getKey(), o);
        if (prev != null) {
            prev.releaseObject();
        }
        // TODO: need to dispose object after removing from cache - this would be based on policy
        return ;
    }

    public void onTime() {
        for (Map.Entry<String, CacheObject> e: localCache.entrySet()) {

            e.getKey();
            e.getValue().releaseObject();

        }
    }

}
