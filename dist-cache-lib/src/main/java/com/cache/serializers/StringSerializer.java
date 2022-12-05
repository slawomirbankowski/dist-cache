package com.cache.serializers;

import com.cache.interfaces.CacheSerializer;

/** serializer and deserializer using String - assuming everything is String */
public class StringSerializer implements CacheSerializer {

    @Override
    public byte[] serialize(Object obj) {
        try {
            return obj.toString().getBytes();
        } catch (Exception ex) {
            return null;
        }
    }
    @Override
    public Object deserialize(byte[] b) {
        return new String(b);
    }
    @Override
    public String serializeToString(Object obj) {
        return obj.toString();
    }

    @Override
    public Object deserializeFromString(String str) {
        return str;
    }

}
