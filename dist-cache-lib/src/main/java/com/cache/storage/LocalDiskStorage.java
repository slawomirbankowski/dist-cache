package com.cache.storage;

import com.cache.api.CacheObject;
import com.cache.api.StorageInitializeParameter;
import com.cache.base.CacheStorageBase;

import java.util.Optional;

/** cache with Kafka topic - key would be key for object
 * */
public class LocalDiskStorage extends CacheStorageBase {

    /** TODO: init local disk storage */
    public LocalDiskStorage(StorageInitializeParameter p) {

    }
    /** Local Disk is external storage */
    public  boolean isInternal() { return false; }
    /** TODO: get item from local disk */
    public Optional<CacheObject> getItem(String key) {
        return Optional.empty();
    }
    public Optional<CacheObject> setItem(CacheObject o) {
        return Optional.empty();
    }

}
