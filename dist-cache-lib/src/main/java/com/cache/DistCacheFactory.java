package com.cache;

import com.cache.api.Cache;
import com.cache.api.CacheConfig;
import com.cache.api.CacheEvent;
import com.cache.managers.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

/**
 * factory class to create cache with desired configuration
 *
 * local cache object contains storages that keeps object for fast read
 * and connects to other distibuted cache through agent system
 *
 * */
public class DistCacheFactory {

    /** local logger */
    private static final Logger log = LoggerFactory.getLogger(DistCacheFactory.class);
    /** all created caches so far, this is just to iterate through objects and close them if needed */
    private static final LinkedList<Cache> createdCaches = new LinkedList<>();

    /** get existing instance OR create one if it is not existing */
    public static synchronized Cache getDefaultInstance() {
        Cache existingCache = createdCaches.getLast();
        if (existingCache != null) {
            return existingCache;
        } else {
            return createDefaultInstance();
        }
    }
    /** create new instance of cache with default settings */
    public static synchronized Cache createDefaultInstance() {
        return DistCacheFactory.buildDefaultFactory().createInstance();
    }
    /** create new instance of cache with given configuration */
    public static synchronized Cache createInstance(CacheConfig cfg, HashMap<String, Function<CacheEvent, String>> callbacks) {
        return DistCacheFactory.buildConfigFactory(cfg)
                .withCallbacks(callbacks)
                .createInstance();
    }
    public static Cache createInstance(CacheConfig cacheCfg) {
        return DistCacheFactory
                .buildConfigFactory(cacheCfg)
                .createInstance();
    }
    /** create new instance of cache with properties */
    public static Cache createInstance(Properties cacheProps) {
        return DistCacheFactory.buildPropertiesFactory(cacheProps)
                .createInstance();
    }

    /** create new empty config for distributed cache*/
    public static CacheConfig buildEmptyConfig() {
        return CacheConfig.buildEmptyConfig();
    }

    /** build empty factory to fill with properties, callbacks with methods like:
     *  withProperty, withName, withPort, withStorageHashMap, withStorageElasticsearch, ...
     * cache instance could be created from factory */
    public static DistCacheFactory buildEmptyFactory() {
        return new DistCacheFactory();
    }

    /** build default configuration with default name, HashMap as storage */
    public static DistCacheFactory buildDefaultFactory() {
        return DistCacheFactory
                .buildEmptyFactory()
                .withNameDefault()
                .withStorageHashMap()
                .withMaxIssues(CacheConfig.CACHE_ISSUES_MAX_COUNT_VALUE)
                .withMaxEvents(CacheConfig.CACHE_EVENTS_MAX_COUNT_VALUE)
                .withMaxObjectAndItems(CacheConfig.CACHE_MAX_LOCAL_OBJECTS_VALUE, CacheConfig.CACHE_MAX_LOCAL_ITEMS_VALUE);
    }

    /** build factory based on properties */
    public static DistCacheFactory buildPropertiesFactory(Map initialFactoryProperties) {
        return DistCacheFactory
                .buildEmptyFactory()
                .withProperties(initialFactoryProperties);
    }
    /** */
    public static DistCacheFactory buildConfigFactory(CacheConfig cacheCfg) {
        return buildPropertiesFactory(cacheCfg.getProperties());
    }

    /** cache properties for factory */
    private Properties props = new Properties();
    /** callbacks for events - methods (values) to call when there is event of given type (keys) */
    private final HashMap<String, Function<CacheEvent, String>> callbacks = new HashMap<>();

    /** factory is just creating managers */
    private DistCacheFactory() {
    }
    /** extract configuration from current factory */
    public CacheConfig extractCacheConfig() {
        return CacheConfig.buildConfig(props);
    }

    /** create instance of cache from current factory using properties and callbacks */
    public Cache createInstance() {
        Cache cache = new CacheManager(new CacheConfig(props), callbacks);
        createdCaches.add(cache);
        return cache;
    }

