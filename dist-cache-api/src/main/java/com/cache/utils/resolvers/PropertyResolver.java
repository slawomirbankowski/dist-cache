package com.cache.utils.resolvers;

import com.cache.interfaces.Resolver;

import java.util.Optional;
import java.util.Properties;

public class PropertyResolver implements Resolver {
    Properties pr;
    public PropertyResolver(Properties pr) {
        this.pr = pr;
    }
    /** get single value for a key */
    public Optional<String> getValue(String key) {
        String value = pr.getProperty(key);
        if (value == null) {
            return Optional.empty();
        }
        return Optional.of(value);
    }
}
