package com.cache.interfaces;

import java.util.Optional;

/** interface for resolver of values or keys */
public interface Resolver {
    /** get single value for a key */
    Optional<String> getValue(String key);
}
