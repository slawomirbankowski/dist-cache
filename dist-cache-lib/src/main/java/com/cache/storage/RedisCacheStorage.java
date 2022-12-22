package com.cache.storage;

import com.cache.api.*;
import com.cache.base.CacheStorageBase;
//import redis.clients.jedis.Jedis;

import java.util.*;

/** cache with Redis as Cache Storage
 *
 * TODO: Implement storage saving cache objects in Redis
 *
 * */
public class RedisCacheStorage extends CacheStorageBase {

    /** initialize Redis storage */
    public RedisCacheStorage(StorageInitializeParameter p) {

        super(p);
        /*
        String host = "localhost";
        int port = 9949;
        Jedis jedis = new Jedis(host, port);
        log.info("Redis client ID: " + jedis.clientId());
        log.info("Redis client info: " + jedis.clientInfo());
       // jedis.
*/
    }
    /** Redis is external storage */
    public  boolean isInternal() { return false; }
    /** get type of this storage */
    public CacheStorageType getStorageType() {
        return CacheStorageType.redis;
    }
    /** check if object has given key, optional with specific type */
    public boolean contains(String key) {
        return false;
    }
    /** TODO: implement redis */
    public Optional<CacheObject> getObject(String key) {
        return Optional.empty();
    }
    public  Optional<CacheObject> setObject(CacheObject o) {
        return Optional.empty();
    }
    /** remove objects in cache storage by keys */
    public void removeObjectsByKeys(List<String> keys) {
    }
    /** remove object in cache storage by key */
    public void removeObjectByKey(String key) {
    }
    /** get number of items in cache */
    public  int getItemsCount() {
        return 0;
    }
    /** get number of objects in this cache */
    public int getObjectsCount() { return 0; }
    /** get keys for all cache items */
    public Set<String> getKeys(String containsStr) {
        return new HashSet<String>();
    }
    /** get info values */
    public List<CacheObjectInfo> getInfos(String containsStr) {
        return new LinkedList<CacheObjectInfo>();
    }
    /** get values of cache objects that contains given String in key */
    public List<CacheObject> getValues(String containsStr) {
        return new LinkedList<CacheObject>();
    }
    /** clear caches with given clear cache */
    public int clearCacheForGroup(String groupName) {
        return 1;
    }
    /** clear cache contains given partial key */
    public int clearCacheContains(String str) {
        return 1;
    }
    /** clear cache by given mode
     * returns estimated of elements cleared */
    public int clearCache(CacheClearMode clearMode) {
        return -1;
    }
    /** check cache every X seconds to clear TTL caches */
    public void onTimeClean(long checkSeq) {

    }
}