    /** add friendly name for this cache - it would be any name that would be visible in logs, via REST endpoints
     * name should be unique, but it is not a must */
    public DistCacheFactory withName(String name) {
        props.setProperty(CacheConfig.CACHE_NAME, name);
        return this;
    }
    public DistCacheFactory withNameDefault() {
        return withName(CacheConfig.CACHE_NAME_VALUE_DEFAULT);
    }
    /** add comma-separated cache agent list */
    public DistCacheFactory withServers(String servs) {
        props.setProperty(CacheConfig.CACHE_SERVERS, servs);
        return this;
    }
    /** define port on which agent will be listening */
    public DistCacheFactory withPort(int port) {
        props.setProperty(CacheConfig.CACHE_PORT, ""+port);
        return this;
    }
    /** define port to define value on which agent will be listening */
    public DistCacheFactory withDefaultPort() {
        return withPort(CacheConfig.CACHE_PORT_VALUE_DEFAULT);
    }
    /** add storage with HashMap */
    public DistCacheFactory withStorageHashMap() {
        String existingProps = ""+props.getProperty(CacheConfig.CACHE_STORAGES);
        props.setProperty(CacheConfig.CACHE_STORAGES, existingProps + "," + CacheConfig.CACHE_STORAGE_VALUE_HASHMAP);
        return this;
    }
    public DistCacheFactory withStoragePriorityQueue() {
        String existingProps = ""+props.getProperty(CacheConfig.CACHE_STORAGES);
        props.setProperty(CacheConfig.CACHE_STORAGES, existingProps + "," + CacheConfig.CACHE_STORAGE_VALUE_PRIORITYQUEUE);
        return this;
    }
    public DistCacheFactory withStorageWeakHashMap() {
        String existingProps = ""+props.getProperty(CacheConfig.CACHE_STORAGES);
        props.setProperty(CacheConfig.CACHE_STORAGES, existingProps + "," + CacheConfig.CACHE_STORAGE_VALUE_WEAKHASHMAP);
        return this;
    }
    public DistCacheFactory withStorageElasticsearch(String url, String user, String pass) {
        String existingProps = ""+props.getProperty(CacheConfig.CACHE_STORAGES);
        props.setProperty(CacheConfig.CACHE_STORAGES, existingProps + "," + CacheConfig.CACHE_STORAGE_VALUE_ELASTICSEARCH);
        props.setProperty(CacheConfig.ELASTICSEARCH_URL, url);
        props.setProperty(CacheConfig.ELASTICSEARCH_USER, user);
        props.setProperty(CacheConfig.ELASTICSEARCH_PASS, pass);
        return this;
    }
    public DistCacheFactory withStorageRedis(String url, String port) {
        String existingProps = ""+props.getProperty(CacheConfig.CACHE_STORAGES);
        props.setProperty(CacheConfig.CACHE_STORAGES, existingProps + "," + CacheConfig.CACHE_STORAGE_VALUE_REDIS);
        props.setProperty(CacheConfig.REDIS_URL, url);
        props.setProperty(CacheConfig.REDIS_PORT, port);
        return this;
    }
    public DistCacheFactory withStorageKafka(String brokers) {
        String existingProps = ""+props.getProperty(CacheConfig.CACHE_STORAGES);
        props.setProperty(CacheConfig.CACHE_STORAGES, existingProps + "," + CacheConfig.CACHE_STORAGE_VALUE_KAFKA);
        props.setProperty(CacheConfig.KAFKA_BROKERS, brokers);
        return this;
    }
    /** add URL for Dist Cache standalone application */
    public DistCacheFactory withCacheApp(String cacheAppUrl) {
        props.setProperty(CacheConfig.CACHE_APPLICATION_URL, cacheAppUrl);
        return this;
    }
    public DistCacheFactory withProperty(String name, String value) {
        props.setProperty(name, value);
        return this;
    }
    public DistCacheFactory withProperties(Map initialFactoryProperties) {
        initialFactoryProperties.entrySet().stream().forEach(pr -> {

        });
        return this;
    }
    public DistCacheFactory withObjectTimeToLive(long timeToLiveMs) {
        props.setProperty(CacheConfig.CACHE_TTL, ""+timeToLiveMs);
        return this;
    }
    public DistCacheFactory withMaxObjectAndItems(int maxObjects, int maxItems) {
        props.setProperty(CacheConfig.CACHE_MAX_LOCAL_OBJECTS, ""+maxObjects);
        props.setProperty(CacheConfig.CACHE_MAX_LOCAL_ITEMS, ""+maxItems);
        return this;
    }
    public DistCacheFactory withMaxIssues(long maxIssues) {
        props.setProperty(CacheConfig.CACHE_ISSUES_MAX_COUNT, ""+maxIssues);
        return this;
    }
    /** set maximum number of events kept in cache queue */
    public DistCacheFactory withMaxEvents(long maxEvents) {
        props.setProperty(CacheConfig.CACHE_EVENTS_MAX_COUNT, ""+maxEvents);
        return this;
    }
    /** add callback */
    public DistCacheFactory withCallback(String eventType, Function<CacheEvent, String> callback) {
        callbacks.put(eventType, callback);
        return this;
    }
    public DistCacheFactory withCallbacks(Map<String, Function<CacheEvent, String>> callbackMethods) {
        callbackMethods.entrySet().stream().forEach(cb -> {
            callbacks.put(cb.getKey(), cb.getValue());
        });
        return this;
    }

    /** define internal timer delay and period time in milliseconds */
    public DistCacheFactory withTimer(long delayMs, long periodMs) {
        props.setProperty(CacheConfig.CACHE_TIMER_DELAY, ""+delayMs);
        props.setProperty(CacheConfig.CACHE_TIMER_PERIOD, ""+periodMs);
        return this;
    }
}
