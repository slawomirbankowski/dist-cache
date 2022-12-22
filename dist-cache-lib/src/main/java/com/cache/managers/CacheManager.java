package com.cache.managers;

import com.cache.agent.AgentInstance;
import com.cache.api.*;
import com.cache.base.CacheBase;
import com.cache.base.CacheStorageBase;
import com.cache.interfaces.Agent;
import com.cache.api.DistMessage;
import com.cache.utils.DistMessageProcessor;
import com.cache.utils.DistWebApiProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    /** tasks to be executed periodicaly like cleaning cache or refreshing statistics */
    private final LinkedList<TimerTask> timerTasks = new LinkedList<>();
    /** last time of clean for storages */
    private long lastCleanTime = System.currentTimeMillis();
    /** agent object connected to given manager
     * agent can connect to different cache managers and different services in the same group
     * to cooperate as distributed cache
     *  */
    private AgentInstance agent;
    /** all storages to store cache objects - there could be internal storages,
     * Elasticsearch, Redis, local disk, JDBC database with indexed table, and many others */
    private final Map<String, CacheStorageBase> storages = new HashMap<>();
    /** processor that is connecting message method with current class method to be executed */
    private final DistMessageProcessor messageProcessor = new DistMessageProcessor()
            .addMethod( ServiceMethods.agentCacheClear.getMethodName(), this::messageClearCache)
            .addMethod("getStorages", this::messageGetStorages)
            .addMethod("getCacheInfo", this::messageGetCacheInfo)
            .addMethod("getStats", this::messageGetCacheStats)
            .addMethod("getConfig", this::messageGetConfig)
            .addMethod("setObject", this::messageSetObject)
            .addMethod("getObject", this::messageGetObject);
    private final DistWebApiProcessor webApiProcessor = new DistWebApiProcessor()
            .addHandler("ping", (m, req) -> req.responseOkText("ping"))
            .addHandler("createdDate", (m, req) -> req.responseOkText( getCreateDate().toString()))
            .addHandler("guid", (m, req) -> req.responseOkText(getCacheGuid()));

    /** initialize current manager with properties
     * this is creating storages, connecting to storages
     * creating cache policy, create agent and connecting to other cache agents */
    public CacheManager(AgentInstance agent, DistConfig cacheCfg, CachePolicy policy) {
        super(cacheCfg, policy);
        this.agent = agent;
        // TODO: finishing initialization - to be done, creating agent, storages, policies
        initializeStorages();
        initializeAgent();
        initializeTimer();
        addEvent(new CacheEvent(this, "CacheManager", CacheEvent.EVENT_CACHE_START));
    }
    /** get information about all storages in this cache */
    public List<StorageInfo> getStoragesInfo() {
        return storages.values().stream()
                .map(CacheStorageBase::getStorageInfo)
                .collect(Collectors.toList());
    }
    /** get agent for communication with other services in distributed environment */
    public Agent getAgent() {
        return agent;
    }
    /** get number of storages */
    public int getStoragesCount() {
        return storages.size();
    }

    /** process message, returns status */
    public DistMessage processMessage(DistMessage msg) {
        log.info("Process message by CacheManager, message: " + msg);
        cacheStats.processMessage();
        return messageProcessor.process(msg.getMethod(), msg);
    }
    /** handle API request in this Web API for this service */
    public AgentWebApiResponse handleRequest(AgentWebApiRequest request) {
        cacheStats.handleRequest();
        return webApiProcessor.handleRequest(request);
    }
    /** initialize all storages from configuration */
    private void initializeStorages() {
        addEvent(new CacheEvent(this, "initializeStorages", CacheEvent.EVENT_INITIALIZE_STORAGES));
        StorageInitializeParameter initParams = new StorageInitializeParameter(this);
        String cacheStorageList = ""+cacheCfg.getProperty(DistConfig.CACHE_STORAGES);
        log.info("Initializing cache storages: " + cacheStorageList);
        Arrays.stream(cacheStorageList.split(","))
                .distinct()
                .filter(x -> !x.isEmpty())
                .forEach(storageClass -> initializeSingleStorage(initParams, storageClass));
    }

    /** initialize single storage */
    private void initializeSingleStorage(StorageInitializeParameter initParams, String className) {
        try {
            cacheStats.initializeSingleStorage();
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
            log.warn("Cannot initialize storage for class: " + className);
        }
    }

    /** initialize Agent to communicate with other CacheManagers */
    protected void initializeAgent() {
        addEvent(new CacheEvent(this, "initializeAgent", CacheEvent.EVENT_INITIALIZE_AGENT));
        agent.getAgentServices().registerService(this);
    }
    /** */
    protected void initializeTimer() {
        addEvent(new CacheEvent(this, "initializeTimer", CacheEvent.EVENT_INITIALIZE_TIMERS));
        // initialization for clean
        long cleanDelayMs = cacheCfg.getPropertyAsLong(DistConfig.TIMER_DELAY, DistConfig.TIMER_DELAY_VALUE);
        long cleanPeriodMs = cacheCfg.getPropertyAsLong(DistConfig.TIMER_PERIOD, DistConfig.TIMER_PERIOD_VALUE);
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
        // initialization for
        long ratioDelayMs = cacheCfg.getPropertyAsLong(DistConfig.TIMER_RATIO_DELAY, DistConfig.TIMER_RATIO_DELAY_VALUE);
        long ratioPeriodMs = cacheCfg.getPropertyAsLong(DistConfig.TIMER_RATIO_DELAY, DistConfig.TIMER_RATIO_DELAY_VALUE);
        log.info("Scheduling ratio timer task for cache: " + getCacheGuid());
        TimerTask onTimeStatsTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    onTimeStatsRefresh();
                } catch (Exception ex) {
                    addIssue("initializeTimer:ratio", ex);
                }
            }
        };
        timerTasks.add(onTimeStatsTask);
        timer.scheduleAtFixedRate(onTimeStatsTask, ratioDelayMs, ratioPeriodMs);
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

                value.clearCacheForGroup("");
                value.disposeStorage();
            }
            storages.clear();
        }
        agent.close();
        addEvent(new CacheEvent(this, "onClose", CacheEvent.EVENT_CLOSE_END));
    }

    /** set object in all or one internal caches */
    private List<CacheObject> writeObjectToStorages(CacheObject co) {
        cacheStats.writeObjectToStorages(co);
        var supportedStorages = co.getSupportedStorages();
        return storages.values().stream()
                .filter(st -> supportedStorages.contains(st.getStorageType()))
                .flatMap(storage -> storage.setObject(co).stream())
                .collect(Collectors.toList());
    }

    /** acquire object from external method, this could be slow because if could be a database query of external service
     * we would like to put cache around */
    private <T> T acquireObject(String key, Function<String, T> acquireMethod, CacheMode mode, Set<String> groups) {
        // Measure time of getting this object from cache
        long startActTime = System.currentTimeMillis();
        T objFromMethod = acquireMethod.apply(key);
        long acquireTimeMs = System.currentTimeMillis()-startActTime; // this is time of getting this object from method
        log.debug("===> Got object from external method/supplier, time: " + acquireTimeMs);
        CacheObject co = new CacheObject(key, objFromMethod, acquireTimeMs, acquireMethod, mode, groups);
        policy.checkAndApply(co, cacheStats);
        writeObjectToStorages(co);
        cacheStats.acquireObject(key, acquireTimeMs);
        //Optional<CacheObject> prev = setItem(co);
        //prev.ifPresent(CacheObject::releaseObject);
        return objFromMethod;
    }
    /** set object to cache */
    public CacheSetBack setCacheObject(String key, Object value, CacheMode mode, Set<String> groups) {
        cacheStats.setCacheObject(key);
        log.info("Set cache, key=" + key + ", mode=" + mode.getMode());
        Function<String, Object> acquireMethod = s -> value;
        CacheObject co = new CacheObject(key, value, 0L, acquireMethod, mode, groups);
        List<CacheObject> prevObjects = writeObjectToStorages(co);
        // cacheStats.keyRead(key, );
        return new CacheSetBack(prevObjects, co);
    }

    /** if cache contains given key */
    public boolean contains(String key) {
        return storages.values().stream().anyMatch(x -> x.getObject(key).isPresent());
    }

    /** clear caches with given clear cache */
    public int clearCaches(CacheClearMode clearMode) {
        addEvent(new CacheEvent(this, "clearCaches", CacheEvent.EVENT_CACHE_CLEAN));
        storages.values().stream().forEach(x -> x.clearCache(clearMode));
        return 1;
    }
    public Set<String> getStorageKeys() {
        return storages.keySet();
    }
    /** get all cache keys that contains given string */
    public Set<String> getCacheKeys(String containsStr, boolean includeExternal) {
        return storages.values()
                .stream()
                .filter(x -> x.isInternal() || includeExternal)
                .flatMap(x -> x.getKeys(containsStr).stream())
                .collect(Collectors.toSet());
    }
    /** get all cache keys that contains given string;  this is getting keys ONLY in internal */
    public Set<String> getCacheKeys(String containsStr) {
        return getCacheKeys(containsStr, false);
    }
    /** get values stored in cache
     * this might return only first X values */
    public List<CacheObject> getCacheValues(String containsStr, boolean includeExternal) {
        return storages.values()
                .stream()
                .filter(x -> x.isInternal() || includeExternal)
                .flatMap(x -> x.getValues(containsStr).stream())
                .collect(Collectors.toList());
    }
    public List<CacheObject> getCacheValues(String containsStr) {
        return getCacheValues(containsStr, false);
    }

    /** get list of cache infos for given key */
    public List<CacheObjectInfo> getCacheInfos(String containsStr, boolean includeExternal) {
        return storages.values()
                .stream()
                .filter(x -> x.isInternal() || includeExternal)
                .flatMap(x -> x.getInfos(containsStr).stream())
                .collect(Collectors.toList());
    }
    public List<CacheObjectInfo> getCacheInfos(String containsStr) {
        return getCacheInfos(containsStr, false);
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
        long checkSeq = cacheStats.check();
        addEvent(new CacheEvent(this, "onTimeClean", CacheEvent.EVENT_TIMER_CLEAN));
        storages.values().stream().forEach(x ->  x.timeToClean(checkSeq, lastCleanTime));
        lastCleanTime = System.currentTimeMillis();
    }

    public void onTimeStatsRefresh() {
        addEvent(new CacheEvent(this, "onTimeStatsRefresh", CacheEvent.EVENT_INITIALIZE_TIMER_RATIO));
        cacheStats.refresh();
    }

    /** get item from cache if exists or None */
    public Optional<CacheObject> getCacheObject(String key) {
        cacheStats.getCacheObject(key);
        for (CacheStorageBase storage: storages.values()) {
            long storageReadTimeStart = System.currentTimeMillis();
            Optional<CacheObject> fromCache = storage.getObject(key);
            if (fromCache.isPresent()) {
                try {
                    CacheObject co = fromCache.get();
                    co.use();
                    // TODO: if this is not internal cache - need to increase usage and lastUseDate ???
                    cacheStats.getObjectHit(key, storage, System.currentTimeMillis()-storageReadTimeStart);
                    return Optional.ofNullable(co);
                } catch (Exception ex) {
                    cacheStats.getObjectErrorRead(key, storage);
                    // TODO: log problem with casting value from cache for given key into specific type
                    addIssue("getObject", ex);
                }
            }
        }
        cacheStats.getObjectMiss(key);
        return Optional.empty();
    }
    /** get item from cache if exists
     * returns None if object is not in cache or desired cast if not working */
    public <T> Optional<T> getObject(String key) {
        try {
            return getCacheObject(key).map(co -> (T)co.getValue());
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    /** execute with cache for key
     * if object in cache exists and it is valid, then this object would be returned
     * if not exists then method would be executed to get object, object would be put to cache and returned */
    public <T> T withCache(String key, Supplier<? extends T> m, CacheMode mode, Set<String> groups) {
        try {
            Optional<T> itemFromCache = getObject(key);
            return itemFromCache.orElseGet(() -> acquireObject(key, __ -> m.get(), mode, groups));
        } catch (Exception ex) {
            addIssue("withCache", ex);
            return null;
        }
    }
    public <T> T withCache(String key, Function<String, ? extends T> mapper, CacheMode mode, Set<String> groups) {
        return withCache(key, () -> mapper.apply(key), mode, groups);
    }
    /** method to get registration keys for this agent */
    private DistMessage messageClearCache(String methodName, DistMessage msg) {
        clearCacheContains(msg.getMessage().toString());
        return msg.response("", DistMessageStatus.ok);
    }
    /** method to get registration keys for this agent */
    private DistMessage messageGetStorages(String methodName, DistMessage msg) {
        return msg.response(new StorageInfos(getStoragesInfo()), DistMessageStatus.ok);
    }
    private DistMessage messageGetCacheInfo(String methodName, DistMessage msg) {
        return msg.response(getCacheInfo(), DistMessageStatus.ok);
    }
    private DistMessage messageGetCacheStats(String methodName, DistMessage msg) {
        return msg.response(cacheStats, DistMessageStatus.ok);
    }
    private DistMessage messageGetConfig(String methodName, DistMessage msg) {
        return msg.response(getConfig(), DistMessageStatus.ok);
    }
    private DistMessage messageSetObject(String methodName, DistMessage msg) {
        CacheObjectSerialized cos = (CacheObjectSerialized)msg.getMessage();
        return msg.response(setCacheObject(cos.getKey(), cos), DistMessageStatus.ok);
    }
    private DistMessage messageGetObject(String methodName, DistMessage msg) {
        String key = msg.getMessage().toString();
        return msg.response(getCacheObject(key), DistMessageStatus.ok);
    }
}
