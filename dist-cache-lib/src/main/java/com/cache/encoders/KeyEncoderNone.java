package com.cache.encoders;

import com.cache.api.CacheKeyEncoder;

/** no encoding */
public class KeyEncoderNone implements CacheKeyEncoder {
    @Override
    public String encodeKey(String key) {
        return key;
    }
}
