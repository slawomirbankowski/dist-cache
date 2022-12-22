package com.cache.storage;

import com.cache.api.*;
import com.cache.base.CacheStorageBase;

import java.util.*;

/** cache with Elasticsearch index - need to connect to Elasticsearch,
 * create index and read/write items from/to cache
 *
 * TODO: Implement storage saving cache objects in Elasticsearch
 * */
public class ElasticsearchCacheStorage extends CacheStorageBase {

    /** TODO: init Elasticsearch storage */
    public ElasticsearchCacheStorage(StorageInitializeParameter p) {
        super(p);
    }
    /** Elasticsearch is external storage */
    public  boolean isInternal() { return false; }
    /** get type of this storage */
    public CacheStorageType getStorageType() {
        return CacheStorageType.elasticsearch;
    }
    /** check if object has given key, optional with specific type */
    public boolean contains(String key) {
        return false;
    }
    /** TODO: get item from Elasticsearch */
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
    public int getItemsCount() {
        return 0;
    }
    /** get number of objects in this cache */
    public int getObjectsCount() { return 0; }

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
        // TODO: implements
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
