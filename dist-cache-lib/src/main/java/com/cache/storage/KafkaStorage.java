package com.cache.storage;

import com.cache.api.CacheObject;
import com.cache.api.StorageInitializeParameter;
import com.cache.base.CacheStorageBase;

import java.util.Optional;

/** cache with Kafka topic - key would be key for object
 * */
public class KafkaStorage extends CacheStorageBase {

    /** TODO: init Kafka storage */
    public KafkaStorage(StorageInitializeParameter p) {

    }
    /** Kafka is external storage */
    public  boolean isInternal() { return false; }
    /** TODO: get item from Kafka */
    public Optional<CacheObject> getItem(String key) {
        return Optional.empty();
    }
    public Optional<CacheObject> setItem(CacheObject o) {
        return Optional.empty();
    }

}
