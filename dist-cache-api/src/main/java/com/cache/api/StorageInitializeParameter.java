package com.cache.api;

import com.cache.interfaces.Cache;

/** initialization parameter(s) for cache Storage */
public class StorageInitializeParameter {
    /** parent cache object */
    public Cache cache;

    public StorageInitializeParameter(Cache cache) {
        this.cache = cache;
    }

    // TODO: fill initialize parameters to initialize Kafka, Elasticsearch, Redis, ...

}
