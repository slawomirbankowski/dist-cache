package com.cache.managers;

import com.cache.agent.AgentObject;
import com.cache.api.CacheableMethod;
import com.cache.base.CacheManagerBase;
import com.cache.base.CachePolicyBase;
import com.cache.base.CacheStorageBase;
import com.cache.storage.KafkaStorage;

import java.time.LocalDateTime;
import java.util.*;

/** manager to connect all storages, policies, agents
 * to perform clean based on time
 * replace all cache with fresh objects
 * */
public class CacheManager implements CacheManagerBase {

    /** UUID for cache manager - globaly unique */
    private String cacheManagerGuid = UUID.randomUUID().toString();
    /** creation date and time of this cache manager */
    private LocalDateTime createdDateTime = LocalDateTime.now();

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
        // TODO: finishing initialization - to be done, creating agent, storages, policies
        initializeStorages();

    }
    private void initializeStorages() {
        // storages.put("", new KafkaStorage());
        // storages.put("", new InternalHashMapCacheStorage());

    }
    /** */
    public boolean contains(String key) {
        return true;
    }
    // TODO: implement many methods to be used for wrapping DAO and other slow methods
    /** */
    public Object withCache(String key, CacheableMethod m) {
        // TODO: implement withCache - add type, key, get object from cache OR from method
        return "";
    }
    public Object withCache() {
        return "";
    }
}
