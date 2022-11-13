package com.cache.storage;

import com.cache.api.*;
import com.cache.base.CacheStorageBase;

import java.util.*;

/** cache with local disk - this could be ephemeral
 * this kind of cache should be for larger object without need of often use
 * */
public class LocalDiskStorage extends CacheStorageBase {

    private String filePrefixName;
    /** TODO: init local disk storage */
    public LocalDiskStorage(StorageInitializeParameter p) {
        super(p);
        filePrefixName = initParams.cacheCfg.getProperty(CacheConfig.LOCAL_DISK_PREFIX_PATH, "/tmp/");
    }
    /** Local Disk is external storage */
    public  boolean isInternal() { return false; }
    /** check if object has given key, optional with specific type */
    public boolean contains(String key) {
        return false;
    }
    /** TODO: get item from local disk */
    public Optional<CacheObject> getObject(String key) {
        return Optional.empty();
    }
    public Optional<CacheObject> setObject(CacheObject o) {
        try {
            String cacheObjectFileName = filePrefixName + o.getKey() + ".cache"; // TODO: change this to have HEX encoding of key
            // create temporary file with content - object
            java.io.File f = java.io.File.createTempFile("", "");
            // TODO: get initial path to save file - should be the same path to get it
            // serialize CacheObject into file stream
        } catch (Exception ex) {
            initParams.cache.addIssue("LocalDiskStorage.setObject", ex);
        }
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
    public List<CacheObjectInfo> getValues(String containsStr) {
        return new LinkedList<CacheObjectInfo>();
    }
    /** clear caches with given clear cache */
    public int clearCaches(int clearMode) {

        return 1;
    }
    /** clear cache contains given partial key */
    public int clearCacheContains(String str) {
        return 1;
    }
    /** check cache every X seconds to clear TTL caches */
    public void onTimeClean(long checkSeq) {

    }
}
