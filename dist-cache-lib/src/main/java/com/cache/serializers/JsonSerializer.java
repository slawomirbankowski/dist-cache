package com.cache.serializers;

import com.cache.interfaces.CacheSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

/** serialize using JSON */
public class JsonSerializer implements CacheSerializer {

    @Override
    public byte[] serialize(Object obj) {
        try {
            return serializeToString(obj).getBytes();
        } catch (Exception ex) {
            return null;
        }
    }
    @Override
    public Object deserialize(byte[] b) {
        try {
            return deserializeFromString(new String(b));
        } catch (Exception ex) {
            return null;
        }
    }
    @Override
    public String serializeToString(Object obj) {
        try {
            //Map<String, String>()
            ObjectMapper mapper = JsonMapper.builder()
                    .findAndAddModules()
                    .build();
            return mapper.writeValueAsString(obj);
        } catch (Exception ex) {
            return null;
        }
    }
    @Override
    public Object deserializeFromString(String str) {
        try {
            // TODO: read from JSON to Object
            ObjectMapper mapper = JsonMapper.builder()
                    .findAndAddModules()
                    .build();
            return ""; //mapper.writeValueAsString(obj);
        } catch (Exception ex) {
            return null;
        }
    }
}
