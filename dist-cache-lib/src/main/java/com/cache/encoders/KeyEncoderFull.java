package com.cache.encoders;

import com.cache.interfaces.CacheKeyEncoder;

import java.util.Base64;

/** encoding whole key */
public class KeyEncoderFull implements CacheKeyEncoder {
    @Override
    public String encodeKey(String key) {
        return new String(Base64.getEncoder().encode(key.getBytes()));
    }
}
