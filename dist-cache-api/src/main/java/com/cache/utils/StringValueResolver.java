package com.cache.utils;

import com.cache.interfaces.Resolver;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/** resolver for properties to change value */
public class StringValueResolver {

    /** list of resolvers for given value */
    private List<Resolver> resolvers = new LinkedList<>();

    public StringValueResolver() {
    }
    /** add resolver */
    public StringValueResolver addResolver(Resolver r) {
        resolvers.add(r);
        return this;
    }
    /** resolve key */
    private List<String> resolveKeyAll(String key) {
        return resolvers.stream().flatMap(res -> res.getValue(key).stream()).collect(Collectors.toList());
    }
    /** resolve key */
    private Optional<String> resolveKeyFirst(String key) {
        return resolvers.stream().flatMap(res -> res.getValue(key).stream()).findFirst();
    }
    /** resolve key */
    private String resolveKey(String key) {
        return resolveKeyFirst(key).orElseGet(() -> key);
    }

    /** resolve string with all value resolvers */
    public String resolve(String value) {
        if (value == null) {
            return null;
        }
        return resolve(value, 0);
    }
    /** resolve string with all value resolvers */
    private String resolve(String value, int depth) {
        if (depth >= 5) {
            return "";
        } else {
            StringBuilder outText = new StringBuilder();
            StringBuilder key = new StringBuilder();
            int pos = 0;
            boolean isKey = false;
            while (pos < value.length()) {
                char currentChar = value.charAt(pos);
                if (currentChar == '$') {
                } else if (pos > 0 && value.charAt(pos - 1) == '$' && value.charAt(pos) == '{') {
                    isKey = true;
                } else if (pos > 0 && value.charAt(pos) == '}') {
                    isKey = false;
                    String keyText = key.toString();
                    String valueRaw = resolveKey(keyText);
                    String valueResolved = resolve(valueRaw, depth + 1);
                    outText.append(valueResolved);
                    key = new StringBuilder();
                } else if (isKey) {
                    key.append(currentChar);
                } else {
                    outText.append(value.charAt(pos));
                }
                pos = pos + 1;
            }
            return outText.toString();
        }
    }
}
