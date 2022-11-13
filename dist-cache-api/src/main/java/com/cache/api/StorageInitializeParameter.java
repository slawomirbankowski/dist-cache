package com.cache.api;

import java.util.Properties;

/** initialization parameter(s) for cache Storage */
public class StorageInitializeParameter {
    /** parent cache object */
    public Cache cache;
    /** properties */
    public CacheConfig cacheCfg;

    public StorageInitializeParameter(CacheConfig cfg, Cache cache) {
        this.cacheCfg = cfg;
        this.cache = cache;
    }

    // TODO: fill initialize parameters to initialize Kafka, Elasticsearch, Redis, ...

}
