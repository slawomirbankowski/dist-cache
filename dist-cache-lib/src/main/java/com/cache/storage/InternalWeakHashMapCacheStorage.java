package com.cache.storage;

import com.cache.api.CacheObject;
import com.cache.api.StorageInitializeParameter;
import com.cache.base.CacheStorageBase;

import java.util.HashMap;
import java.util.Optional;
import java.util.WeakHashMap;

/** cache with internal WeakHashMap */
public class InternalWeakHashMapCacheStorage extends CacheStorageBase {

    /** */
    private WeakHashMap<String, CacheObject> localCache = new WeakHashMap<>();
    /** WeakHashMap is internal storage */
    public  boolean isInternal() { return true; }
    public InternalWeakHashMapCacheStorage(StorageInitializeParameter p) {
    }
    /** */
    public Optional<CacheObject> getItem(String key) {
        return Optional.empty();
    }
    /** put object to cache */
    public Optional<CacheObject> setItem(CacheObject o) {
        return Optional.empty();
    }

}
