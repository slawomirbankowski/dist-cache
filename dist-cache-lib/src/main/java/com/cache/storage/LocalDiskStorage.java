package com.cache.storage;

import com.cache.api.CacheObject;
import com.cache.api.StorageInitializeParameter;
import com.cache.base.CacheStorageBase;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/** cache with Kafka topic - key would be key for object
 * */
public class LocalDiskStorage extends CacheStorageBase {

    /** TODO: init local disk storage */
    public LocalDiskStorage(StorageInitializeParameter p) {
        super(p);

    }
    /** Local Disk is external storage */
    public  boolean isInternal() { return false; }
    /** check if object has given key, optional with specific type */
    public boolean contains(String key) {
        return false;
    }
    /** TODO: get item from local disk */
    public Optional<CacheObject> getObject(String key) {
        return Optional.empty();
    }
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
    public  int getItemsCount() {
        return 0;
    }
    /** get number of objects in this cache */
    public int getObjectsCount() { return 0; }
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
    public void onTimeClean(long checkSeq) {

    }
}
