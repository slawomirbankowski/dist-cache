package com.cache.managers;

import com.cache.agent.AgentObject;
import com.cache.api.*;
import com.cache.api.Cache;
import com.cache.base.CachePolicyBase;
import com.cache.base.CacheStorageBase;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    private final Map<String, CacheStorageBase> storages = new HashMap<>();
    /** list of policies for given cache object to check to what caches that object should be add */
    private List<CachePolicyBase> policies = new LinkedList<CachePolicyBase>();
    /** queue of issues reported when using cache */
    private Queue<String> issues = new LinkedList<>();
    // TODO: add callbacks to be called when something important is happening to cache, each callback method would be called in different places of dist-cache


    /** initialize current manager with properties
     * this is creating storages, connecting to storages
     * creating cache policy, create agent and connecting to other cache agents */
    public CacheManager(Properties p) {
        this.cacheProps = p;
        // TODO: finishing initialization - to be done, creating agent, storages, policies
        initializeStorages();
        initializeAgent();
        initializePolicies();
    }

    /** get unique identifier for this CacheManager object */
    public String getCacheManagerGuid() { return cacheManagerGuid; }
    /** get date and time of creation for this CacheManager */
    public LocalDateTime getCreatedDateTime() { return createdDateTime; }

    /** initialize all storages from configuration*/
    private void initializeStorages() {
        StorageInitializeParameter initParams = new StorageInitializeParameter();
        Arrays.stream(cacheProps.getProperty(CacheConfig.CACHE_STORAGES).split(","))
                .distinct()
                .forEach(storageClass -> initializeSingleStorage(initParams, storageClass));
    }
    /** initialize single storage */
    private void initializeSingleStorage(StorageInitializeParameter initParams, String className) {
        try {
            String fullClassName = "com.cache.storage." + className;
            CacheStorageBase storage = (CacheStorageBase)Class.forName(fullClassName)
                    .getConstructor()
                    .newInstance(initParams);
            CacheStorageBase prevStorage = storages.put(storage.toString(), storage);
            if (prevStorage != null) {
                prevStorage.disposeStorage();
            }
        } catch (Exception ex) {
            // TODO: report problem with storage creation
        }
    }

    /** initialize Agent to communicate with other CacheManagers */
    public void initializeAgent() {

    }

    /** initialize policies */
    public void initializePolicies() {

    }

    /** set object in all or one internal caches */
    private List<Optional<CacheObject>> setItemInternal(CacheObject co) {
        return storages.values().stream()
                .filter(x -> x.isInternal())
                .map(storage -> storage.setItem(co))
                .collect(Collectors.toList());
    }

    /** acquire object from external method, this could be slow because if could be a database query of external service
     * we would like to put cache around */
    private <T> T acquireObject(String key, CacheableMethod<T> m) {
        // Measure time of getting this object from cache
        long startActTime = System.currentTimeMillis();
        T objFromMethod = m.get(key);
        long acquireTimeMs = System.currentTimeMillis()-startActTime; // this is time of getting this object from method
        // TODO: add this object to cache
        CacheObject co = new CacheObject(key, new CacheableWrapper(objFromMethod), acquireTimeMs, m);
        // TODO: need to set object in internal caches
        setItemInternal(co);
        //Optional<CacheObject> prev = setItem(co);
        //prev.ifPresent(CacheObject::releaseObject);
        return objFromMethod;
    }

    /** if cache contains given key */
    public boolean contains(String key) {
        return storages.values().stream().anyMatch(x -> x.contains(key));
    }

    // TODO: implement many methods to be used for wrapping DAO and other slow methods
    /** execute with cache for key
     * if object in cache exists and it is valid, then this object would be returned
     * if not exists then method would be executed to get object, object would be put to cache and returned */
    public <T> T withCache(String key, CacheableMethod<T> m) {
        // TODO: change this to get from internal first and then from external in specific order
        for (CacheStorageBase storage: storages.values()) {
            Optional<CacheObject> fromCache = storage.getItem(key);
            if (fromCache.isPresent()) {
                try {
                    CacheObject co = fromCache.get();
                    co.use();
                    // TODO: if this is not internal cache - need to increase usage and lastUseDate ???
                    return (T) co.getValue();
                } catch (Exception ex) {
                    // TODO: log problem with casting value from cache for given key into specific type
                }
            }
        }
        // no value found in storages
        return acquireObject(key, m);
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
