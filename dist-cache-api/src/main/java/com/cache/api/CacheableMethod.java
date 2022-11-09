package com.cache.api;

/** method to get cache object, wrapper for cache manager */
public interface CacheableMethod<T> {
    /** get object from any method */
    T get(String key);
}
