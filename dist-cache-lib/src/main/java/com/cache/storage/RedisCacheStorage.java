package com.cache.storage;

import com.cache.api.CacheObject;
import com.cache.api.StorageInitializeParameter;
import com.cache.base.CacheStorageBase;

import java.util.Optional;

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
    public Optional<CacheObject> getItem(String key) {
        return Optional.empty();
    }
    public  Optional<CacheObject> setItem(CacheObject o) {
        return Optional.empty();
    }

}
