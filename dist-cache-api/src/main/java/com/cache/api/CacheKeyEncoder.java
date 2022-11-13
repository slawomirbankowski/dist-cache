package com.cache.api;

public interface CacheKeyEncoder {
    /** encode key to not show passwords and secrets */
    String encodeKey(String key);
}
