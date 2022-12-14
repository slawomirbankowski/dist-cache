package com.cache.api;

import com.cache.utils.CacheUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

/** configuration for distributed system - this is keeping parameters in Properties format */
public class DistConfig {

    /** build empty DistConfig with no values */
    public static DistConfig buildEmptyConfig() {
        return new DistConfig(new Properties());
    }
    public static DistConfig buildConfig(Properties initialProperties) {
        return new DistConfig(initialProperties);
    }
    /** GUID for configuration */
    private final String configGuid = CacheUtils.generateConfigGuid();
    /** all properties to be used for DistCache initialization */
    private Properties props = null;

    public DistConfig(Properties p) {
        this.props = p;
        props.setProperty("", configGuid);
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

    /** configuration contains property for given name */
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
    /** save to File */
    public void saveToFile(String fileName) {
        // TODO: save Properties into file for given name
    }
    /** save to JSON */
    public String saveToJson() {
        // TODO: save Properties to JSON
        return "";
    }
    /** name of group - all caches connecting together should be having the same group
     * name of group could be like GlobalAppCache */
    public static String DIST_GROUP = "DIST_GROUP";

    /** name of Distributed system - local instance
     * name should be unique */
    public static String DIST_NAME = "DIST_NAME";
    public static String DIST_NAME_VALUE_DEFAULT = "DistSystem";

    /** port of cache for extending and distributed join */
    public static String AGENT_SOCKET_PORT = "AGENT_SOCKET_PORT";
    /** */
    public static int AGENT_SOCKET_PORT_DEFAULT_VALUE = 9901;
    /** sequencer for default agent port */
    public static final AtomicInteger AGENT_SOCKET_PORT_VALUE_SEQ = new AtomicInteger(9901);

    /** delay of timer run to clear storages - value in milliseconds */
    public static String TIMER_DELAY = "TIMER_DELAY";
    public static long TIMER_DELAY_VALUE = 1000;
    public static String TIMER_COMMUNICATE_DELAY = "TIMER_COMMUNICATE_DELAY";
    public static long TIMER_COMMUNICATE_DELAY_VALUE = 60000;
    public static String TIMER_RATIO_DELAY = "TIMER_RATIO_DELAY";
    public static long TIMER_RATIO_DELAY_VALUE = 60000;
    /** period of timer to clear storages - value in milliseconds */
    public static String TIMER_PERIOD = "TIMER_PERIOD";
    public static long TIMER_PERIOD_VALUE = 1000;

    /** */
    public static String AGENT_SERVER_SOCKET_CLIENT_TIMEOUT = "AGENT_SERVER_SOCKET_CLIENT_TIMEOUT";
    public static int AGENT_SERVER_SOCKET_CLIENT_TIMEOUT_DEFAULT_VALUE = 2000;

    /** default value of time-to-live objects in cache*/
    public static String CACHE_TTL = "CACHE_TTL";
    public static long CACHE_TTL_VALUE = CacheMode.TIME_ONE_HOUR;

    public static String SERIALIZER_DEFINITION = "SERIALIZER_DEFINITION";
    public static String SERIALIZER_DEFINITION_SERIALIZABLE_VALUE = "java.lang.String=StringSerializer,default=ObjectStreamSerializer";

    /** URL of cache standalone application to synchronize all distributed cache managers
     * Cache Standalone App is registering and unregistering all cache agents with managers
     * that are working in cluster */
    public static String CACHE_APPLICATION_URL = "CACHE_APPLICATION_URL";
    public static String CACHE_APPLICATION_URL_DEFAULT_VALUE = "http://localhost:8085/api";
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
    public static String CACHE_STORAGE_VALUE_PRIORITYQUEUE = "InternalWithTtlAndPriority";
    public static String CACHE_STORAGE_VALUE_REDIS = "RedisCacheStorage";
    public static String CACHE_STORAGE_VALUE_KAFKA = "KafkaStorage";
    public static String CACHE_STORAGE_VALUE_LOCAL_DISK = "LocalDiskStorage";
    public static String CACHE_STORAGE_VALUE_ELASTICSEARCH = "ElasticsearchCacheStorage";
    public static String CACHE_STORAGE_VALUE_JDBC = "JdbcStorage";

    /** settings for JDBC storage */
    public static String CACHE_STORAGE_JDBC_URL = "CACHE_STORAGE_JDBC_URL";
    public static String CACHE_STORAGE_JDBC_DRIVER = "CACHE_STORAGE_JDBC_DRIVER";
    public static String CACHE_STORAGE_JDBC_USER = "CACHE_STORAGE_JDBC_USER";
    public static String CACHE_STORAGE_JDBC_PASS = "CACHE_STORAGE_JDBC_PASS";
    public static String CACHE_STORAGE_JDBC_DIALECT = "CACHE_STORAGE_JDBC_DIALECT";
    public static String CACHE_STORAGE_JDBC_INIT_CONNECTIONS = "CACHE_STORAGE_JDBC_INIT_CONNECTIONS";
    public static String CACHE_STORAGE_JDBC_MAX_ACTIVE_CONNECTIONS = "CACHE_STORAGE_JDBC_MAX_ACTIVE_CONNECTIONS";

    /** JDBC connection for agent registration */
    public static String JDBC_URL = "JDBC_URL";
    public static String JDBC_DRIVER = "JDBC_DRIVER";
    public static String JDBC_USER = "JDBC_USER";
    public static String JDBC_PASS = "JDBC_PASS";
    public static String JDBC_DIALECT = "JDBC_DIALECT";
    public static String JDBC_INIT_CONNECTIONS = "JDBC_INIT_CONNECTIONS";
    public static String JDBC_MAX_ACTIVE_CONNECTIONS = "JDBC_MAX_ACTIVE_CONNECTIONS";

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
