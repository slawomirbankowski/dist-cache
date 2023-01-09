package com.cache.utils.resolvers;

import com.cache.interfaces.Resolver;
import java.util.Map;
import java.util.Optional;

/** Key-Value resolver with defined map */
public class MapResolver implements Resolver {
    Map<String, String> map;
    public MapResolver(Map<String, String> map) {
        this.map = map;
    }
    /** get single value for a key */
    public Optional<String> getValue(String key) {
        String value = map.get(key);
        if (value == null) {
            return Optional.empty();
        }
        return Optional.of(value);
    }
}
