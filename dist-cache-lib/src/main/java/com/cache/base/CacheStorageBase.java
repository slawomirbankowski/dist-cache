package com.cache.base;

public interface CacheStorageBase {

    public Object getItem(String key);
    public Object setItem(Object o);

}
