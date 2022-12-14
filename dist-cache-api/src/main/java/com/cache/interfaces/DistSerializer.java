package com.cache.interfaces;

/** interface for serializers to serialize data for external cache storages */
public interface DistSerializer {
    /** serialize Object to byte[] */
    byte[] serialize(Object obj);
    /** deserialize byte[] to Object */
    Object deserialize(String objectClassName, byte[] b);

    /** serialize Object to String */
    String serializeToString(Object obj);
    /** deserialize Object from String */
    Object deserializeFromString(String objectClassName, String str);
}
