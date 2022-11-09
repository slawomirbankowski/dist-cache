package com.cache.managers;

import com.cache.agent.AgentObject;
import com.cache.api.CacheableMethod;
import com.cache.base.Cache;
import com.cache.base.CachePolicyBase;
import com.cache.base.CacheStorageBase;
import com.cache.storage.KafkaStorage;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;

/** manager to connect all storages, policies, agents
 * to perform clean based on time
 * replace all cache with fresh objects
 * */
public class CacheManager implements Cache {

    /** UUID for cache manager - globaly unique */
    private String cacheManagerGuid = UUID.randomUUID().toString();
    /** creation date and time of this cache manager */
    private LocalDateTime createdDateTime = LocalDateTime.now();
    /** cache properties to initialize all storages, agent, policies, */
    private Properties cacheProps = null;
    /** agent object connected to given manager
     * agent can connect to different cache managers in the same group
     * to cooperate as distributed cache
     *  */
    private AgentObject agent = new AgentObject();
    /** all storages to store cache objects - there could be internal storages,
     * Elasticsearch, Redis, local disk, JDBC database with indexed table, and many others */
    private Map<String, CacheStorageBase> storages = new HashMap<>();
    /** list of policies for given cache object to check to what caches that object should be add */
    private List<CachePolicyBase> policies = new LinkedList<CachePolicyBase>();
    /** initialize current manager with properties
     * this is creating storages, connecting to storages
     * creating cache policy, create agent and connecting to other cache agents */
    public CacheManager(Properties p) {
        this.cacheProps = p;
        // TODO: finishing initialization - to be done, creating agent, storages, policies
        initializeStorages();
    }
    private void initializeStorages() {
        // storages.put("", new KafkaStorage());
        // storages.put("", new InternalHashMapCacheStorage());

    }
    /** if cache contains given key */
    public boolean contains(String key) {
        return true;
    }
    // TODO: implement many methods to be used for wrapping DAO and other slow methods
    /** execute with cache for key
     * if object in cache exists and it is valid, then this object would be returned
     * if not exists then method would be executed to get object, object would be put to cache and returned */
    public <T> Object withCache(String key, CacheableMethod m, T sampleObj) {
        // TODO: implement withCache - add type, key, get object from cache OR from method

        return "";
    }

    public <T> Object withCache(String key, Method method, Object obj) {
        // TODO: get object with cache
        for (CacheStorageBase storage: storages.values()) {
            if (storage.isInternal()) {
                // TO

            }
        }
        // TODO:
        return "";
    }

}
