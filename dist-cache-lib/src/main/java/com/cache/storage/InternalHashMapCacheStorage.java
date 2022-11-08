package com.cache.storage;

import com.cache.api.CacheObject;
import com.cache.api.StorageInitializeParameter;
import com.cache.base.CacheStorageBase;

import java.util.HashMap;

/** cache with internal HashMap */
public class InternalHashMapCacheStorage extends CacheStorageBase {

    /** */
    private java.util.HashMap<String, CacheObject> localCache = new HashMap<>();

    public InternalHashMapCacheStorage(StorageInitializeParameter p) {
    }

    /** get item from cache */
    public CacheObject getItem(String key) {
        return new CacheObject();
    }
    /** add item into cache  */
    public void setItem(CacheObject o) {
        return ;
    }

}