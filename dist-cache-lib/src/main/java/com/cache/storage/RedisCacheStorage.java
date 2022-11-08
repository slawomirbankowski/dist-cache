package com.cache.storage;

import com.cache.api.CacheObject;
import com.cache.api.StorageInitializeParameter;
import com.cache.base.CacheStorageBase;

/** cache with Elasticsearch index - need to connect to Elasticsearch,
 * create index and read/write items from/to cache
 * */
public class RedisCacheStorage extends CacheStorageBase {

    /** initialize Redis storage */
    public RedisCacheStorage(StorageInitializeParameter p) {
    }
    /** Redis is external storage */
    public  boolean isInternal() { return false; }
    /** TODO: implement redis */
    public CacheObject getItem(String key) {
        return null;
    }
    public void setItem(CacheObject o) {
        return ;
    }

}
