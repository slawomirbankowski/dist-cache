package com.cache.storage;

import com.cache.api.*;
import com.cache.base.CacheStorageBase;

import java.util.*;
import java.util.stream.Collectors;

/** cache with internal HashMap */
public class InternalHashMapCacheStorage extends CacheStorageBase {

    /** objects in cache */
    private final java.util.HashMap<String, CacheObject> localCache = new HashMap<>();

    public InternalHashMapCacheStorage(StorageInitializeParameter p) {
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
    /** get number of items in cache */
    public int getItemsCount() {
        // TODO: calculate number of items
        return localCache.size();
    }
    /** get number of objects in this cache */
    public int getObjectsCount() { return localCache.size(); }

    /** get keys for all cache items */
    public Set<String> getKeys(String containsStr) {
        return localCache.keySet();
    }
    /** get info values */
    public List<CacheObjectInfo> getValues(String containsStr) {
        return localCache.values()
                .stream()
                .map(CacheObject::getInfo)
                .collect(Collectors.toList());
    }
    public void onTimeClean(long checkSeq) {
        log.info("CLEARING objects in cache HashMap, check: " + checkSeq + ", size: " + localCache.size() + ", max:" + maxObjects);
        // TODO: no need to perform this every single time
        List<String> oldKeys = localCache.values()
                .stream()
                .filter(CacheObject::isOld)
                .map(CacheObject::getKey)
                .collect(Collectors.toList());
        // remove old TTL items
        oldKeys.forEach(keyToRemove -> removeObjectByKey(keyToRemove));
        localCache.values()
                .stream()
                .forEach(CacheObject::refreshIfNeeded);
        if (localCache.size() > maxObjects) {
            // check if there is too many items in cache even after deleting old ones
            // TODO: implement removing some objects based on policy
            for (Map.Entry<String, CacheObject> e: localCache.entrySet()) {
                e.getValue().isOld();
                // TODO: clear
                //e.getKey();
                //e.getValue().releaseObject();
            }
        }
    }
    /** remove all objects by keys */
    public void removeObjectsByKeys(List<String> keys) {
        // TODO: add removed items
        keys.forEach(keyToRemove -> removeObjectByKey(keyToRemove));
    }
    public void removeObjectByKey(String key) {
        CacheObject prev = localCache.remove(key);
        if (prev != null) {
            prev.releaseObject();
        }
    }
    /** clear caches with given clear cache method */
    public int clearCaches(int clearMode) {
        // TODO: implement clearing caches with given mode
        return 0;
    }

    /** clear cache contains given partial key */
    public int clearCacheContains(String str) {
        List<String> keys = localCache.values()
                .stream()
                .filter(x -> x.keyContains(str))
                .map(CacheObject::getKey)
                .collect(Collectors.toList());
        removeObjectsByKeys(keys);
        return 0;
    }
}
