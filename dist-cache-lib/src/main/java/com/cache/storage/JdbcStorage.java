package com.cache.storage;

import com.cache.api.CacheObject;
import com.cache.api.StorageInitializeParameter;
import com.cache.base.CacheStorageBase;

import java.util.Optional;

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
    public Optional<CacheObject> getItem(String key) {
        return Optional.empty();
    }
    public  Optional<CacheObject> setItem(CacheObject o) {
        return Optional.empty();
    }
    /** get number of items in cache */
    public int getItemsCount() {
        return 0;
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
