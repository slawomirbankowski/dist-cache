package com.cache.api;

import java.util.Properties;

/** initialization parameter(s) for cache Storage */
public class StorageInitializeParameter {
    public Cache cache;
    public Properties p;

    public StorageInitializeParameter(Properties p, Cache cache) {
        this.p = p;
        this.cache = cache;
    }

    // TODO: fill initialize parameters to initialize Kafka, Elasticsearch, Redis, ...
}
