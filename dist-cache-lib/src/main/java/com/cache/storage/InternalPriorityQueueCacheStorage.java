package com.cache.storage;

import com.cache.api.CacheObject;
import com.cache.api.CacheObjectInfo;
import com.cache.api.StorageInitializeParameter;
import com.cache.base.CacheStorageBase;

import java.util.*;

/** cache with internal HashMap */
public class InternalPriorityQueueCacheStorage extends CacheStorageBase {

    /** objects in cache */
    private final java.util.concurrent.ConcurrentHashMap<String, CacheObject> localCache = new java.util.concurrent.ConcurrentHashMap<>();
    private final PriorityQueue<CacheObject> queue = new PriorityQueue<>();

    public InternalPriorityQueueCacheStorage(StorageInitializeParameter p) {
        super(p);
    }
    /** HashMap is internal storage */
    public  boolean isInternal() { return true; }
    /** check if object has given key, optional with specific type */
    public boolean contains(String key) {
        return localCache.containsKey(key);
    }
    /** get item from cache */
    public Optional<CacheObject> getObject(String key) {
        return Optional.ofNullable(localCache.get(key));
    }
    /** add item into cache  */
    public Optional<CacheObject> setObject(CacheObject o) {
        log.info("Set new item for cache, key: " + o.getKey());
        CacheObject prev = localCache.put(o.getKey(), o);
        if (prev != null) {
            prev.releaseObject();
        }
        // TODO: need to dispose object after removing from cache - this would be based on policy
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
    public List<CacheObjectInfo> getValues(String containsStr) {
        return new LinkedList<CacheObjectInfo>();
    }
    public void onTimeClean(long checkSeq) {
        for (Map.Entry<String, CacheObject> e: localCache.entrySet()) {

            // TODO: clear
            //e.getKey();
            //e.getValue().releaseObject();

        }
    }
    /** clear caches with given clear cache */
    public int clearCaches(int clearMode) {
        return 0;
    }

    /** clear cache contains given partial key */
    public int clearCacheContains(String str) {
        return 0;
    }
}
