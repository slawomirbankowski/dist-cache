package com.cache.storage;

import com.cache.api.CacheObject;
import com.cache.api.StorageInitializeParameter;
import com.cache.base.CacheStorageBase;

import java.util.HashMap;
import java.util.WeakHashMap;

/** cache with internal WeakHashMap */
public class InternalWeakHashMapCacheStorage extends CacheStorageBase {

    /** */
    private WeakHashMap<String, CacheObject> localCache = new WeakHashMap<>();

    public InternalWeakHashMapCacheStorage(StorageInitializeParameter p) {
    }
    /** */
    public CacheObject getItem(String key) {
        return new CacheObject();
    }
    /** put object to cache */
    public void setItem(CacheObject o) {
        return ;
    }

}