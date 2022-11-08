package com.cache.api;

/** method to get cache object, wrapper for cache manager */
public interface CacheableMethod {
    /** get object from any method */
    public Object get(String key);
}
