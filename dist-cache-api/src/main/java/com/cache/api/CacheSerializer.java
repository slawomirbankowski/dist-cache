package com.cache.api;

/** interface for cache serializers to serialize data for external cache storages */
public interface CacheSerializer {
    /** serialize Object to byte[] */
    byte[] serialize(Object obj);
    /** deserialize byte[] to Object */
    Object deserialize(byte[] b);
    /** serialize Object to String */
    String serializeToString(Object obj);
    /** deserialize Object from String */
    Object deserializeFromString(String str);
}
