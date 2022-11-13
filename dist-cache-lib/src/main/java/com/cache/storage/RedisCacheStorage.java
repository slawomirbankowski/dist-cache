package com.cache.storage;

import com.cache.api.CacheObject;
import com.cache.api.CacheObjectInfo;
import com.cache.api.StorageInitializeParameter;
import com.cache.base.CacheStorageBase;

import java.util.*;

/** cache with Elasticsearch index - need to connect to Elasticsearch,
 * create index and read/write items from/to cache
 * */
public class RedisCacheStorage extends CacheStorageBase {

    /** initialize Redis storage */
    public RedisCacheStorage(StorageInitializeParameter p) {
        super(p);
    }
    /** Redis is external storage */
    public  boolean isInternal() { return false; }
    /** check if object has given key, optional with specific type */
    public boolean contains(String key) {
        return false;
    }
    /** TODO: implement redis */
    public Optional<CacheObject> getObject(String key) {
        return Optional.empty();
    }
    public  Optional<CacheObject> setObject(CacheObject o) {
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
    /** get info values */
    public List<CacheObjectInfo> getValues(String containsStr) {
        return new LinkedList<CacheObjectInfo>();
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
