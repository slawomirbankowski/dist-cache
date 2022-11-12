package com.cache.api;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/** base interface for manager */
public interface Cache {

    String getCacheManagerGuid();
    LocalDateTime getCreatedDateTime();
    /** check if cache has been already closed and deinitialized */
    boolean getClosed();
    boolean contains(String key);
    /** get all keys for storages*/
    Set<String> getStorageKeys();
    /** get number of items in cache */
    int getItemsCount();
    Map<String, Integer> getItemsCountPerStorage();

    /** clear caches with given clear cache */
    int clearCaches(int clearMode);
    /** clear cache contains given partial key */
    int clearCacheContains(String str);
    <T> Optional<T> getItem(String key);

    <T> T withCache(String key, CacheableMethod<T> m, CacheMode mode);
    <T> T withCache(String key, Supplier<? extends T> supplier, CacheMode mode);
    <T> T withCache(String key, Function<String, ? extends T> mapper, CacheMode mode);
    <T> T withCache(String key, Method method, Object obj, CacheMode mode);

    /** close and deinitialize cache - remove all items, disconnect from all storages, stop all timers*/
    void close();
}
