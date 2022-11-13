package com.cache.encoders;

import com.cache.api.CacheKeyEncoder;

/** encoding everything after 'secret:' key */
public class KeyEncoderStarting implements CacheKeyEncoder {
    @Override
    public String encodeKey(String key) {
        if (key.indexOf("secret:") >= 0) {
            return key;
        } else {
            return key;
        }
    }
}
