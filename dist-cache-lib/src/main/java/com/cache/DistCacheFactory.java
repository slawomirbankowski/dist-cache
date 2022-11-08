package com.cache;

import com.cache.api.CacheConfig;
import com.cache.managers.CacheManager;

import java.util.Properties;

/**
 * local cache object contains storages that keeps object for fast read
 * and connects to other distibuted cache through agent system
 *
 * */
public class DistCacheFactory {

    /** factory is just creating managers */
    private DistCacheFactory(Properties p) {
    }

    // TODO: change getting instance to define full configuration for cache
    public static CacheManager getInstance() {
        Properties p = new Properties();
        // TODO: create default cache parameters
        p.setProperty(CacheConfig.CACHE_NAME, "");
        p.setProperty("CACHE_NAME", "");
        return new CacheManager(p);
    }
    public static CacheManager getInstance(CacheConfig cfg) {
        // TODO: CACHE_NAME should be added ???
        // TODO: should we create new cache or use existing one like Singleton???
        return new CacheManager(cfg.getProperties());
    }

    public static void main(String[] args) {
        System.out.println("START");
        CacheConfig cfg= CacheConfig.buildEmptyConfig()
                .withName("GlobalCacheTest")
                .withPort(9999)
                .withServers("localhost:9095")
                .withMaxObjectAndItems(100, 20000);

        CacheManager cache = DistCacheFactory.getInstance(cfg);
        //cache.withCache("", );

        //cache.withCache()
        System.out.println("STOP");
    }

}
