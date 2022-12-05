package com.cache.interfaces;

import com.cache.api.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/** base interface for cache that is allowing to operate on cache objects */
public interface Cache extends DistService {

    /** get unique ID of this cache */
    String getCacheGuid();
    /** get date and time of creation for this cache */
    LocalDateTime getCreatedDateTime();
    /** get agent for communication with other services in distributed environment */
    Agent getAgent();
    /** get configuration for cache */
    DistConfig getConfig();
    /** get default serialized for this cache */
    CacheSerializer getCacheSerializer();
    /** check if cache has been already closed and deinitialized */
    boolean getClosed();
    /** get information about all storages in this cache */
    List<StorageInfo> getStoragesInfo();
    /** get information about all storages in this cache */
    int getStoragesCount();

    /** check if there is object for given key in cache - this is checking in all storages available */
    boolean contains(String key);
    /** get all keys for storages, each storage has unique key to identify  */
    Set<String> getStorageKeys();
    /** get all cache keys that contains given string
     * cache keys are searched in all storages
     * this might return only TOP X keys if there are by far too many keys in cache to be downloaded */
    Set<String> getCacheKeys(String containsStr);
    /** get values stored in cache
     * this might returns only first X values */
    List<CacheObjectInfo> getCacheValues(String containsStr);
    /** get number of items in cache,
     * each object might be having many items because it might be a List or Set or Map or Array
     * number of items must be greater or equal than number of objects */
    int getItemsCount();
    /** get number of objects in all storages
     * if one object is inserted into cache - this is still one object even if this is a list of 1000 elements */
    int getObjectsCount();
    /** get number of items in each storage */
    Map<String, Integer> getItemsCountPerStorage();
    /** get number of objects in each storage */
    Map<String, Integer> getObjectsCountPerStorage();
    /** clear caches with given clear cache */
    int clearCaches(int clearMode);
    /** clear cache contains given partial key */
    int clearCacheContains(String str);
    /** get first object in cache for given key
     * if many storages has the same object - only first one is retrieved */
    <T> Optional<T> getObject(String key);
    /** get item from cache if exists or None */
    Optional<CacheObject> getCacheObject(String key);
    /** get item from cache as String if exists or None */
    String getCacheObjectAsString(String key);
    /** get item from cache if exists or None */
    CacheSetBack setCacheObject(String key, Object value, CacheMode mode, Set<String> groups);
    CacheSetBack setCacheObject(String key, Object value, CacheMode mode);
    CacheSetBack setCacheObject(String key, Object value);
    /** get all recent issues with cache,
     * issues might be caused by internal Exception, connection problems, incorrect usage
     * only last X issues are stored in cache */
    Queue<CacheIssue> getIssues();
    /** add issue with method and exception */
    void addIssue(String methodName, Exception ex);
    /** get all recent events added to cache
     * only last X events are stored in cache */
    Queue<CacheEvent> getEvents();

    /** get info about cache */
    CacheInfo getCacheInfo();
    /** set new callback method for events for given type */
    void setCallback(String eventType, Function<CacheEvent, String> callback);

    <T> T withCache(String key, Supplier<? extends T> supplier, CacheMode mode, Set<String> groups);
    <T> T withCache(String key, Supplier<? extends T> supplier, Set<String> groups);
    <T> T withCache(String key, Supplier<? extends T> supplier, CacheMode mode);
    <T> T withCache(String key, Supplier<? extends T> supplier);

    <T> T withCache(String key, Function<String, ? extends T> mapper, CacheMode mode, Set<String> groups);
    <T> T withCache(String key, Function<String, ? extends T> mapper, Set<String> groups);
    <T> T withCache(String key, Function<String, ? extends T> mapper, CacheMode mode);
    <T> T withCache(String key, Function<String, ? extends T> mapper);

    /** close and deinitialize cache - remove all items, disconnect from all storages, stop all timers*/
    void close();
}