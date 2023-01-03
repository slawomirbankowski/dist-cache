package com.cache.api;

import com.cache.utils.DistUtils;
import com.cache.utils.JsonUtils;
import com.cache.utils.StringValueResolver;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
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
    private final String configGuid = DistUtils.generateConfigGuid();
    /** all properties to be used for DistCache initialization */
    private Properties props = null;
    /** resolver for String values in properties */
    private StringValueResolver resolver;

    public DistConfig(Properties p) {
        this.props = p;
        this.resolver = new StringValueResolver();
        props.setProperty("CONFIG_GUID", configGuid);
    }
    public DistConfig(Properties p, StringValueResolver resolver) {
        this.props = p;
        this.resolver = resolver;
        props.setProperty("CONFIG_GUID", configGuid);
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
        return resolver.resolve(props.getProperty(name));
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
        return DistUtils.parseLong(getProperty(name), defaultValue);
    }
    public int getPropertyAsInt(String name, int defaultValue) {
        return DistUtils.parseInt(getProperty(name), defaultValue);
    }
    public double getPropertyAsDouble(String name, double defaultValue) {
        return DistUtils.parseDouble(getProperty(name), defaultValue);
    }
    /** save to File as JSON format */
    public boolean saveToJsonFile(String fileName) {
        try {
            Files.writeString(Path.of(""), toJsonString(), StandardOpenOption.CREATE);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
    /** save to file as Properties format */
    public boolean saveToPropertiesFile(String fileName) {
        try {
            props.store(new FileWriter(fileName), "");
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
    /** save properties from this configuration to JSON as String */
    public String toJsonString() {
        return JsonUtils.serialize(props);
    }
    /** save properties from this configuration to Properties as String */
    public String toPropertiesString() {
        try {
            ByteArrayOutputStream outStr = new ByteArrayOutputStream();
            props.store(outStr, "");
            return new String(outStr.toByteArray());
        } catch (IOException ex) {
            return null;
        }
    }
    /** name of group - all caches connecting together should be having the same group
     * name of group could be like GlobalAppCache */
    public static String DIST_GROUP = "DIST_GROUP";

    /** name of Distributed system - local instance
     * name should be unique */
    public static String DIST_NAME = "DIST_NAME";
    public static String DIST_NAME_VALUE_DEFAULT = "DistSystem";


    /** port of cache for extending and distributed join */
    public static String AGENT_API_PORT = "AGENT_API_PORT";


    /** JDBC connection for agent registration */
    public static String AGENT_REGISTRATION_JDBC_URL = "AGENT_REGISTRATION_JDBC_URL";
    public static String AGENT_REGISTRATION_JDBC_DRIVER = "AGENT_REGISTRATION_JDBC_DRIVER";
    public static String AGENT_REGISTRATION_JDBC_USER = "AGENT_REGISTRATION_JDBC_USER";
    public static String AGENT_REGISTRATION_JDBC_PASS = "AGENT_REGISTRATION_JDBC_PASS";
    public static String AGENT_REGISTRATION_JDBC_DIALECT = "AGENT_REGISTRATION_JDBC_DIALECT";
    public static String AGENT_REGISTRATION_JDBC_INIT_CONNECTIONS = "AGENT_REGISTRATION_JDBC_INIT_CONNECTIONS";
    public static String AGENT_REGISTRATION_JDBC_MAX_ACTIVE_CONNECTIONS = "AGENT_REGISTRATION_JDBC_MAX_ACTIVE_CONNECTIONS";

    /** elasticsearch registration parameters */
    public static String AGENT_REGISTRATION_ELASTICSEARCH_URL = "AGENT_REGISTRATION_ELASTICSEARCH_URL";
    public static String AGENT_REGISTRATION_ELASTICSEARCH_USER = "AGENT_REGISTRATION_ELASTICSEARCH_USER";
    public static String AGENT_REGISTRATION_ELASTICSEARCH_PASS = "AGENT_REGISTRATION_ELASTICSEARCH_PASS";

    /** */
    public static String REDIS_PORT = "REDIS_PORT";


    /** port of cache for extending and distributed join */
    public static String AGENT_SERVER_SOCKET_PORT = "AGENT_SERVER_SOCKET_PORT";
    /** */
    public static int AGENT_SERVER_SOCKET_PORT_DEFAULT_VALUE = 9901;
    /** sequencer for default agent port */
    public static final AtomicInteger AGENT_SOCKET_PORT_VALUE_SEQ = new AtomicInteger(9901);

    /** */
    public static String AGENT_SERVER_HTTP_PORT = "AGENT_SERVER_HTTP_PORT";
    public static int AGENT_SERVER_HTTP_PORT_DEFAULT_VALUE = 9901;

    public static String AGENT_SERVER_DATAGRAM_PORT = "AGENT_SERVER_DATAGRAM_PORT";
    public static int AGENT_SERVER_DATAGRAM_PORT_DEFAULT_VALUE = 9933;
    public static String AGENT_SERVER_DATAGRAM_TIMEOUT = "AGENT_SERVER_DATAGRAM_TIMEOUT";

    /** Server for agent communication based on Kafka */
    public static String AGENT_SERVER_KAFKA_BROKERS = "AGENT_SERVER_KAFKA_BROKERS";
    public static String AGENT_SERVER_KAFKA_TOPIC = "AGENT_SERVER_KAFKA_TOPIC";

    /** */
    public static final String AGENT_INACTIVATE_AFTER = "AGENT_INACTIVATE_AFTER";
    public static final long AGENT_INACTIVATE_AFTER_DEFAULT_VALUE = CacheMode.TIME_TEN_MINUTES;
    public static final String AGENT_DELETE_AFTER = "AGENT_DELETE_AFTER";
    public static final long AGENT_DELETE_AFTER_DEFAULT_VALUE = CacheMode.TIME_ONE_DAY;

    /** period of timer to clear storages - value in milliseconds */
    public static String TIMER_CLEAN_STORAGE_PERIOD = "TIMER_CLEAN_STORAGE_PERIOD";
    public static long TIMER_CLEAN_STORAGE_PERIOD_DELAY_VALUE = CacheMode.TIME_ONE_MINUTE;

    /** timer to refresh statistics */
    public static String TIMER_STAT_REFRESH_PERIOD = "TIMER_STAT_REFRESH_PERIOD";
    public static long TIMER_STAT_REFRESH_PERIOD_DELAY_VALUE = CacheMode.TIME_ONE_MINUTE;

    /** timer to check registration objects like agents, servers, new configurations */
    public static String TIMER_REGISTRATION_PERIOD = "TIMER_REGISTRATION_PERIOD";
    public static long TIMER_REGISTRATION_PERIOD_DELAY_VALUE = CacheMode.TIME_ONE_MINUTE;

    public static String TIMER_SERVER_CLIENT_PERIOD = "TIMER_REGISTRATION_PERIOD";
    public static long TIMER_SERVER_CLIENT_PERIOD_DELAY_VALUE = CacheMode.TIME_ONE_MINUTE;

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

    public static String CACHE_POLICY = "CACHE_POLICY";

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
    public static String CACHE_STORAGE_VALUE_MONGO = "MongodbStorage";
    public static String CACHE_STORAGE_VALUE_CASSANDRA = "CassandraStorage";
    public static String CACHE_STORAGE_VALUE_KAFKA = "KafkaStorage";
    public static String CACHE_STORAGE_VALUE_LOCAL_DISK = "LocalDiskStorage";
    public static String CACHE_STORAGE_VALUE_ELASTICSEARCH = "ElasticsearchCacheStorage";
    public static String CACHE_STORAGE_VALUE_JDBC = "JdbcStorage";

    /** CACHE STORAGE - settings for JDBC storage */
    public static String CACHE_STORAGE_JDBC_URL = "CACHE_STORAGE_JDBC_URL";
    public static String CACHE_STORAGE_JDBC_DRIVER = "CACHE_STORAGE_JDBC_DRIVER";
    public static String CACHE_STORAGE_JDBC_USER = "CACHE_STORAGE_JDBC_USER";
    public static String CACHE_STORAGE_JDBC_PASS = "CACHE_STORAGE_JDBC_PASS";
    public static String CACHE_STORAGE_JDBC_DIALECT = "CACHE_STORAGE_JDBC_DIALECT";
    public static String CACHE_STORAGE_JDBC_INIT_CONNECTIONS = "CACHE_STORAGE_JDBC_INIT_CONNECTIONS";
    public static String CACHE_STORAGE_JDBC_MAX_ACTIVE_CONNECTIONS = "CACHE_STORAGE_JDBC_MAX_ACTIVE_CONNECTIONS";

    /** CACHE STORAGE - settings for MongoDB storage */
    public static String CACHE_STORAGE_MONGODB_HOST = "CACHE_STORAGE_MONGODB_HOST";
    public static String CACHE_STORAGE_MONGODB_PORT = "CACHE_STORAGE_MONGODB_PORT";
    public static String CACHE_STORAGE_MONGODB_DATABASE = "CACHE_STORAGE_MONGODB_DATABASE";
    public static String CACHE_STORAGE_MONGODB_COLLECTION = "CACHE_STORAGE_MONGODB_COLLECTION";

    /** CACHE STORAGE - settings for Redis storage */
    public static String CACHE_STORAGE_REDIS_HOST = "CACHE_STORAGE_REDIS_HOST";
    public static String CACHE_STORAGE_REDIS_PORT = "CACHE_STORAGE_REDIS_PORT";
    public static String CACHE_STORAGE_REDIS_URL = "REDIS_URL";

    /** CACHE STORAGE - settings for Elasticsearch storage */
    public static String CACHE_STORAGE_ELASTICSEARCH_URL = "CACHE_STORAGE_ELASTICSEARCH_URL";
    public static String CACHE_STORAGE_ELASTICSEARCH_USER = "CACHE_STORAGE_ELASTICSEARCH_USER";
    public static String CACHE_STORAGE_ELASTICSEARCH_PASS = "CACHE_STORAGE_ELASTICSEARCH_PASS";

    /** CACHE STORAGE - settings for Cassandra storage */
    public static String CACHE_STORAGE_CASSANDRA_HOST = "CACHE_STORAGE_CASSANDRA_HOST";
    public static String CACHE_STORAGE_CASSANDRA_PORT = "CACHE_STORAGE_CASSANDRA_PORT";

    /** CACHE STORAGE - Kafka brokers */
    public static String CACHE_STORAGE_KAFKA_BROKERS = "CACHE_STORAGE_KAFKA_BROKERS";
    public static String CACHE_STORAGE_KAFKA_TOPIC = "CACHE_STORAGE_KAFKA_TOPIC";
    public static String CACHE_STORAGE_KAFKA_BROKERS_DEFAULT_VALUE = "dist-cache-items";

    /** CACHE STORAGE - LocalDisk prefix for storage folder/ directory */
    public static String CACHE_STORAGE_LOCAL_DISK_PREFIX_PATH = "CACHE_STORAGE_LOCAL_DISK_PREFIX_PATH";

}
