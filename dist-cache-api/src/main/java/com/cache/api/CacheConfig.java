package com.cache.api;

import java.util.Properties;

/** factory to create configuration for cache  */
public class CacheConfig {

    /** all properties to be used for DistCache initialization */
    private Properties props = null;
    public CacheConfig(Properties p) {
        this.props = p;
    }
    /** get current properties */
    public Properties getProperties() {
        return props;
    }

    /** build empty CacheConfig with no values */
    public static CacheConfig buildEmptyConfig() {
        return new CacheConfig(new Properties());
    }

    public CacheConfig withName(String name) {
        props.setProperty(CACHE_NAME, name);
        return this;
    }
    public CacheConfig withServers(String servs) {
        props.setProperty(CACHE_SERVERS, servs);
        return this;
    }
    public CacheConfig withPort(int port) {
        props.setProperty(CACHE_PORT, ""+port);
        return this;
    }
    public CacheConfig withDefaultPort() {
        return withPort(CACHE_PORT_VALUE_DEFAULT);
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
    public CacheConfig withMaxObjectAndItems(int maxObjects, int maxItems) {
        props.setProperty(CACHE_MAX_LOCAL_OBJECTS, ""+maxObjects);
        props.setProperty(CACHE_MAX_LOCAL_ITEMS, ""+maxItems);
        return this;
    }


    /** name of group - all caches connecting together should be having the same group
     * name of group could be like GlobalAppCache */
    public static String CACHE_GROUP = "CACHE_GROUP";
    /** name of cache - local instance
     * name should be unique */
    public static String CACHE_NAME = "CACHE_NAME";
    /** port of cache for extending and distributed join */
    public static String CACHE_PORT = "CACHE_PORT";
    /** */
    public static int CACHE_PORT_VALUE_DEFAULT = 9999;

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
    /** maximum number of local items - each object could be a list with many objects
     * this could be taken from collection size = number of items */
    public static String CACHE_MAX_LOCAL_ITEMS = "CACHE_MAX_LOCAL_ITEMS";
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
