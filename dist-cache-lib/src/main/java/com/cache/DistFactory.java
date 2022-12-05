package com.cache;

import com.cache.agent.AgentInstance;
import com.cache.interfaces.Agent;
import com.cache.interfaces.Cache;
import com.cache.api.DistConfig;
import com.cache.api.CacheEvent;
import com.cache.managers.CacheManager;
import com.cache.utils.CacheUtils;
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
 * and connects to other distributed cache through agent system
 *
 * */
public class DistFactory {

    /** local logger */
    private static final Logger log = LoggerFactory.getLogger(DistFactory.class);
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
        return DistFactory.buildDefaultFactory().createCacheInstance();
    }
    /** create new instance of cache with given configuration */
    public static synchronized Cache createCacheInstance(DistConfig cfg, HashMap<String, Function<CacheEvent, String>> callbacks) {
        return DistFactory.buildConfigFactory(cfg)
                .withCallbacks(callbacks)
                .createCacheInstance();
    }
    public static Cache createCacheInstance(DistConfig cacheCfg) {
        return DistFactory
                .buildConfigFactory(cacheCfg)
                .createCacheInstance();
    }
    /** create new instance of cache with properties */
    public static Cache createCacheInstance(Properties cacheProps) {
        return DistFactory.buildPropertiesFactory(cacheProps)
                .createCacheInstance();
    }

    /** create new empty config for distributed cache*/
    public static DistConfig buildEmptyConfig() {
        return DistConfig.buildEmptyConfig();
    }

    /** build empty factory to fill with properties, callbacks with methods like:
     *  withProperty, withName, withPort, withStorageHashMap, withStorageElasticsearch, ...
     * cache instance could be created from factory */
    public static DistFactory buildEmptyFactory() {
        return new DistFactory();
    }

    /** build default configuration with default name, HashMap as storage */
    public static DistFactory buildDefaultFactory() {
        return DistFactory
                .buildEmptyFactory()
                .withNameDefault()
                .withStorageHashMap()
                .withMaxIssues(DistConfig.CACHE_ISSUES_MAX_COUNT_VALUE)
                .withMaxEvents(DistConfig.CACHE_EVENTS_MAX_COUNT_VALUE)
                .withMaxObjectAndItems(DistConfig.CACHE_MAX_LOCAL_OBJECTS_VALUE, DistConfig.CACHE_MAX_LOCAL_ITEMS_VALUE);
    }

    /** build factory based on properties */
    public static DistFactory buildPropertiesFactory(Map initialFactoryProperties) {
        return DistFactory
                .buildEmptyFactory()
                .withMap(initialFactoryProperties);
    }
    /** build factory from given configuration */
    public static DistFactory buildConfigFactory(DistConfig cacheCfg) {
        return buildPropertiesFactory(cacheCfg.getProperties());
    }

    /** cache properties for factory */
    private Properties props = new Properties();
    /** callbacks for events - methods (values) to call when there is event of given type (keys) */
    private final HashMap<String, Function<CacheEvent, String>> callbacks = new HashMap<>();


    /** factory is just creating managers */
    private DistFactory() {
    }
    /** extract configuration from current factory */
    public DistConfig extractCacheConfig() {
        return DistConfig.buildConfig(props);
    }

    /** create instance of cache from current factory using properties and callbacks */
    public Cache createCacheInstance() {
        DistConfig config = new DistConfig(props);
        AgentInstance agent = new AgentInstance(config, callbacks);
        agent.initializeAgent();
        Cache cache = new CacheManager(agent, config);
        createdCaches.add(cache);
        // TODO: add measure service
        // TODO: add other services
        return cache;
    }
    /** create instance of agent from current factory using properties and callbacks */
    public Agent createAgentInstance() {
        DistConfig config = new DistConfig(props);
        AgentInstance agent = new AgentInstance(config, callbacks);
        agent.initializeAgent();
        return agent;
    }


    /** add friendly name for this cache - it would be any name that would be visible in logs, via REST endpoints
     * name should be unique, but it is not a must */
    public DistFactory withName(String name) {
        props.setProperty(DistConfig.DIST_NAME, name);
        return this;
    }
    public DistFactory withNameDefault() {
        return withName(DistConfig.DIST_NAME_VALUE_DEFAULT);
    }
    /** add comma-separated cache agent list */
    public DistFactory withServers(String servs) {
        props.setProperty(DistConfig.CACHE_SERVERS, servs);
        return this;
    }
    /** define port on which agent will be listening */
    public DistFactory withSocketPort(int port) {
        props.setProperty(DistConfig.AGENT_SOCKET_PORT, ""+port);
        return this;
    }
    /** define port to define value on which agent will be listening */
    public DistFactory withDefaultSocketPort() {
        return withSocketPort(DistConfig.AGENT_SOCKET_PORT_DEFAULT_VALUE);
    }
    /** add all ENV variables to cache configuration */
    public DistFactory withEnvironmentVariables() {
        log.debug("Adding ENV variables " + System.getenv().size() + " to CacheConfig");
        for (Map.Entry<String, String> e : System.getenv().entrySet()) {
            props.setProperty(e.getKey(), e.getValue());
        }
        return this;
    }
    public DistFactory withCommandLineArguments(String[] args) {
        // TODO: read command-line arguments into map of properties to be added
        return this;
    }
    /** add common properties for this cache/machine/agent/address/path */
    public DistFactory withCommonProperties() {
        props.setProperty("CACHE_GUID", CacheUtils.getCacheGuid());
        props.setProperty("CACHE_HOST_NAME", CacheUtils.getCurrentHostName());
        props.setProperty("CACHE_HOST_ADDRESS", CacheUtils.getCurrentHostAddress());
        props.setProperty("CACHE_LOCATION_PATH", CacheUtils.getCurrentLocationPath());
        return this;
    }
    /** add storage with HashMap */
    public DistFactory withStorageHashMap() {
        String existingProps = ""+props.getProperty(DistConfig.CACHE_STORAGES);
        props.setProperty(DistConfig.CACHE_STORAGES, existingProps + "," + DistConfig.CACHE_STORAGE_VALUE_HASHMAP);
        return this;
    }
    public DistFactory withStoragePriorityQueue() {
        String existingProps = ""+props.getProperty(DistConfig.CACHE_STORAGES);
        props.setProperty(DistConfig.CACHE_STORAGES, existingProps + "," + DistConfig.CACHE_STORAGE_VALUE_PRIORITYQUEUE);
        return this;
    }
    public DistFactory withStorageWeakHashMap() {
        String existingProps = ""+props.getProperty(DistConfig.CACHE_STORAGES);
        props.setProperty(DistConfig.CACHE_STORAGES, existingProps + "," + DistConfig.CACHE_STORAGE_VALUE_WEAKHASHMAP);
        return this;
    }
    public DistFactory withStorageElasticsearch(String url, String user, String pass) {
        String existingProps = ""+props.getProperty(DistConfig.CACHE_STORAGES);
        props.setProperty(DistConfig.CACHE_STORAGES, existingProps + "," + DistConfig.CACHE_STORAGE_VALUE_ELASTICSEARCH);
        props.setProperty(DistConfig.ELASTICSEARCH_URL, url);
        props.setProperty(DistConfig.ELASTICSEARCH_USER, user);
        props.setProperty(DistConfig.ELASTICSEARCH_PASS, pass);
        return this;
    }
    public DistFactory withStorageJdbc(String url, String driver, String user, String pass) {
        String existingProps = ""+props.getProperty(DistConfig.CACHE_STORAGES);
        props.setProperty(DistConfig.CACHE_STORAGES, existingProps + "," + DistConfig.CACHE_STORAGE_VALUE_JDBC);
        props.setProperty(DistConfig.CACHE_STORAGE_JDBC_URL, url);
        props.setProperty(DistConfig.CACHE_STORAGE_JDBC_DRIVER, driver);
        props.setProperty(DistConfig.CACHE_STORAGE_JDBC_USER, user);
        props.setProperty(DistConfig.CACHE_STORAGE_JDBC_PASS, pass);
        props.setProperty(DistConfig.CACHE_STORAGE_JDBC_DIALECT, driver);
        return this;
    }
    public DistFactory withStorageLocalDisk(String basePath) {
        String existingProps = ""+props.getProperty(DistConfig.CACHE_STORAGES);
        props.setProperty(DistConfig.CACHE_STORAGES, existingProps + "," + DistConfig.CACHE_STORAGE_VALUE_LOCAL_DISK);
        props.setProperty(DistConfig.LOCAL_DISK_PREFIX_PATH, basePath);
        return this;
    }
    public DistFactory withJdbc(String url, String driver, String user, String pass) {
        props.setProperty(DistConfig.JDBC_URL, url);
        props.setProperty(DistConfig.JDBC_DRIVER, driver);
        props.setProperty(DistConfig.JDBC_USER, user);
        props.setProperty(DistConfig.JDBC_PASS, pass);
        return this;
    }
    public DistFactory withStorageRedis(String url, String port) {
        String existingProps = ""+props.getProperty(DistConfig.CACHE_STORAGES);
        props.setProperty(DistConfig.CACHE_STORAGES, existingProps + "," + DistConfig.CACHE_STORAGE_VALUE_REDIS);
        props.setProperty(DistConfig.REDIS_URL, url);
        props.setProperty(DistConfig.REDIS_PORT, port);
        return this;
    }
    public DistFactory withStorageKafka(String brokers) {
        String existingProps = ""+props.getProperty(DistConfig.CACHE_STORAGES);
        props.setProperty(DistConfig.CACHE_STORAGES, existingProps + "," + DistConfig.CACHE_STORAGE_VALUE_KAFKA);
        props.setProperty(DistConfig.KAFKA_BROKERS, brokers);
        return this;
    }
    /** add URL for Dist Cache standalone application */
    public DistFactory withCacheApp(String cacheAppUrl) {
        props.setProperty(DistConfig.CACHE_APPLICATION_URL, cacheAppUrl);
        return this;
    }
    /** */
    public DistFactory withCacheAppDefaultUrl() {
        props.setProperty(DistConfig.CACHE_APPLICATION_URL, DistConfig.CACHE_APPLICATION_URL_DEFAULT_VALUE);
        return this;
    }
    public DistFactory withProperty(String name, String value) {
        props.setProperty(name, value);
        return this;
    }
    public DistFactory withProperties(Properties initialFactoryProperties) {
        for (Map.Entry<Object, Object> e: initialFactoryProperties.entrySet()) {
            props.setProperty(e.getKey().toString(), e.getValue().toString());
        }
        return this;
    }
    public DistFactory withMap(Map<String, String> initialFactoryProperties) {
        for (Map.Entry<String, String> e: initialFactoryProperties.entrySet()) {
            props.setProperty(e.getKey(), e.getValue());
        }
        return this;
    }
    public DistFactory withObjectTimeToLive(long timeToLiveMs) {
        props.setProperty(DistConfig.CACHE_TTL, ""+timeToLiveMs);
        return this;
    }
    public DistFactory withMaxObjectAndItems(int maxObjects, int maxItems) {
        props.setProperty(DistConfig.CACHE_MAX_LOCAL_OBJECTS, ""+maxObjects);
        props.setProperty(DistConfig.CACHE_MAX_LOCAL_ITEMS, ""+maxItems);
        return this;
    }
    public DistFactory withMaxIssues(long maxIssues) {
        props.setProperty(DistConfig.CACHE_ISSUES_MAX_COUNT, ""+maxIssues);
        return this;
    }
    /** set maximum number of events kept in cache queue */
    public DistFactory withMaxEvents(long maxEvents) {
        props.setProperty(DistConfig.CACHE_EVENTS_MAX_COUNT, ""+maxEvents);
        return this;
    }
    /** add callback */
    public DistFactory withCallback(String eventType, Function<CacheEvent, String> callback) {
        callbacks.put(eventType, callback);
        return this;
    }
    public DistFactory withCallbacks(Map<String, Function<CacheEvent, String>> callbackMethods) {
        callbackMethods.entrySet().stream().forEach(cb -> {
            callbacks.put(cb.getKey(), cb.getValue());
        });
        return this;
    }
    /** define internal timer delay and period time in milliseconds */
    public DistFactory withTimer(long delayMs, long periodMs) {
        props.setProperty(DistConfig.CACHE_TIMER_DELAY, ""+delayMs);
        props.setProperty(DistConfig.CACHE_TIMER_PERIOD, ""+periodMs);
        return this;
    }
}
