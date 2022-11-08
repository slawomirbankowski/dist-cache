package com.cache.storage;

import com.cache.api.CacheObject;
import com.cache.api.CacheableMethod;
import com.cache.api.StorageInitializeParameter;
import com.cache.base.CacheStorageBase;

/** cache with Elasticsearch index - need to connect to Elasticsearch,
 * create index and read/write items from/to cache
 * */
public class ElasticsearchCacheStorage extends CacheStorageBase {

    /** TODO: init Elasticsearch storage */
    public ElasticsearchCacheStorage(StorageInitializeParameter p) {

    }
    /** TODO: get item from Elasticsearch */
    public CacheObject getItem(String key) {
        return new CacheObject();
    }
    public void setItem(CacheObject o) {
        return ;
    }

}
