package com.cache.serializers;

import com.cache.interfaces.CacheSerializer;

/** serializer and deserializer using reflection - get all fields and get/set values as simple Strings */
public class ReflectionSimpleSerializer implements CacheSerializer {

    @Override
    public byte[] serialize(Object obj) {
        try {

            return null;
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public Object deserialize(byte[] b) {


        return null;
    }

    @Override
    public String serializeToString(Object obj) {
        return null;
    }

    @Override
    public Object deserializeFromString(String str) {
        return null;
    }

}
