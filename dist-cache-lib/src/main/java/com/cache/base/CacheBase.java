package com.cache.base;

import com.cache.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Supplier;

/** manager to connect all storages, policies, agents
 * to perform clean based on time
 * replace all cache with fresh objects
 * */
public abstract class CacheBase implements Cache {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(CacheBase.class);
    /** UUID for cache manager - globaly unique */
    protected String cacheManagerGuid = CacheUtils.generateCacheGuid();
    /** creation date and time of this cache manager */
    protected LocalDateTime createdDateTime = LocalDateTime.now();
    /** check sequence - this is number of executions of onTime() method */
    protected AtomicLong checkSequence = new AtomicLong();
    /** sequence of added items into this cache */
    protected AtomicLong addedItemsSequence = new AtomicLong();
    /** if cache has been already closed */
    protected boolean isClosed = false;
    /** cache properties to initialize all storages, agent, policies, */
    protected CacheConfig cacheCfg = null;
    protected CacheMode defaultMode = CacheMode.modeTtlTenSeconds;
    /** queue of issues reported when using cache */
    protected final Queue<CacheIssue> issues = new LinkedList<>();
    /** queue of events that would be added to callback methods */
    protected final Queue<CacheEvent> events = new LinkedList<>();
    /** callbacks */
    protected HashMap<String, Function<CacheEvent, String>> callbacks = new HashMap<>();

    public CacheBase() {
        this.cacheCfg = CacheConfig.buildDefaultConfig();
        addEvent(CacheEvent.startCache());

        log.info("Creating new cache with DEFAULT CONFIG and GUID: " + cacheManagerGuid);
    }
    /** initialize current manager with properties
     * this is creating storages, connecting to storages
     * creating cache policy, create agent and connecting to other cache agents */
    public CacheBase(CacheConfig cfg) {
        this.cacheCfg = cfg;
        // add all callback functions
        cacheCfg.getCallbacks().entrySet().stream().forEach(cb -> callbacks.put(cb.getKey(), cb.getValue()));
        log.info("--------> Creating new cache with GUID: " + cacheManagerGuid + ", CONFIG: " + cfg.getConfigGuid() + ", properties: " + cfg.getProperties().size());
    }

    /** get configuration for cache */
    public CacheConfig getCacheConfig() {
        return cacheCfg;
    }
    /** get value of cache configuration */
    public String getConfigValue(String cfgName) {
        return cacheCfg.getProperties().getProperty(cfgName);
    }
    /** get unique identifier for this CacheManager object */
    public String getCacheManagerGuid() { return cacheManagerGuid; }
    /** get date and time of creation for this CacheManager */
    public LocalDateTime getCreatedDateTime() { return createdDateTime; }

    /** add issue to cache manager to be revoked by parent
     * issue could be Exception, Error, problem with connecting to storage,
     * internal error, not consistent state that is unknown and could be used by parent manager */
    protected void addIssue(CacheIssue issue) {
        synchronized (issues) {
            issues.add(issue);
            while (issues.size() > cacheCfg.getPropertyAsLong(CacheConfig.CACHE_ISSUES_MAX_COUNT, CacheConfig.CACHE_ISSUES_MAX_COUNT_VALUE)) {
                issues.poll();
            }
        }
    }
    /** add issue with method and exception */
    protected void addIssue(String methodName, Exception ex) {
        addIssue(new CacheIssue(this, methodName, ex));
    }
    /** add new event and distribute it to callback methods,
     * event could be useful information about change of cache status, new connection, refresh of cache, clean */
    protected void addEvent(CacheEvent event) {
        synchronized (events) {
            events.add(event);
            while (events.size() > cacheCfg.getPropertyAsLong(CacheConfig.CACHE_EVENTS_MAX_COUNT, CacheConfig.CACHE_EVENTS_MAX_COUNT_VALUE)) {
                events.poll();
            }
        }
        Function<CacheEvent, String> callback = callbacks.get(event.getEventType());
        if (callback != null) {
            try {
                callback.apply(event);
            } catch (Exception ex) {
                log.warn("Exception while running callback for event " + event.getEventType());
            }
        }
    }
    /** set new callback method for events for given type */
    public void setCallback(String eventType, Function<CacheEvent, String> callback) {
        log.info("Set callback method for events" + eventType);
        callbacks.put(eventType, callback);
    }

    /** get all recent issues with cache */
    public Queue<CacheIssue> getIssues() {
        return issues;
    }
    /** get all recent events added to cache */
    public Queue<CacheEvent> getEvents() {
        return events;
    }

    public <T> T withCache(String key, CacheableMethod<T> m) {
        return withCache(key, k -> m.get(k), defaultMode);
    }
    public <T> T withCache(String key, Supplier<? extends T> supplier) {
        return withCache(key, supplier, defaultMode);
    }
    public <T> T withCache(String key, Function<String, ? extends T> mapper) {
        return withCache(key, mapper, defaultMode);
    }
    public <T> T withCache(String key, Method method, Object obj) {
        return withCache(key, method, obj, defaultMode);
    }

    /** check if cache has been already closed and deinitialized */
    public boolean getClosed() { return isClosed; }
    /** close all items in cache */
    protected abstract void onClose();
    /** close and deinitialize cache - remove all items, disconnect from all storages, stop all timers*/
    public void close() {
        isClosed = true;
        log.info("Closing cache for GUID: " + getCacheManagerGuid());
        onClose();
    }
}
