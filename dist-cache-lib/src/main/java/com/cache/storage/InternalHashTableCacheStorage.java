package com.cache.storage;

import com.cache.api.*;
import com.cache.api.enums.CacheStorageType;
import com.cache.api.info.CacheObjectInfo;
import com.cache.base.CacheStorageBase;
import com.cache.interfaces.Cache;

import java.util.*;
import java.util.stream.Collectors;

/** cache with internal HashMap */
public class InternalHashTableCacheStorage extends CacheStorageBase {

    /** objects in cache */
    private final  Map<String, CacheObject> localCache;

    public InternalHashTableCacheStorage(Cache cache) {
        super(cache);
        localCache = Collections.synchronizedMap(new java.util.Hashtable<>((int)maxObjects));
    }
    /** Hashtable is internal storage */
    public boolean isInternal() { return true; }
    /** returns true if storage is global,
     * it means that one global shared storage is available for all cache instances*/
    public boolean isGlobal() { return false; }

    /**  get additional info parameters for this storage */
    public Map<String, Object> getStorageAdditionalInfo() {
        return Map.of("className", localCache.getClass().getName());
    }
    /** get type of this storage */
    public CacheStorageType getStorageType() {
        return CacheStorageType.memory;
    }
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
        log.trace("Set new item for cache, key: " + o.getKey());
        CacheObject prev = localCache.put(o.getKey(), o);
        if (prev != null) {
            prev.releaseObject();
            return Optional.of(prev);
        } else {
            return Optional.empty();
        }
    }
    /** get number of items in cache */
    public int getItemsCount() {
        return localCache.values().stream().mapToInt(o -> o.getSize()).sum();
    }
    /** get number of objects in this cache */
    public int getObjectsCount() { return localCache.size(); }

    /** get keys for all cache items */
    public Set<String> getKeys(String containsStr) {
        return localCache.keySet().stream().filter(x -> x.contains(containsStr)).collect(Collectors.toSet());
    }
    /** get info values */
    public List<CacheObjectInfo> getInfos(String containsStr) {
        return localCache.values()
                .stream()
                .map(CacheObject::getInfo)
                .collect(Collectors.toList());
    }
    /** get values of cache objects that contains given String in key */
    public List<CacheObject> getValues(String containsStr) {

        return localCache.values()
                .stream()
                .filter(o -> o.getKey().contains(containsStr))
                .collect(Collectors.toList());
    }
    public void onTimeClean(long checkSeq) {
        log.trace("CLEARING objects in cache HashMap, check: " + checkSeq + ", size: " + localCache.size() + ", max:" + maxObjects);
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
                if (e.getValue().isOld()) {

                    //e.getValue().releaseObject();
                }
                // TODO: clear
                //e.getKey();
                //e.getValue().releaseObject();
            }
        }
    }
    /** remove all objects by keys */
    public void removeObjectsByKeys(Collection<String> keys) {
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
    public int clearCacheForGroup(String groupName) {
        // TODO: implement clearing caches with given mode
        return 0;
    }
    /** clear cache by given mode
     * returns estimated of elements cleared */
    public int clearCache(CacheClearMode clearMode) {
        return -1;
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
