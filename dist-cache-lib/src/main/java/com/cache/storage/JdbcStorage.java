package com.cache.storage;

import com.cache.api.CacheObject;
import com.cache.api.StorageInitializeParameter;
import com.cache.base.CacheStorageBase;

/** cache with JDBC connection to any compliant database
 * it would create special table with cache items and index to fast access
 * */
public class JdbcStorage extends CacheStorageBase {

    /** TODO: init JDBC storage */
    public JdbcStorage(StorageInitializeParameter p) {

    }

    /** Kafka is external storage */
    public  boolean isInternal() { return false; }
    /** TODO: get item from JDBC */
    public CacheObject getItem(String key) {
        return null;
    }
    public void setItem(CacheObject o) {
        return ;
    }

}