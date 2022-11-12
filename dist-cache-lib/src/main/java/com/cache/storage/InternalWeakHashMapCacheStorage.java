package com.cache.storage;

import com.cache.api.CacheObject;
import com.cache.api.StorageInitializeParameter;
import com.cache.base.CacheStorageBase;

import java.util.*;

/** cache with internal WeakHashMap */
public class InternalWeakHashMapCacheStorage extends CacheStorageBase {

    /** */
    private WeakHashMap<String, CacheObject> localCache = new WeakHashMap<>();
    /** WeakHashMap is internal storage */
    public  boolean isInternal() { return true; }
    public InternalWeakHashMapCacheStorage(StorageInitializeParameter p) {
        super(p);
    }
    /** check if object has given key, optional with specific type */
    public boolean contains(String key) {
        return false;
    }
    /** */
    public Optional<CacheObject> getItem(String key) {
        return Optional.empty();
    }
    /** put object to cache */
    public Optional<CacheObject> setItem(CacheObject o) {
        return Optional.empty();
    }
    /** get number of items in cache */
    public int getItemsCount() {
        return localCache.size();
    }
    /** get number of objects in this cache */
    public int getObjectsCount() { return localCache.size(); }

    /** get keys for all cache items */
    public Set<String> getKeys(String containsStr) {
        return new HashSet<String>();
    }
    /** clear caches with given clear cache */
    public int clearCaches(int clearMode) {

        return 1;
    }
    /** clear cache contains given partial key */
    public int clearCacheContains(String str) {
        return 1;
    }
    /** check cache every X seconds to clear TTL caches */
    public void onTime(long checkSeq) {

    }
}
