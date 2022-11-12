package com.cache.base;

import com.cache.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/** base abstract class for storage to keep caches
 * storage could be:
 * internal (HashMap, WeakHashMap) - kept in local JVM memory
 * external (Elasticsearch, Redis) - kept somewhere outside JVM memory */
public abstract class CacheStorageBase {
    protected static final Logger log = LoggerFactory.getLogger(CacheBase.class);
    /** unique identifier of this storage */
    private final String storageUid;
    /** date and time of creation of this storage */
    private final LocalDateTime storageCreatedDate = LocalDateTime.now();
    /** initialization parameters for subclasses */
    protected StorageInitializeParameter initParams;

    /** base constructor to pass initialization parameters */
    public CacheStorageBase(StorageInitializeParameter p) {
        this.initParams = p;
        this.storageUid  = "STORAGE_" + this.getClass().getSimpleName() + "_" + UUID.randomUUID();
    }
    /** check if object has given key, optional with specific type */
    public abstract boolean contains(String key);
    /** get CacheObject item from cache by full key */
    public abstract Optional<CacheObject> getItem(String key);
    /** set item to cache and get previous item in cache for the same key */
    public abstract Optional<CacheObject> setItem(CacheObject o);
    /** get number of items in cache */
    public abstract int getItemsCount();

    /** clear caches with given clear cache */
    public abstract int clearCaches(int clearMode);
    /** clear cache contains given partial key */
    public abstract int clearCacheContains(String str);
    /** check cache every X seconds to clear TTL caches
     * onTime should be run by parent manager in cycles */
    public abstract void onTime(long checkSeq);
    /** returns true if storage is internal and cache objects are kept in local memory
     * false if storage is external and cache objects are kept in any storages like Redis, Elasticsearch, DB*/
    public abstract boolean isInternal();
    /** dispose this storage if needed */
    public void disposeStorage() {
        // by default no dispose - it could be overriden by any storage
    }
    /** get unique storage ID */
    public String getStorageUid() { return storageUid; }

}
