package com.cache.api;

import java.util.Properties;

/** factory to create configuration for cache  */
public class CacheConfig {

    /** */
    private Properties props = null;
    public CacheConfig(Properties p) {
        this.props = p;
    }
    /** */
    public Properties getProperties() {
        return props;
    }
    /** build empty */
    public static CacheConfig buildEmptyConfig() {
        return new CacheConfig(new Properties());
    }

    /** TODO: create factory of common properties like name, servers,
     * max objects, max items, ttl time */
    public CacheConfig withProperty() {
        return this;
    }

    public CacheConfig withName(String name) {
        Properties p = new Properties(props);
        p.setProperty(CACHE_NAME, name);
        return this;
    }
    public CacheConfig withServers(String servs) {
        Properties p = new Properties(props);
        p.setProperty(CACHE_SERVERS, servs);
        return this;
    }
    public CacheConfig withObjectAndItems(int maxObjects, int maxItems) {
        Properties p = new Properties(props);
        p.setProperty(CACHE_MAX_LOCAL_OBJECTS, ""+maxObjects);
        p.setProperty(CACHE_MAX_LOCAL_ITEMS, ""+maxItems);
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
    /** list of cache agent servers - this should be semicolon separated list of initial cache agents like:
     * server001d:9999;server015d:9999;server018d:9999
     * there might be more agents registering and un-registering to the cache agent list
     * list of agents can be synchronized through different repositories like JDBC DB or Kafka or Elasticsearch
     * */
    public static String CACHE_SERVERS = "CACHE_SERVERS";
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
    /** elasticsearch URL */
    public static String ELASTICSEARCH_URL = "ELASTICSEARCH_URL";
    public static String ELASTICSEARCH_USER = "ELASTICSEARCH_USER";
    /** elasticsearch password */
    public static String ELASTICSEARCH_PASS = "ELASTICSEARCH_PASS";
    /** Kafka brokers */
    public static String KAFKA_BROKERS = "KAFKA_BROKERS";
    /** URL for redis */
    public static String REDIS_URL = "REDIS_URL";

}
