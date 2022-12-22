package com.cache.base;

import com.cache.api.*;
import com.cache.interfaces.CacheKeyEncoder;
import com.cache.interfaces.CacheStorage;
import com.cache.interfaces.DistSerializer;
import com.cache.utils.CacheUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/** base abstract class for storage to keep caches
 * storage could be:
 * internal (HashMap, WeakHashMap) - kept in local JVM memory
 * external (Elasticsearch, Redis) - kept somewhere outside JVM memory
 * */
public abstract class CacheStorageBase implements CacheStorage {
    protected static final Logger log = LoggerFactory.getLogger(CacheStorageBase.class);
    /** unique identifier of this storage */
    private final String storageUid;
    /** date and time of creation of this storage */
    private final LocalDateTime storageCreatedDate = LocalDateTime.now();
    /** maximum number of items in cache,
     * this is not ceil that is strict but above that more clearing will be done */
    protected final long maxItems;
    /** maximum number of objects in cache,
     * this is not ceil that is strict but above that more clearing will be done */
    protected final long maxObjects;
    /** serializer dedicated for this cache storage */
    protected final DistSerializer distSerializer;
    /** initialization parameters for subclasses */
    protected StorageInitializeParameter initParams;

    /** base constructor to pass initialization parameters */
    public CacheStorageBase(StorageInitializeParameter p) {
        this.initParams = p;
        this.storageUid = CacheUtils.generateStorageGuid(getClass().getSimpleName());
        this.distSerializer = p.cache.getAgent().getSerializer();
        this.maxObjects = initParams.cache.getConfig().getPropertyAsLong(DistConfig.CACHE_MAX_LOCAL_OBJECTS, 1000);
        this.maxItems = initParams.cache.getConfig().getPropertyAsLong(DistConfig.CACHE_MAX_LOCAL_ITEMS, 1000);
    }

    /** get unique storage ID */
    public String getStorageUid() { return storageUid; }
    /** get UID of parent cache */
    public String getCacheUid() { return initParams.cache.getCacheGuid(); }
    /** get type of this storage */
    public abstract CacheStorageType getStorageType();
    /** get name of this storage - by default it is simple name of this class */
    public String getStorageName() {
        return getClass().getSimpleName();
    }
    /** get information about this storage */
    public StorageInfo getStorageInfo() {
        return new StorageInfo(storageUid, storageCreatedDate, this.getClass().getName(),
                getItemsCount(), getObjectsCount(), isInternal()); }
    /** get key encoder - this is a class to encode key to protect passwords, secrets of a key */
    public CacheKeyEncoder getKeyEncoder() {
        return initParams.cache.getKeyEncoder();
    }
    protected String encodeKeyToFileEnd(String key) {
        return "." +  CacheUtils.stringToHex(getKeyEncoder().encodeKey(key)) + ".cache";
    }
    protected String encodeKey(String key) {
        return CacheUtils.stringToHex(getKeyEncoder().encodeKey(key));
    }
    /** check if object has given key, optional with specific type */
    /** get CacheObject item from cache by full key */
    public abstract Optional<CacheObject> getObject(String key);
    /** set item to cache and get previous item in cache for the same key */
    public abstract Optional<CacheObject> setObject(CacheObject o);
    /** remove objects in cache by keys */
    public abstract void removeObjectsByKeys(List<String> keys);
    /** remove single object by key */
    public void removeObjectByKey(String key) {
        removeObjectsByKeys(List.of(key));
    }
    /** get number of objects in cache storage */
    public abstract int getObjectsCount();
    /** get number of items in cache storage */
    public abstract int getItemsCount();
    public abstract Set<String> getKeys(String containsStr);
    /** get info values */
    public abstract List<CacheObject> getValues(String containsStr);
    /** get information objects for cache values */
    public abstract List<CacheObjectInfo> getInfos(String containsStr);

    /** clear caches with given clear cache */
    public abstract int clearCache(CacheClearMode clearMode);

    /** clear cache contains given partial key */
    public abstract int clearCacheContains(String str);
    /** clear cache for given group */
    public abstract int clearCacheForGroup(String groupName);

    /** check cache every X seconds to clear TTL caches
     * onTime should be run by parent manager in cycles */
    public abstract void onTimeClean(long checkSeq);

    /** check cache every X seconds to clear TTL caches
     * onTime should be run by parent manager in cycles */
    public void timeToClean(long checkSeq, long lastCleanTime) {
        // TODO: add minimum time between clean
        if (checkSeq % timeCleanEvery() == 0) {
            onTimeClean(checkSeq);
        }
    }
    /** every this value storage would be cleared */
    protected int timeCleanEvery() {
        return 2;
    }
    /** returns true if storage is internal and cache objects are kept in local memory
     * false if storage is external and cache objects are kept in any storages like Redis, Elasticsearch, DB*/
    public abstract boolean isInternal();
    /** dispose this storage if needed */
    public void disposeStorage() {
        // by default no dispose - it could be overridden by any storage
    }

}
