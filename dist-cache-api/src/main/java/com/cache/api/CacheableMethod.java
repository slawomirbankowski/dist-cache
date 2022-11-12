package com.cache.api;

import java.lang.reflect.InvocationTargetException;

/** method to get cache object, wrapper for cache manager */
public interface CacheableMethod<T> {
    /** get object from any method */
    T get(String key);
}
