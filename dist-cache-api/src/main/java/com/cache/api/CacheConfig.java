package com.cache.api;

import java.util.HashMap;
import java.util.Properties;
import java.util.function.Function;

/** factory to create configuration for cache  */
public class CacheConfig {

    /** parent factory to create cache from given configuration */
    private Function<CacheConfig, Cache> parentFactory;
    /** GUID for configuration */
    private final String configGuid = CacheUtils.generateConfigGuid();
    /** all properties to be used for DistCache initialization */
    private Properties props = null;
    /** callbacks */
    private final HashMap<String, Function<CacheEvent, String>> callbacks = new HashMap<>();

    public CacheConfig(Properties p) {
        this.props = p;
    }

    /** get current properties */
    public Properties getProperties() {
        return props;
    }
    public String getConfigGuid() {
        return configGuid;
    }

    /** get callbacks assigned to this configuration */
    public HashMap<String, Function<CacheEvent, String>> getCallbacks() { return callbacks; }
    public String getProperty(String name) {
        return props.getProperty(name);
    }
    public String getProperty(String name, String defaultValue) {
        String value = getProperty(name);
        if (value != null) {
            return value;
        } else {
            return defaultValue;
        }
    }
    public long getPropertyAsLong(String name, long defaultValue) {
        return CacheUtils.parseLong(getProperty(name), defaultValue);
    }
    /** build empty CacheConfig with no values */
    public static CacheConfig buildEmptyConfig() {

        return new CacheConfig(new Properties());
    }
    /** build default configuration with default name, HashMap as storage */
    public static CacheConfig buildDefaultConfig() {
        return CacheConfig
                .buildEmptyConfig()
                .withNameDefault()
                .withStorageHashMap()
                .withMaxIssues(CACHE_ISSUES_MAX_COUNT_VALUE)
                .withMaxEvents(CACHE_EVENTS_MAX_COUNT_VALUE)
                .withMaxObjectAndItems(CACHE_MAX_LOCAL_OBJECTS_VALUE, CACHE_MAX_LOCAL_ITEMS_VALUE);
    }
    /** add friendly name for this cache - it would be any name that would be visible in logs, via REST endpoints
     * name should be unique, but it is not a must */
    public CacheConfig withName(String name) {
        props.setProperty(CACHE_NAME, name);
        return this;
    }
    public CacheConfig withNameDefault() {
        return withName(CACHE_NAME_VALUE_DEFAULT);
    }
    /** add comma-separated cache agent list */
    public CacheConfig withServers(String servs) {
        props.setProperty(CACHE_SERVERS, servs);
        return this;
    }
    /** define port on which agent will be listening */
    public CacheConfig withPort(int port) {
        props.setProperty(CACHE_PORT, ""+port);
        return this;
    }
    /** define port to define value on which agent will be listening */
    public CacheConfig withDefaultPort() {
        return withPort(CACHE_PORT_VALUE_DEFAULT);
    }
    /** add storage with HashMap */
    public CacheConfig withStorageHashMap() {
        String existingProps = ""+props.getProperty(CACHE_STORAGES);
        props.setProperty(CACHE_STORAGES, existingProps + "," + CACHE_STORAGE_VALUE_HASHMAP);
        return this;
    }
    public CacheConfig withStoragePriorityQueue() {
        String existingProps = ""+props.getProperty(CACHE_STORAGES);
        props.setProperty(CACHE_STORAGES, existingProps + "," + CACHE_STORAGE_VALUE_PRIORITYQUEUE);
        return this;
    }
    public CacheConfig withStorageWeakHashMap() {
        String existingProps = ""+props.getProperty(CACHE_STORAGES);
        props.setProperty(CACHE_STORAGES, existingProps + "," + CACHE_STORAGE_VALUE_WEAKHASHMAP);
        return this;
    }
    public CacheConfig withStorageElasticsearch(String url, String user, String pass) {
        String existingProps = ""+props.getProperty(CACHE_STORAGES);
        props.setProperty(CACHE_STORAGES, existingProps + "," + CACHE_STORAGE_VALUE_ELASTICSEARCH);
        props.setProperty(ELASTICSEARCH_URL, url);
        props.setProperty(ELASTICSEARCH_USER, user);
        props.setProperty(ELASTICSEARCH_PASS, pass);
        return this;
    }
    public CacheConfig withStorageRedis(String url, String port) {
        String existingProps = ""+props.getProperty(CACHE_STORAGES);
        props.setProperty(CACHE_STORAGES, existingProps + "," + CACHE_STORAGE_VALUE_REDIS);
        props.setProperty(REDIS_URL, url);
        props.setProperty(REDIS_PORT, port);
        return this;
    }
    public CacheConfig withStorageKafka(String brokers) {
        String existingProps = ""+props.getProperty(CACHE_STORAGES);
        props.setProperty(CACHE_STORAGES, existingProps + "," + CACHE_STORAGE_VALUE_KAFKA);
        props.setProperty(KAFKA_BROKERS, brokers);
        return this;
    }
    /** add URL for Dist Cache standalone application */
    public CacheConfig withCacheApp(String cacheAppUrl) {
        props.setProperty(CACHE_APPLICATION_URL, cacheAppUrl);
        return this;
    }
    public CacheConfig addProperty(String name, String value) {
        props.setProperty(name, value);
        return this;
    }
    public CacheConfig withObjectTimeToLive(long timeToLiveMs) {
        props.setProperty(CACHE_TTL, ""+timeToLiveMs);
        return this;
    }

