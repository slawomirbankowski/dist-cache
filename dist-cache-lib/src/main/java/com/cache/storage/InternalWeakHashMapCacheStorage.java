package com.cache.storage;

import com.cache.api.CacheClearMode;
import com.cache.api.CacheObject;
import com.cache.api.CacheObjectInfo;
import com.cache.api.StorageInitializeParameter;
import com.cache.base.CacheStorageBase;

import java.util.*;

/** cache with internal WeakHashMap */
public class InternalWeakHashMapCacheStorage extends CacheStorageBase {

    /** weak hash map */
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
    public Optional<CacheObject> getObject(String key) {
        return Optional.empty();
    }
    /** put object to cache */
    public Optional<CacheObject> setObject(CacheObject o) {
        return Optional.empty();
    }
    /** remove objects in cache storage by keys */
    public void removeObjectsByKeys(List<String> keys) {
    }
    /** remove object in cache storage by key */
    public void removeObjectByKey(String key) {
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
    /** get info values */
    public List<CacheObjectInfo> getInfos(String containsStr) {
        return new LinkedList<CacheObjectInfo>();
    }

    /** get values of cache objects that contains given String in key */
    public List<CacheObject> getValues(String containsStr) {
        return new LinkedList<CacheObject>();
    }
    /** clear caches with given clear cache */
    public int clearCacheForGroup(String groupName) {
        return 1;
    }
    /** clear cache by given mode
     * returns estimated of elements cleared */
    public int clearCache(CacheClearMode clearMode) {
        return -1;
    }
    /** clear cache contains given partial key */
    public int clearCacheContains(String str) {
        return 1;
    }
    /** check cache every X seconds to clear TTL caches */
    public void onTimeClean(long checkSeq) {

    }
}
