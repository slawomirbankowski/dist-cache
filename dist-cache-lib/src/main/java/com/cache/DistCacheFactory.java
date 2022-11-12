package com.cache;

import com.cache.api.CacheConfig;
import com.cache.api.CacheableMethod;
import com.cache.managers.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.SLF4JServiceProvider;

import java.util.Properties;
import java.util.ServiceLoader;

/**
 * local cache object contains storages that keeps object for fast read
 * and connects to other distibuted cache through agent system
 *
 * */
public class DistCacheFactory {
    private static final Logger log = LoggerFactory.getLogger(DistCacheFactory.class);

    /** factory is just creating managers */
    private DistCacheFactory(Properties p) {
    }

    // TODO: change getting instance to define full configuration for cache
    public static CacheManager getInstance() {
        Properties p = new Properties();
        // TODO: create default cache parameters
        p.setProperty(CacheConfig.CACHE_NAME, "");
        return new CacheManager(p);
    }
    public static CacheManager getInstance(CacheConfig cfg) {
        // TODO: CACHE_NAME should be added ???
        // TODO: should we create new cache or use existing one like Singleton???
        return new CacheManager(cfg.getProperties());
    }

}
