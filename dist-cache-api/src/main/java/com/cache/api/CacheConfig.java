package com.cache.api;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

/** factory to create configuration for cache  */
public class CacheConfig {

    /** build empty CacheConfig with no values */
    public static CacheConfig buildEmptyConfig() {
        return new CacheConfig(new Properties());
    }
    public static CacheConfig buildConfig(Properties initialProperties) {
        return new CacheConfig(initialProperties);
    }
    /** GUID for configuration */
    private final String configGuid = CacheUtils.generateConfigGuid();
    /** all properties to be used for DistCache initialization */
    private Properties props = null;

    public CacheConfig(Properties p) {
        this.props = p;
    }

    /** get current properties */
    public Map getProperties() {
        return Collections.unmodifiableMap(props);
    }
    /** get HashMap with properties for cache */
    public HashMap<String, String> getHashMap() {
        HashMap<String, String> hm = new HashMap<>();
        for (Map.Entry<Object, Object> e: props.entrySet()) {
            hm.put(e.getKey().toString(), e.getValue().toString());
        }
        return hm;
    }
    /** get unique ID of this config for cache */
    public String getConfigGuid() {
        return configGuid;
    }
    /** get cache property for given name */
    public String getProperty(String name) {
        return props.getProperty(name);
    }
    public boolean hasProperty(String name) {
        return props.containsKey(name);
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
    public int getPropertyAsInt(String name, int defaultValue) {
        return CacheUtils.parseInt(getProperty(name), defaultValue);
    }
    public double getPropertyAsDouble(String name, double defaultValue) {
        return CacheUtils.parseDouble(getProperty(name), defaultValue);
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

    /** delay of timer run to clear storages - value in milliseconds */
    public static String CACHE_TIMER_DELAY = "CACHE_TIMER_DELAY";
    public static long CACHE_TIMER_DELAY_VALUE = 1000;
    public static String CACHE_TIMER_COMMUNICATE_DELAY = "CACHE_TIMER_COMMUNICATE_DELAY";
    public static long CACHE_TIMER_COMMUNICATE_DELAY_VALUE = 60000;
    public static String CACHE_TIMER_RATIO_DELAY = "CACHE_TIMER_COMMUNICATE_DELAY";
    public static long CACHE_TIMER_RATIO_DELAY_VALUE = 60000;
    /** period of timer to clear storages - value in milliseconds */
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

    /** maximum number of local items - each object could be a list with many objects
     * this could be taken from collection size = number of items */
    public static String CACHE_MAX_LOCAL_ITEMS = "CACHE_MAX_LOCAL_ITEMS";
    public static int CACHE_MAX_LOCAL_ITEMS_VALUE = 100000;

    public static String CACHE_KEY_ENCODER = "CACHE_KEY_ENCODER";
    public static String CACHE_KEY_ENCODER_VALUE_NONE = "com.cache.encoders.KeyEncoderNone";
    public static String CACHE_KEY_ENCODER_VALUE_SECRET = "com.cache.encoders.KeyEncoderStarting";
    public static String CACHE_KEY_ENCODER_VALUE_FULL = "com.cache.encoders.KeyEncoderFull";

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
    public static String CACHE_STORAGE_VALUE_JDBC = "JdbcStorage";

    /** JDBC connection */
    public static String JDBC_URL = "JDBC_URL";
    public static String JDBC_DRIVER = "JDBC_DRIVER";
    public static String JDBC_USER = "JDBC_USER";
    public static String JDBC_PASS = "JDBC_PASS";

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

    public static String LOCAL_DISK_PREFIX_PATH = "LOCAL_DISK_PREFIX_PATH";
}