    public CacheConfig withMaxObjectAndItems(int maxObjects, int maxItems) {
        props.setProperty(CACHE_MAX_LOCAL_OBJECTS, ""+maxObjects);
        props.setProperty(CACHE_MAX_LOCAL_ITEMS, ""+maxItems);
        return this;
    }
    public CacheConfig withMaxIssues(long maxIssues) {
        props.setProperty(CACHE_ISSUES_MAX_COUNT, ""+maxIssues);
        return this;
    }
    /** set maximum number of events kept in cache queue */
    public CacheConfig withMaxEvents(long maxEvents) {
        props.setProperty(CACHE_EVENTS_MAX_COUNT, ""+maxEvents);
        return this;
    }
    /** add callback */
    public CacheConfig withCallback(String eventType, Function<CacheEvent, String> callback) {
        callbacks.put(eventType, callback);
        return this;
    }

    /** define internal timer delay and period time in milliseconds */
    public CacheConfig withTimer(long delayMs, long periodMs) {
        props.setProperty(CACHE_TIMER_DELAY, ""+delayMs);
        props.setProperty(CACHE_TIMER_PERIOD, ""+periodMs);
        return this;
    }
    /** name of group - all caches connecting together should be having the same group
     * name of group could be like GlobalAppCache */
    public static String CACHE_GROUP = "CACHE_GROUP";
    /** name of cache - local instance
     * name should be unique */
    public static String CACHE_NAME = "CACHE_NAME";
    public static String CACHE_NAME_VALUE_DEFAULT = "Cache";
    /** port of cache for extending and distributed join */
    public static String CACHE_PORT = "CACHE_PORT";
    /** */
    public static int CACHE_PORT_VALUE_DEFAULT = 9999;

    public static String CACHE_TIMER_DELAY = "CACHE_TIMER_DELAY";
    public static long CACHE_TIMER_DELAY_VALUE = 1000;

    public static String CACHE_TIMER_PERIOD = "CACHE_TIMER_PERIOD";
    public static long CACHE_TIMER_PERIOD_VALUE = 1000;

    /** default value of time-to-live objects in cache*/
    public static String CACHE_TTL = "CACHE_TTL";
    public static long CACHE_TTL_VALUE = CacheMode.TIME_ONE_HOUR;

    /** list of cache agent servers - this should be semicolon separated list of initial cache agents like:
     * server001d:9999;server015d:9999;server018d:9999
     * There might be more agents registering and un-registering to the cache agent list.
     * The list of agents can be synchronized through different repositories like JDBC DB or Kafka or Elasticsearch
     * */
    public static String CACHE_SERVERS = "CACHE_SERVERS";
    /** URL of cache standalone application to synchronize all distributed cache managers
     * Cache Standalone App is registering and unregistering all cache agents with managers
     * that are working in cluster */
    public static String CACHE_APPLICATION_URL = "CACHE_APPLICATION_URL";
    /** maximum number of local objects */
    public static String CACHE_MAX_LOCAL_OBJECTS = "CACHE_MAX_LOCAL_OBJECTS";
    public static int CACHE_MAX_LOCAL_OBJECTS_VALUE = 1000;


    /** maximum number of issues stored in cache */
    public static String CACHE_ISSUES_MAX_COUNT = "CACHE_ISSUES_MAX_COUNT";
    public static long CACHE_ISSUES_MAX_COUNT_VALUE = 1000;

    /** maximum number of issues stored in cache */
    public static String CACHE_EVENTS_MAX_COUNT = "CACHE_EVENTS_MAX_COUNT";
    public static long CACHE_EVENTS_MAX_COUNT_VALUE = 100;

    /** IDs of static callbacks */
    public static String CACHE_CALLBACKS = "CACHE_CALLBACKS";

    /** maximum number of local items - each object could be a list with many objects
     * this could be taken from collection size = number of items */
    public static String CACHE_MAX_LOCAL_ITEMS = "CACHE_MAX_LOCAL_ITEMS";
    public static int CACHE_MAX_LOCAL_ITEMS_VALUE = 100000;

    /** default number of milliseconds as time to live for given cache object */
    public static String CACHE_DEFAULT_TTL_TIME = "CACHE_DEFAULT_TTL_TIME";
    /** list of semicolon separated storages initialized for cache
     * Elasticsearch;HashMap;Redis
     *  */
    public static String CACHE_STORAGES = "CACHE_STORAGES";
    /** names of storages and class names for these storages in package com.cache.storage */
    public static String CACHE_STORAGE_VALUE_HASHMAP = "InternalHashMapCacheStorage";
    public static String CACHE_STORAGE_VALUE_WEAKHASHMAP = "InternalWeakHashMapCacheStorage";
    public static String CACHE_STORAGE_VALUE_PRIORITYQUEUE = "InternalWeakHashMapCacheStorage";
    public static String CACHE_STORAGE_VALUE_REDIS = "RedisCacheStorage";
    public static String CACHE_STORAGE_VALUE_KAFKA = "KafkaStorage";
    public static String CACHE_STORAGE_VALUE_LOCAL_DISK = "LocalDiskStorage";
    public static String CACHE_STORAGE_VALUE_ELASTICSEARCH = "ElasticsearchCacheStorage";

    /** elasticsearch URL */
    public static String ELASTICSEARCH_URL = "ELASTICSEARCH_URL";
    public static String ELASTICSEARCH_USER = "ELASTICSEARCH_USER";
    /** elasticsearch password */
    public static String ELASTICSEARCH_PASS = "ELASTICSEARCH_PASS";
    /** Kafka brokers */
    public static String KAFKA_BROKERS = "KAFKA_BROKERS";
    /** URL for redis */
    public static String REDIS_URL = "REDIS_URL";
    public static String REDIS_PORT = "REDIS_PORT";
}
