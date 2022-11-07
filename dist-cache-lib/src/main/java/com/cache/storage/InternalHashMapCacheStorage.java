package com.cache.storage;

import com.cache.base.CacheStorageBase;

import java.util.HashMap;

public class InternalHashMapCacheStorage implements CacheStorageBase {

    /** */
    private java.util.HashMap<String, Object> localCache = new HashMap<>();
    /** */
    public Object getItem(String key) {
        return new Object();
    }
    /** */
    public Object setItem(Object o) {
        return "";
    }

}
