package com.cache.storage;

import com.cache.api.CacheObject;
import com.cache.api.StorageInitializeParameter;
import com.cache.base.CacheStorageBase;

/** cache with Kafka topic - key would be key for object
 * */
public class KafkaStorage extends CacheStorageBase {

    /** TODO: init Kafka storage */
    public KafkaStorage(StorageInitializeParameter p) {

    }
    /** TODO: get item from Kafka */
    public CacheObject getItem(String key) {
        return new CacheObject();
    }
    public void setItem(CacheObject o) {
        return ;
    }

}