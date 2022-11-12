package com.cache.api;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
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
    /** get all cache keys that contains given string */
    Set<String> getCacheKeys(String containsStr);
    /** get number of items in cache */
    int getItemsCount();
    /** get number of objects in all storages
     * if one object is inserted into cache - this is still one object even if this is a list of 1000 elements */
    int getObjectsCount();
    Map<String, Integer> getItemsCountPerStorage();

    /** clear caches with given clear cache */
    int clearCaches(int clearMode);
    /** clear cache contains given partial key */
    int clearCacheContains(String str);
    <T> Optional<T> getObject(String key);

    /** get all recent issues with cache */
    Queue<CacheIssue> getIssues();
    /** get all recent events added to cache */
    Queue<CacheEvent> getEvents();
    /** set new callback method for events for given type */
    public void setCallback(String eventType, Function<CacheEvent, String> callback);


    <T> T withCache(String key, Supplier<? extends T> supplier, CacheMode mode);
    <T> T withCache(String key, Function<String, ? extends T> mapper, CacheMode mode);
    <T> T withCache(String key, Method method, Object obj, CacheMode mode);


    <T> T withCache(String key, Supplier<? extends T> supplier);
    <T> T withCache(String key, Function<String, ? extends T> mapper);
    <T> T withCache(String key, Method method, Object obj);


    /** close and deinitialize cache - remove all items, disconnect from all storages, stop all timers*/
    void close();
}
