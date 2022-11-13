package com.cache.encoders;

import com.cache.api.CacheKeyEncoder;

/** encoding everything after 'secret:' key */
public class KeyEncoderStarting implements CacheKeyEncoder {
    @Override
    public String encodeKey(String key) {
        int secretPos = key.indexOf("secret:");
        if (secretPos >= 0) {
            return key.substring(0, secretPos) + key.substring(secretPos);
        } else {
            return key;
        }
    }
}
