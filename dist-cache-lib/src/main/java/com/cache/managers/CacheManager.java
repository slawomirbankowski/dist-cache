package com.cache.managers;

import com.cache.agent.AgentInstance;
import com.cache.api.*;
import com.cache.base.CacheBase;
import com.cache.base.CachePolicyBase;
import com.cache.base.CacheStorageBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(CacheManager.class);
    /** timer to schedule important check methods */
    private final Timer timer = new Timer();
    private final LinkedList<TimerTask> timerTasks = new LinkedList<>();
    /** agent object connected to given manager
     * agent can connect to different cache managers in the same group
     * to cooperate as distributed cache
     *  */
    private AgentInstance agent = new AgentInstance(this);
    /** all storages to store cache objects - there could be internal storages,
     * Elasticsearch, Redis, local disk, JDBC database with indexed table, and many others */
    private final Map<String, CacheStorageBase> storages = new HashMap<>();
    /** list of policies for given cache object to check to what caches that object should be add */
    private final List<CachePolicyBase> policies = new LinkedList<CachePolicyBase>();

    /** initialize current manager with properties
     * this is creating storages, connecting to storages
     * creating cache policy, create agent and connecting to other cache agents */
    public CacheManager(CacheConfig cacheCfg) {
        super(cacheCfg);
        // TODO: finishing initialization - to be done, creating agent, storages, policies
        initializeStorages();
        initializeAgent();
        initializePolicies();
        initializeTimer();
        addEvent(new CacheEvent(this, "CacheManager", CacheEvent.EVENT_CACHE_START));
    }
    public CacheManager(CacheConfig cacheCfg, Map<String, Function<CacheEvent, String>> callbacksMethods) {
        super(cacheCfg, callbacksMethods);
        // TODO: finishing initialization - to be done, creating agent, storages, policies
        initializeStorages();
        initializeAgent();
        initializePolicies();
        initializeTimer();
        addEvent(new CacheEvent(this, "CacheManager", CacheEvent.EVENT_CACHE_START));
    }
    /** get information about all storages in this cache */
    public List<StorageInfo> getStoragesInfo() {
        return storages.values().stream()
                .map(CacheStorageBase::getStorageInfo)
                .collect(Collectors.toList());
    }
    /** get number of storages */
    public int getStoragesCount() {
        return storages.size();
    }

    /** initialize all storages from configuration*/
    private void initializeStorages() {
        addEvent(new CacheEvent(this, "initializeStorages", CacheEvent.EVENT_INITIALIZE_STORAGES));
        StorageInitializeParameter initParams = new StorageInitializeParameter(cacheCfg, this);
        String cacheStorageList = ""+cacheCfg.getProperty(CacheConfig.CACHE_STORAGES);
        log.info("Initializing cache storages: " + cacheStorageList);
        Arrays.stream(cacheStorageList.split(","))
                .distinct()
                //.filter(st -> !st.isBlank() && st.isEmpty()) // TODO: check what should be put here
                .forEach(storageClass -> initializeSingleStorage(initParams, storageClass));
    }
    /** initialize single storage */
    private void initializeSingleStorage(StorageInitializeParameter initParams, String className) {
        try {
            String fullClassName = "com.cache.storage." + className;
            addEvent(new CacheEvent(this, "initializeStorages", CacheEvent.EVENT_INITIALIZE_STORAGE, fullClassName));
            log.debug("Initializing storage for class: " + fullClassName + ", current storages: " + storages.size());
            CacheStorageBase storage = (CacheStorageBase)Class.forName(fullClassName)
                    .getConstructor(StorageInitializeParameter.class)
                    .newInstance(initParams);
            CacheStorageBase prevStorage = storages.put(storage.getStorageUid(), storage);
            log.info("Initialized storage: " + storage.getStorageUid() + ", current storages: " + storages.size());
            if (prevStorage != null) {
                log.debug("Got previous storage to dispose: " + prevStorage.getStorageUid());
                addEvent(new CacheEvent(this, "initializeStorages", CacheEvent.EVENT_DISPOSE_STORAGE, fullClassName));
                prevStorage.disposeStorage();
            }
        } catch (Exception ex) {
            addIssue("initializeSingleStorage", ex);
            // TODO: report problem with storage creation
            log.warn("Cannot initialize storage for class: " + className);
        }
    }

    /** initialize Agent to communicate with other CacheManagers */
    protected void initializeAgent() {
        addEvent(new CacheEvent(this, "initializeAgent", CacheEvent.EVENT_INITIALIZE_AGENT));
        agent.initializeAgent();
    }

    /** initialize policies */
    protected void initializePolicies() {
        addEvent(new CacheEvent(this, "initializePolicies", CacheEvent.EVENT_INITIALIZE_POLICIES));
    }

    protected void initializeTimer() {
        addEvent(new CacheEvent(this, "initializeTimer", CacheEvent.EVENT_INITIALIZE_TIMERS));
        // initialization for clean
        long cleanDelayMs = cacheCfg.getPropertyAsLong(CacheConfig.CACHE_TIMER_DELAY, CacheConfig.CACHE_TIMER_DELAY_VALUE);
        long cleanPeriodMs = cacheCfg.getPropertyAsLong(CacheConfig.CACHE_TIMER_PERIOD, CacheConfig.CACHE_TIMER_PERIOD_VALUE);
        log.info("Scheduling clean timer task for cache: " + getCacheGuid());
        TimerTask onTimeCleanTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    onTimeClean();
                } catch (Exception ex) {
                    addIssue("initializeTimer:clean", ex);
                }
            }
        };
        timerTasks.add(onTimeCleanTask);
        timer.scheduleAtFixedRate(onTimeCleanTask, cleanDelayMs, cleanPeriodMs);
        addEvent(new CacheEvent(this, "initializeTimer", CacheEvent.EVENT_INITIALIZE_TIMER_CLEAN));

        // initialization for communicate
        long communicateDelayMs = cacheCfg.getPropertyAsLong(CacheConfig.CACHE_TIMER_COMMUNICATE_DELAY, CacheConfig.CACHE_TIMER_COMMUNICATE_DELAY_VALUE);
        long communicatePeriodMs = cacheCfg.getPropertyAsLong(CacheConfig.CACHE_TIMER_COMMUNICATE_DELAY, CacheConfig.CACHE_TIMER_COMMUNICATE_DELAY_VALUE);
        log.info("Scheduling communicating timer task for cache: " + getCacheGuid());
        TimerTask onTimeCommunicateTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    onTimeCommunicate();
                } catch (Exception ex) {
                    // TODO: mark exception
                    addIssue("initializeTimer:communicate", ex);
                }
            }
        };
        timerTasks.add(onTimeCommunicateTask);
        timer.scheduleAtFixedRate(onTimeCommunicateTask, communicateDelayMs, communicatePeriodMs);
        addEvent(new CacheEvent(this, "initializeTimer", CacheEvent.EVENT_INITIALIZE_TIMER_COMMUNICATE));
        // initialization for
        long ratioDelayMs = cacheCfg.getPropertyAsLong(CacheConfig.CACHE_TIMER_RATIO_DELAY, CacheConfig.CACHE_TIMER_RATIO_DELAY_VALUE);
        long ratioPeriodMs = cacheCfg.getPropertyAsLong(CacheConfig.CACHE_TIMER_RATIO_DELAY, CacheConfig.CACHE_TIMER_RATIO_DELAY_VALUE);
        log.info("Scheduling ratio timer task for cache: " + getCacheGuid());
        TimerTask onTimeHitRatioTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    onTimeRatio();
                } catch (Exception ex) {
                    addIssue("initializeTimer:ratio", ex);
                }
            }
        };
        timerTasks.add(onTimeHitRatioTask);
        timer.scheduleAtFixedRate(onTimeHitRatioTask, ratioDelayMs, ratioPeriodMs);
        addEvent(new CacheEvent(this, "initializeTimer", CacheEvent.EVENT_INITIALIZE_TIMER_RATIO));
    }

    /** executing close of this cache */
    protected void onClose() {
        addEvent(new CacheEvent(this, "onClose", CacheEvent.EVENT_CLOSE_BEGIN));
        log.info("Stopping all timer tasks");
        timerTasks.forEach(TimerTask::cancel);
        timer.cancel();
        log.info("Removing caches and dispose storages");
        synchronized (storages) {
            for (CacheStorageBase value : storages.values()) {
                value.clearCaches(1);
                value.disposeStorage();
            }
            storages.clear();
        }
        log.info("Clearing events");
        events.clear();
        log.info("Clearing issues");
        issues.clear();
        addEvent(new CacheEvent(this, "onClose", CacheEvent.EVENT_CLOSE_END));
    }

    /** set object in all or one internal caches */
    private List<CacheObject> setItemInternal(CacheObject co) {
        addedItemsSequence.incrementAndGet();
        return storages.values().stream()
                .filter(CacheStorageBase::isInternal)
                .flatMap(storage -> storage.setObject(co).stream())
                .collect(Collectors.toList());
    }

    /** acquire object from external method, this could be slow because if could be a database query of external service
     * we would like to put cache around */
    private <T> T acquireObject(String key, CacheableMethod<T> acquireMethod, CacheMode mode, Set<String> groups) {
        // Measure time of getting this object from cache
        long startActTime = System.currentTimeMillis();
        hitRatio.miss(); // hit ratio - add miss event
        T objFromMethod = acquireMethod.get(key);
        long acquireTimeMs = System.currentTimeMillis()-startActTime; // this is time of getting this object from method
        log.info("===> Got object from external method/supplier, time: " + acquireTimeMs);
        CacheObject co = new CacheObject(key, objFromMethod, acquireTimeMs, acquireMethod, mode, groups);
        // TODO: need to set object in internal caches
        setItemInternal(co);
        //Optional<CacheObject> prev = setItem(co);
        //prev.ifPresent(CacheObject::releaseObject);
        return objFromMethod;
    }
    /** set object to cache */
    public CacheSetBack setCacheObject(String key, Object value, CacheMode mode, Set<String> groups) {
        log.info("Set cache, key=" + key + ", mode=" + mode.getMode());
        CacheableMethod<Object> acquireMethod = new CacheableMethod<Object>() {
            @Override
            public Object get(String key) {
                return value;
            }
        };
        CacheObject co = new CacheObject(key, value, 0L, acquireMethod, mode, groups);
        List<CacheObject> prevObjects = setItemInternal(co);
        return new CacheSetBack(prevObjects, co);
    }

    /** if cache contains given key */
    public boolean contains(String key) {
        return storages.values().stream().anyMatch(x -> x.contains(key));
    }


    /** clear caches with given clear cache */
    public int clearCaches(int clearMode) {
        addEvent(new CacheEvent(this, "clearCaches", CacheEvent.EVENT_CACHE_CLEAN));
        storages.values().stream().forEach(x -> x.clearCaches(clearMode));
        return 1;
    }
    public Set<String> getStorageKeys() {
        return storages.keySet();
    }
    /** get all cache keys that contains given string */
    public Set<String> getCacheKeys(String containsStr) {
        return storages.values()
                .stream()
                .filter(x -> x.isInternal())
                .flatMap(x -> x.getKeys(containsStr).stream())
                .collect(Collectors.toSet());
    }
    /** get values stored in cache
     * this might returns only first X values */
    public List<CacheObjectInfo> getCacheValues(String containsStr) {
        return storages.values()
                .stream()
                .filter(x -> x.isInternal())
                .flatMap(x -> x.getValues(containsStr).stream())
                .collect(Collectors.toList());
    }

    /** get number of objects in all storages
     * if one object is inserted into cache - this is still one object even if this is a list of 1000 elements */
    public int getObjectsCount() {
        return storages.values().stream().map(x -> x.getObjectsCount()).reduce((x, y) -> x+y).orElse(0);
    }

    /** get number of items in cache, if a list with 100 elements is inserted into cache
     * then this is 1 object but 100 items  */
    public int getItemsCount() {
        return storages.values().stream().map(x -> x.getItemsCount()).reduce((x, y) -> x+y).orElse(0);
    }
    public Map<String, Integer> getItemsCountPerStorage() {
        Map<String, Integer> cnts = new HashMap<>();
        storages.values().stream().forEach(x -> cnts.put(x.getStorageUid(), x.getItemsCount()));
        return cnts;
    }
    /** get number of objects in each storage */
    public Map<String, Integer> getObjectsCountPerStorage() {
        Map<String, Integer> cnts = new HashMap<>();
        storages.values().stream().forEach(x -> cnts.put(x.getStorageUid(), x.getObjectsCount()));
        return cnts;
    }
    /** clear cache contains given partial key */
    public int clearCacheContains(String str) {
        storages.values().stream().forEach(x -> x.clearCacheContains(str));
        return 1;
    }
    /** check cache every X seconds to clear TTL caches */
    public void onTimeClean() {
        long checkSeq = checkSequence.incrementAndGet();
        addEvent(new CacheEvent(this, "onTimeClean", CacheEvent.EVENT_TIMER_CLEAN));
        storages.values().stream().forEach(x -> x.onTimeClean(checkSeq));
    }
    public void onTimeCommunicate() {
        addEvent(new CacheEvent(this, "onTimeClean", CacheEvent.EVENT_TIMER_COMMUNICATE));
        agent.onTimeCommunicate();
    }
    public void onTimeRatio() {
        //addEvent(new CacheEvent(this, "onTimeClean", CacheEvent.EVENT_TIMER_COMMUNICATE));

    }

    /** get item from cache if exists or None */
    public Optional<CacheObject> getCacheObject(String key) {
        for (CacheStorageBase storage: storages.values()) {
            Optional<CacheObject> fromCache = storage.getObject(key);
            if (fromCache.isPresent()) {
                try {
                    CacheObject co = fromCache.get();
                    co.use();
                    // TODO: if this is not internal cache - need to increase usage and lastUseDate ???
                    return Optional.ofNullable(co);
                } catch (Exception ex) {
                    // TODO: log problem with casting value from cache for given key into specific type
                    addIssue("getObject", ex);
                }
            }
        }
        return Optional.empty();
    }
    /** get item from cache if exists or None */
    public <T> Optional<T> getObject(String key) {
        for (CacheStorageBase storage: storages.values()) {
            Optional<CacheObject> fromCache = storage.getObject(key);
            if (fromCache.isPresent()) {
                try {
                    CacheObject co = fromCache.get();
                    co.use();
                    // TODO: if this is not internal cache - need to increase usage and lastUseDate ???
                    return Optional.ofNullable((T) co.getValue());
                } catch (Exception ex) {
                    // TODO: log problem with casting value from cache for given key into specific type
                    addIssue("getObject", ex);
                }
            }
        }
        return Optional.empty();
    }


    /** execute with cache for key
     * if object in cache exists and it is valid, then this object would be returned
     * if not exists then method would be executed to get object, object would be put to cache and returned */
    public <T> T withCache(String key, CacheableMethod<T> m, CacheMode mode, Set<String> groups) {
        try {
            Optional<T> itemFromCache = getObject(key);
            if (itemFromCache.isPresent()) {
                hitRatio.hit();
            }
            return itemFromCache.orElseGet(() -> acquireObject(key, m, mode, groups));
        } catch (Exception ex) {
            addIssue("withCache", ex);
            return null;
        }
    }
    public <T> T withCache(String key, Supplier<? extends T> supplier, CacheMode mode, Set<String> groups) {
        return withCache(key, (CacheableMethod<T>) key1 -> supplier.get(), mode, groups);
    }
    public <T> T withCache(String key, Function<String, ? extends T> mapper, CacheMode mode, Set<String> groups) {
        return withCache(key, new CacheableMethod<T>() {
            @Override
            public T get(String key) {
                return mapper.apply(key);
            }
        }, mode, groups);
    }
    public <T> T withCache(String key, Method method, Object obj, CacheMode mode, Set<String> groups) {
        try {
            // TODO: implement getting value by reflection
            return withCache(key, (CacheableMethod<T>) key1 -> (T)CacheUtils.getFromMethod(method, obj), mode, groups);
        } catch (Exception ex) {
            addIssue("withCache", ex);
            return null;
        }
    }

}
