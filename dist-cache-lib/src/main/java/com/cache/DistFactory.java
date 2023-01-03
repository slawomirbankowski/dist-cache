package com.cache;

import com.cache.agent.AgentInstance;
import com.cache.api.*;
import com.cache.interfaces.Agent;
import com.cache.interfaces.Cache;
import com.cache.interfaces.DistSerializer;
import com.cache.interfaces.Resolver;
import com.cache.serializers.ComplexSerializer;
import com.cache.utils.JsonUtils;
import com.cache.utils.DistUtils;
import com.cache.utils.CustomSerializer;
import com.cache.utils.StringValueResolver;
import com.cache.utils.resolvers.MapResolver;
import com.cache.utils.resolvers.PropertyResolver;
import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * factory class to create cache with desired configuration
 * local cache object contains storages that keeps object for fast read
 * and connects to other distributed cache through agent system
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
                .withCacheStorageHashMap()
                .withMaxIssues(DistConfig.CACHE_ISSUES_MAX_COUNT_VALUE)
                .withMaxEvents(DistConfig.CACHE_EVENTS_MAX_COUNT_VALUE)
                .withCacheMaxObjectsAndItems(DistConfig.CACHE_MAX_LOCAL_OBJECTS_VALUE, DistConfig.CACHE_MAX_LOCAL_ITEMS_VALUE);
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
    /** all serializers assigned to class name
     * key = full name of class to be serialized
     * value = serializer to do the work */
    private final HashMap<String, DistSerializer> serializers = new HashMap<>();
    /** cache policy */
    private CachePolicy policy = CachePolicyBuilder.empty().create();
    /** resolver for configuration values */
    private StringValueResolver resolver = new StringValueResolver()
            .addResolver(new PropertyResolver(props))
            .addResolver(new MapResolver(System.getenv()));

    /** factory is just creating managers */
    private DistFactory() {
    }
    /** extract configuration from current factory */
    public DistConfig extractCacheConfig() {
        return DistConfig.buildConfig(props);
    }

    /** create instance of cache from current factory using properties and callbacks
     * this is creating Agent and Agent is creating Cache by get method
     * */
    public Cache createCacheInstance() {
        DistConfig config = new DistConfig(props, resolver);
        AgentInstance agent = new AgentInstance(config, callbacks, serializers, policy);
        agent.initializeAgent();
        return agent.getAgentServices().getCache();
    }
    /** create instance of agent from current factory using properties and callbacks
     * This is just creating Agent itself
     * */
    public Agent createAgentInstance() {
        DistConfig config = new DistConfig(props, resolver);
        AgentInstance agent = new AgentInstance(config, callbacks, serializers, policy);
        agent.initializeAgent();
        return agent;
    }

    /** add all ENV variables to cache configuration */
    public DistFactory withEnvironmentVariables() {
        log.debug("Adding ENV variables " + System.getenv().size() + " to CacheConfig");
        for (Map.Entry<String, String> e : System.getenv().entrySet()) {
            props.setProperty(e.getKey(), e.getValue());
        }
        return this;
    }
    /** add new command line arguments to Dist properties */
    public DistFactory withCommandLineArguments(String[] args) {
        String currentProperty = null;
        List<String> currentValues = new LinkedList<>();
        for (int i=0; i<args.length; i++) {
            String currArg = args[i];
            if (currArg.startsWith("--")) {
                if (currentProperty != null && !currentValues.isEmpty()) {
                    props.put(currentProperty, String.join(",", currentValues));
                }
                currentProperty = currArg.substring(2);
                currentValues.clear();
            } else {
                currentValues.add(currArg);
            }
        }
        return this;
    }
    /** add friendly name for this cache - it would be any name that would be visible in logs, via REST endpoints
     * name should be unique, but it is not a must */
    public DistFactory withName(String name) {
        props.setProperty(DistConfig.DIST_NAME, name);
        return this;
    }
    /** add common properties for this cache/machine/agent/address/path */
    public DistFactory withCommonProperties() {
        props.setProperty("CACHE_GUID", DistUtils.getCacheGuid());
        props.setProperty("CACHE_HOST_NAME", DistUtils.getCurrentHostName());
        props.setProperty("CACHE_HOST_ADDRESS", DistUtils.getCurrentHostAddress());
        props.setProperty("CACHE_LOCATION_PATH", DistUtils.getCurrentLocationPath());
        return this;
    }
    /** add default name for Distributed system*/
    public DistFactory withNameDefault() {
        return withName(DistConfig.DIST_NAME_VALUE_DEFAULT);
    }

    /** add simple property */
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
    /** with map of properties */
    public DistFactory withMap(Map<String, String> initialFactoryProperties) {
        for (Map.Entry<String, String> e: initialFactoryProperties.entrySet()) {
            props.setProperty(e.getKey(), e.getValue());
        }
        return this;
    }
    /** add JSON with properties */
    public DistFactory withJson(String jsonDefinition) {
        Map<String, String> map = JsonUtils.deserialize(jsonDefinition, new TypeReference<Map<String, String>>() {});
        return withMap(map);
    }
    /** add properties from file with properties format */
    public DistFactory withPropertiesFile(String propertiesFile) {
        try {
            Properties propFromFile = new Properties();
            propFromFile.load(new java.io.FileReader(propertiesFile));
            props.putAll(propFromFile);
        } catch (FileNotFoundException ex) {
            log.warn("Cannot find properties file for name: " + propertiesFile);
        } catch (IOException ex) {
            log.warn("Cannot read properties file: " + propertiesFile);
        }
        return this;
    }
    /** load properties from URL in properties format */
    public DistFactory withPropertiesUrl(String urlWithProperties) {
        try {
            Properties propFromUrl = new Properties();
            URL conn = new URL(urlWithProperties);
            propFromUrl.load(conn.openConnection().getInputStream());
            props.putAll(propFromUrl);
        } catch (IOException ex) {
            log.warn("Cannot read properties from URL: " + urlWithProperties);
        }
        return this;
    }

    /** with resolver to resolve values form String  */
    public DistFactory withResolver(Resolver r) {
        resolver.addResolver(r);
        return this;
    }


    /** define port for Web Api */
    public DistFactory withWebApiPort(int port) {
        props.setProperty(DistConfig.AGENT_API_PORT, ""+port);
        return this;
    }


    /** set default serializers */
    public DistFactory withSerializerDefault() {
        props.setProperty(DistConfig.SERIALIZER_DEFINITION, DistConfig.SERIALIZER_DEFINITION_SERIALIZABLE_VALUE);
        serializers.putAll(ComplexSerializer.parseSerializers(DistConfig.SERIALIZER_DEFINITION_SERIALIZABLE_VALUE));
        return this;
    }
    /** definition of serializer from String */
    public DistFactory withSerializer(String serializerDefinition) {
        serializers.putAll(ComplexSerializer.parseSerializers(serializerDefinition));
        props.setProperty(DistConfig.SERIALIZER_DEFINITION, serializerDefinition);
        return this;
    }
    public DistFactory withSerializer(DistSerializerTypes serializer, String... className) {
        // TODO: add better way to define serializers

        //serializers.put()
        //ComplexSerializer.createComplexSerializer()
        //serializers.putAll(ComplexSerializer.parseSerializers(serializerDefinition));
        //props.setProperty(DistConfig.SERIALIZER_DEFINITION, serializerDefinition);
        return this;
    }

    /** add serializer to Dist system */
    public DistFactory withSerializer(String className, DistSerializer ser) {
        serializers.put(className, ser);
        return this;
    }
    /** add custom serializations for this system */
    public DistFactory withSerializerCustom(String className, Function<Object, String> serializeFunction, BiFunction<String, String, Object> deserializeFunction) {
        serializers.put(className, new CustomSerializer(serializeFunction, deserializeFunction));
        return this;
    }


    /** add registration method as JDBC */
    public DistFactory withRegistrationJdbc(String url, String driver, String user, String pass) {
        props.setProperty(DistConfig.AGENT_REGISTRATION_JDBC_URL, url);
        props.setProperty(DistConfig.AGENT_REGISTRATION_JDBC_DRIVER, driver);
        props.setProperty(DistConfig.AGENT_REGISTRATION_JDBC_USER, user);
        props.setProperty(DistConfig.AGENT_REGISTRATION_JDBC_PASS, pass);
        return this;
    }

    /** add URL for Dist standalone application */
    public DistFactory withRegisterApplication(String cacheAppUrl) {
        props.setProperty(DistConfig.CACHE_APPLICATION_URL, cacheAppUrl);
        return this;
    }
    /** add URL for Dist application */
    public DistFactory withRegisterApplicationDefaultUrl() {
        props.setProperty(DistConfig.CACHE_APPLICATION_URL, DistConfig.CACHE_APPLICATION_URL_DEFAULT_VALUE);
        return this;
    }
    /** add registration with Elasticsearch */
    public DistFactory withRegistrationElasticsearch(String url, String user, String pass) {
        props.setProperty(DistConfig.AGENT_REGISTRATION_ELASTICSEARCH_URL, url);
        props.setProperty(DistConfig.AGENT_REGISTRATION_ELASTICSEARCH_USER, user);
        props.setProperty(DistConfig.AGENT_REGISTRATION_ELASTICSEARCH_PASS, pass);
        return this;
    }

    /** add times to inactivate other agents that have no ping for more than time declared,
     * remove all agents without ping for more than time declared
     * */
    public DistFactory withRegisterCleanAfter(long inactivateWithoutPingMs, long deleteWithoutPingMs) {
        props.setProperty(DistConfig.AGENT_INACTIVATE_AFTER, ""+inactivateWithoutPingMs);
        props.setProperty(DistConfig.AGENT_DELETE_AFTER, ""+deleteWithoutPingMs);
        return this;
    }


    /** define port for Socket communication */
    public DistFactory withServerSocketPort(int port) {
        props.setProperty(DistConfig.AGENT_SERVER_SOCKET_PORT, ""+port);
        return this;
    }

    /** define port to define value on which agent will be listening - this is Socket server */
    public DistFactory withServerSocketDefaultPort() {
        return withServerSocketPort(DistConfig.AGENT_SERVER_SOCKET_PORT_DEFAULT_VALUE);
    }
    /** define port for HTTP communication between agents */
    public DistFactory withServerHttpPort(int port) {
        props.setProperty(DistConfig.AGENT_SERVER_HTTP_PORT, ""+port);
        return this;
    }
    /** define port for DATAGRAM/ UDP communication between agents */
    public DistFactory withServerDatagramPort(int port) {
        props.setProperty(DistConfig.AGENT_SERVER_DATAGRAM_PORT, ""+port);
        return this;
    }

    /** get comma-separated list of defined cache storages */
    private String getExistingStorageList() {
        String existingStorages = props.getProperty(DistConfig.CACHE_STORAGES);
        if (existingStorages == null) {
            existingStorages = "";
        }
        return existingStorages;
    }
    /** add storage with HashMap */
    public DistFactory withCacheStorageHashMap() {
        props.setProperty(DistConfig.CACHE_STORAGES, getExistingStorageList() + "," + DistConfig.CACHE_STORAGE_VALUE_HASHMAP);
        return this;
    }
    /**  */
    public DistFactory withCacheStoragePriorityQueue() {
        props.setProperty(DistConfig.CACHE_STORAGES, getExistingStorageList() + "," + DistConfig.CACHE_STORAGE_VALUE_PRIORITYQUEUE);
        return this;
    }
    public DistFactory withCacheStorageWeakHashMap() {
        props.setProperty(DistConfig.CACHE_STORAGES, getExistingStorageList() + "," + DistConfig.CACHE_STORAGE_VALUE_WEAKHASHMAP);
        return this;
    }
    public DistFactory withCacheStorageElasticsearch(String url, String user, String pass) {
        props.setProperty(DistConfig.CACHE_STORAGES, getExistingStorageList() + "," + DistConfig.CACHE_STORAGE_VALUE_ELASTICSEARCH);
        props.setProperty(DistConfig.CACHE_STORAGE_ELASTICSEARCH_URL, url);
        props.setProperty(DistConfig.CACHE_STORAGE_ELASTICSEARCH_USER, user);
        props.setProperty(DistConfig.CACHE_STORAGE_ELASTICSEARCH_PASS, pass);
        return this;
    }
    /** add JDBC as external storage */
    public DistFactory withCacheStorageJdbc(String url, String driver, String user) {
        props.setProperty(DistConfig.CACHE_STORAGES, getExistingStorageList() + "," + DistConfig.CACHE_STORAGE_VALUE_JDBC);
        props.setProperty(DistConfig.CACHE_STORAGE_JDBC_URL, url);
        props.setProperty(DistConfig.CACHE_STORAGE_JDBC_DRIVER, driver);
        props.setProperty(DistConfig.CACHE_STORAGE_JDBC_USER, user);
        props.setProperty(DistConfig.CACHE_STORAGE_JDBC_DIALECT, driver);
        return this;
    }
    /** add JDBC as external storage */
    public DistFactory withCacheStorageJdbc(String url, String driver, String user, String pass) {
        props.setProperty(DistConfig.CACHE_STORAGES, getExistingStorageList() + "," + DistConfig.CACHE_STORAGE_VALUE_JDBC);
        props.setProperty(DistConfig.CACHE_STORAGE_JDBC_URL, url);
        props.setProperty(DistConfig.CACHE_STORAGE_JDBC_DRIVER, driver);
        props.setProperty(DistConfig.CACHE_STORAGE_JDBC_USER, user);
        props.setProperty(DistConfig.CACHE_STORAGE_JDBC_PASS, pass);
        props.setProperty(DistConfig.CACHE_STORAGE_JDBC_DIALECT, driver);
        return this;
    }
    /** add LocalDisk with custom path as cache storage for large objects */
    public DistFactory withCacheStorageLocalDisk(String basePath) {
        props.setProperty(DistConfig.CACHE_STORAGES, getExistingStorageList() + "," + DistConfig.CACHE_STORAGE_VALUE_LOCAL_DISK);
        props.setProperty(DistConfig.CACHE_STORAGE_LOCAL_DISK_PREFIX_PATH, basePath);
        return this;
    }
    /** add cache storage as Redis */
    public DistFactory withCacheStorageRedis(String url, int port) {
        props.setProperty(DistConfig.CACHE_STORAGES, getExistingStorageList() + "," + DistConfig.CACHE_STORAGE_VALUE_REDIS);
        props.setProperty(DistConfig.CACHE_STORAGE_REDIS_URL, url);
        props.setProperty(DistConfig.REDIS_PORT, ""+port);
        return this;
    }
    /** add cache storage as Mongodb */
    public DistFactory withCacheStorageMongo(String url, int port) {
        props.setProperty(DistConfig.CACHE_STORAGES, getExistingStorageList() + "," + DistConfig.CACHE_STORAGE_VALUE_MONGO);
        props.setProperty(DistConfig.CACHE_STORAGE_MONGODB_HOST, url);
        props.setProperty(DistConfig.REDIS_PORT, ""+port);
        return this;
    }
    /** add cache storage as Kafka */
    public DistFactory withCacheStorageKafka(String brokers, String topicName) {
        String existingProps = ""+props.getProperty(DistConfig.CACHE_STORAGES);
        props.setProperty(DistConfig.CACHE_STORAGES, existingProps + "," + DistConfig.CACHE_STORAGE_VALUE_KAFKA);
        props.setProperty(DistConfig.CACHE_STORAGE_KAFKA_BROKERS, brokers);
        props.setProperty(DistConfig.CACHE_STORAGE_KAFKA_TOPIC, topicName);
        return this;
    }
    /** add cache storage as Kafka */
    public DistFactory withCacheStorageKafka(String brokers) {
        return withCacheStorageKafka(brokers, DistConfig.CACHE_STORAGE_KAFKA_BROKERS_DEFAULT_VALUE);
    }


    /** CACHE setting - object TTL - time to live */
    public DistFactory withCacheObjectTimeToLive(long timeToLiveMs) {
        props.setProperty(DistConfig.CACHE_TTL, ""+timeToLiveMs);
        return this;
    }
    public DistFactory withCacheMaxObjectsAndItems(int maxObjects, int maxItems) {
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
    public DistFactory withTimerStorageClean(long periodMs) {
        props.setProperty(DistConfig.TIMER_CLEAN_STORAGE_PERIOD, ""+periodMs);
        return this;
    }
    /** define internal timer delay for check  */
    public DistFactory withTimerRegistrationPeriod(long periodMs) {
        props.setProperty(DistConfig.TIMER_REGISTRATION_PERIOD, ""+periodMs);
        return this;
    }
    /** define internal timer delay for check servers and clients */
    public DistFactory withTimerServerPeriod(long periodMs) {
        props.setProperty(DistConfig.TIMER_SERVER_CLIENT_PERIOD, ""+periodMs);
        return this;
    }
    /** set cache policy, this is overwriting existing policy */
    public DistFactory withCachePolicy(CachePolicy policy) {
        this.policy = policy;
        return this;
    }

}
