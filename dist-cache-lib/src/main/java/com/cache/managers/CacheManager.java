package com.cache.managers;

import com.cache.agent.AgentObject;
import com.cache.api.*;
import com.cache.base.CacheBase;
import com.cache.base.CachePolicyBase;
import com.cache.base.CacheStorageBase;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/** manager to connect all storages, policies, agents
 * to perform clean based on time
 * replace all cache with fresh objects
 * */
public class CacheManager extends CacheBase {

    /** timer to schedule important check methods */
    private final Timer timer = new Timer();
    private LinkedList<TimerTask> timerTasks = new LinkedList<>();
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
        super(p);
        // TODO: finishing initialization - to be done, creating agent, storages, policies
        initializeStorages();
        initializeAgent();
        initializePolicies();
        initializeTimer();
    }

    /** initialize all storages from configuration*/
    private void initializeStorages() {
        StorageInitializeParameter initParams = new StorageInitializeParameter(cacheProps, this);
        String cacheStorageList = ""+cacheProps.getProperty(CacheConfig.CACHE_STORAGES);
        log.info("Initializing cache storages: " + cacheStorageList);
        Arrays.stream(cacheStorageList.split(","))
                .distinct()
                //.filter(st -> !st.isBlank() && st.isEmpty())
                .forEach(storageClass -> initializeSingleStorage(initParams, storageClass));
    }
    /** initialize single storage */
    private void initializeSingleStorage(StorageInitializeParameter initParams, String className) {
        try {
            String fullClassName = "com.cache.storage." + className;
            log.debug("Initializing storage for class: " + fullClassName + ", current storages: " + storages.size());
            CacheStorageBase storage = (CacheStorageBase)Class.forName(fullClassName)
                    .getConstructor(StorageInitializeParameter.class)
                    .newInstance(initParams);
            CacheStorageBase prevStorage = storages.put(storage.toString(), storage);
            log.info("Initialized storage: " + storage.getStorageUid() + ", current storages: " + storages.size());
            if (prevStorage != null) {
                log.debug("Got previous storage to dispose: " + prevStorage.getStorageUid());
                prevStorage.disposeStorage();
            }
        } catch (Exception ex) {
            // TODO: report problem with storage creation
            log.warn("Cannot initialize storage for class: " + className);
        }
    }

    /** initialize Agent to communicate with other CacheManagers */
    public void initializeAgent() {

    }

    /** initialize policies */
    public void initializePolicies() {

    }

    public void initializeTimer() {
        long delayMs = 1000;
        long periodMs = 1000;
        TimerTask onTimeTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    onTime();
                } catch (Exception ex) {
                    // TODO: mark exception
                }
            }
        };
        timerTasks.add(onTimeTask);
        timer.scheduleAtFixedRate(onTimeTask, delayMs, periodMs);
    }

    /** */
    protected void onClose() {
        log.info("Stopping all timer tasks");
        timerTasks.forEach(x -> x.cancel());
        timer.cancel();
        log.info("Removing caches and dispose storages");
        synchronized (storages) {
            for (CacheStorageBase value : storages.values()) {
                value.clearCaches(1);
                value.disposeStorage();
            }
            storages.clear();
        }
        log.info("Clearing issues");
        issues.clear();
    }


    /** set object in all or one internal caches */
    private List<Optional<CacheObject>> setItemInternal(CacheObject co) {
        addedItemsSequence.incrementAndGet();
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
        log.info("===> Got object from external method/supplier, time: " + acquireTimeMs);
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

    /** clear caches with given clear cache */
    public int clearCaches(int clearMode) {
        storages.values().stream().forEach(x -> x.clearCaches(clearMode));
        return 1;
    }
    public Set<String> getStorageKeys() {
        return storages.keySet();
    }
    /** get number of items in cache */
    public int getItemsCount() {
        return storages.values().stream().map(x -> x.getItemsCount()).reduce((x, y) -> x+y).orElse(0);
    }
    public Map<String, Integer> getItemsCountPerStorage() {
        Map<String, Integer> cnts = new HashMap<>();
        storages.values().stream().forEach(x -> cnts.put(x.getStorageUid(), x.getItemsCount()));
        return cnts;
    }

    /** clear cache contains given partial key */
    public int clearCacheContains(String str) {
        storages.values().stream().forEach(x -> x.clearCacheContains(str));
        return 1;
    }
    /** check cache every X seconds to clear TTL caches */
    public void onTime() {
        long checkSeq = checkSequence.incrementAndGet();
        storages.values().stream().forEach(x -> x.onTime(checkSeq));
    }
    /** get item from cache if exists or None */
    public <T> Optional<T> getItem(String key) {
        for (CacheStorageBase storage: storages.values()) {
            Optional<CacheObject> fromCache = storage.getItem(key);
            if (fromCache.isPresent()) {
                try {
                    CacheObject co = fromCache.get();
                    co.use();
                    // TODO: if this is not internal cache - need to increase usage and lastUseDate ???
                    return Optional.ofNullable((T) co.getValue().getObject());
                } catch (Exception ex) {
                    // TODO: log problem with casting value from cache for given key into specific type
                }
            }
        }
        return Optional.empty();
    }

    /** execute with cache for key
     * if object in cache exists and it is valid, then this object would be returned
     * if not exists then method would be executed to get object, object would be put to cache and returned */
    public <T> T withCache(String key, CacheableMethod<T> m, CacheMode mode) {
        try {
            Optional<T> itemFromCache = getItem(key);
            if (itemFromCache.isPresent()) {
                return itemFromCache.get();
            } else {
                return acquireObject(key, m);
            }
        } catch (Exception ex) {
            return null;
        }
    }
    public <T> T withCache(String key, Supplier<? extends T> supplier, CacheMode mode) {
        return withCache(key, (CacheableMethod<T>) key1 -> supplier.get(), mode);
    }
    public <T> T withCache(String key, Function<String, ? extends T> mapper, CacheMode mode) {
        return withCache(key, (CacheableMethod<T>) mapper, mode);
    }
    public <T> T withCache(String key, Method method, Object obj, CacheMode mode) {
        try {
            // TODO: implement getting value by reflection
            return withCache(key, (CacheableMethod<T>) key1 -> (T)CacheUtils.getFromMethod(method, obj), mode);
        } catch (Exception ex) {
            // TODO:
            return null;
        }
    }

}
