package com.cache.storage;

import com.cache.api.CacheObject;
import com.cache.api.CacheObjectInfo;
import com.cache.api.StorageInitializeParameter;
import com.cache.base.CacheStorageBase;

import java.util.*;

/** cache with JDBC connection to any compliant database
 * it would create special table with cache items and index to fast access
 * */
public class JdbcStorage extends CacheStorageBase {

    /** TODO: init JDBC storage */
    public JdbcStorage(StorageInitializeParameter p) {
        super(p);
    }

    /** Kafka is external storage */
    public  boolean isInternal() { return false; }
    /** check if object has given key, optional with specific type */
    public boolean contains(String key) {
        return false;
    }
    /** TODO: get item from JDBC */
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
    public List<CacheObjectInfo> getValues(String containsStr) {
        return new LinkedList<CacheObjectInfo>();
    }
    /** clear caches with given clear cache */
    public int clearCache(int clearMode) {
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
