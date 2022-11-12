package com.cache;

import com.cache.api.Cache;
import com.cache.api.CacheConfig;
import com.cache.managers.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.LinkedList;
import java.util.Properties;

/**
 * local cache object contains storages that keeps object for fast read
 * and connects to other distibuted cache through agent system
 *
 * */
public class DistCacheFactory {

    /** local logger */
    private static final Logger log = LoggerFactory.getLogger(DistCacheFactory.class);

    /** all created caches so far, this is just to iterate through objects and close them if needed */
    private static final LinkedList<Cache> createdCaches = new LinkedList<>();
    /** factory is just creating managers */
    private DistCacheFactory(Properties p) {
    }

    /** get existing instance OR create one if it is not existing */
    public static synchronized Cache getInstance() {
        Cache existingCache = createdCaches.getLast();
        if (existingCache != null) {
            return existingCache;
        } else {
            return createInstance();
        }
    }
    /** create new instance of cache with default settings */
    public static synchronized Cache createInstance() {
        CacheConfig defaultCfg = CacheConfig.buildDefaultConfig();
        return createInstance(defaultCfg);
    }
    /** create new instance of cache with given configuration */
    public static synchronized Cache createInstance(CacheConfig cfg) {
        Cache cache = new CacheManager(cfg);
        createdCaches.add(cache);
        return cache;
    }
    /** create new instance of cache with properties */
    public static Cache createInstance(Properties cacheProps) {
        return createInstance(new CacheConfig(cacheProps));
    }

    /** create new empty config for distributed cache*/
    public static CacheConfig createEmptyConfig() {
        return CacheConfig.buildEmptyConfig();
    }

}
