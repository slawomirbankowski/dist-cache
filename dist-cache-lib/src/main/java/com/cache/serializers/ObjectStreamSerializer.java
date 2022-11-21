package com.cache.serializers;

import com.cache.api.CacheSerializer;

import java.io.*;
import java.util.Base64;

/** */
public class ObjectStreamSerializer implements CacheSerializer {

    @Override
    public byte[] serialize(Object obj) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(2000);
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            oos.close();
            return baos.toByteArray();
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public Object deserialize(byte[] b) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(b);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Object obj = ois.readObject();
            ois.close();
            return obj;
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public String serializeToString(Object obj) {
        return new String(Base64.getEncoder().encode(serialize(obj)));
    }
    @Override
    public Object deserializeFromString(String str) {
        return deserialize(Base64.getDecoder().decode(str));
    }
}
