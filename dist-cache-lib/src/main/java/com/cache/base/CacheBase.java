package com.cache.base;

import com.cache.api.*;
import com.cache.encoders.KeyEncoderNone;
import com.cache.interfaces.Cache;
import com.cache.interfaces.CacheKeyEncoder;
import com.cache.interfaces.DistSerializer;
import com.cache.utils.CacheHitRatio;
import com.cache.utils.CacheUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    /** UUID for cache manager - globally unique */
    protected String cacheManagerGuid = CacheUtils.generateCacheGuid();
    /** creation date and time of this cache manager */
    protected LocalDateTime createdDateTime = LocalDateTime.now();
    /** check sequence - this is number of executions of onTime() method */
    protected AtomicLong checkSequence = new AtomicLong();
    /** sequence of added items into this cache */
    protected AtomicLong addedItemsSequence = new AtomicLong();
    /** class to register hit/miss ratio with historical records */
    protected CacheHitRatio hitRatio = new CacheHitRatio();
    /** serializer for serialization of cache objects to external storages */
    protected DistSerializer serializer;
    /** if cache has been already closed */
    protected boolean isClosed = false;
    /** cache properties to initialize all storages, agent, policies, */
    protected DistConfig cacheCfg = null;
    /**default mode for cache objects added without mode */
    protected CacheMode defaultMode = CacheMode.modeTtlTenSeconds;

    /** key encoder to hide passwords and secrets in keys */
    protected CacheKeyEncoder keyEncoder;

    public CacheBase() {
        this(DistConfig.buildEmptyConfig());
    }
    /** initialize current manager with properties
     * this is creating storages, connecting to storages
     * creating cache policy, create agent and connecting to other cache agents */
    public CacheBase(DistConfig cfg) {
        this.cacheCfg = cfg;
        // add all callback functions
        initializeEncoder();
        initializeSerializer();
        log.info("--------> Creating new cache with GUID: " + cacheManagerGuid + ", CONFIG: " + cfg.getConfigGuid() + ", properties: " + cfg.getProperties().size());
    }

    /** get configuration for cache */
    public DistConfig getConfig() {
        return cacheCfg;
    }

    /** get type of service: cache, measure, report, */
    public DistServiceType getServiceType() {
        return DistServiceType.cache;
    }
    /** get unique ID of this service */
    public String getServiceUid() {
        return cacheManagerGuid;
    }

    /** get default serialized for this cache */
    public DistSerializer getCacheSerializer() {
        return serializer;
    }
    /** get value of cache configuration */
    public String getConfigValue(String cfgName) {
        return cacheCfg.getProperty(cfgName);
    }
    /** get unique identifier for this CacheManager object */
    public String getCacheGuid() { return cacheManagerGuid; }
    /** get date and time of creation for this CacheManager */
    public LocalDateTime getCreatedDateTime() { return createdDateTime; }
    /** get item from cache as String if exists or None */
    public String getCacheObjectAsString(String key) {
        Optional<CacheObject> co = getCacheObject(key);
        if (co.isPresent()) {
            return co.get().getValue().toString();
        } else {
            return "";
        }
    }
    /** get info about cache */
    public CacheInfo getCacheInfo() {
        return new CacheInfo(cacheManagerGuid, createdDateTime, checkSequence.get(),
            addedItemsSequence.get(), isClosed,
            getAgent().getIssues().size(), getAgent().getEvents().size(),
            getItemsCount(), getObjectsCount(), getStoragesInfo());
    }
    /** initialize key encoder to encode secrets */
    private void initializeEncoder() {
        // TODO: initialize encoder for secrets and passwords in key
        keyEncoder = new KeyEncoderNone();
    }

    /** initialize serializer used for serialization of an object into byte[] or String to be saved in external storages */
    private void initializeSerializer() {
        String serializerDef = cacheCfg.getProperty(DistConfig.SERIALIZER_DEFINITION, DistConfig.SERIALIZER_DEFINITION_SERIALIZABLE_VALUE);

        //serializer = new ComplexSerializer(serializerDef);
    }

    /** add issue to cache manager to be revoked by parent
     * issue could be Exception, Error, problem with connecting to storage,
     * internal error, not consistent state that is unknown and could be used by parent manager */
    public void addIssue(DistIssue issue) {
        getAgent().addIssue(issue);
    }
    /** add issue with method and exception */
    public void addIssue(String methodName, Exception ex) {
        addIssue(new DistIssue(this, methodName, ex));
    }
    /** add new event and distribute it to callback methods,
     * event could be useful information about change of cache status, new connection, refresh of cache, clean */
    protected void addEvent(CacheEvent event) {
        getAgent().addEvent(event);
    }
    /** set new callback method for events for given type */
    public void setCallback(String eventType, Function<CacheEvent, String> callback) {
        getAgent().setCallback(eventType, callback);
    }
    /** set object to cache */
    public CacheSetBack setCacheObject(String key, Object value, CacheMode mode) {
        return setCacheObject(key, value, mode, Collections.emptySet());
    }
    public CacheSetBack setCacheObject(String key, Object value) {
        return setCacheObject(key, value, CacheMode.modeKeep, Collections.emptySet());
    }
    /** get all recent issues with cache */
    public Queue<DistIssue> getIssues() {
        return getAgent().getIssues();
    }
    /** get all recent events added to cache */
    public Queue<CacheEvent> getEvents() {
        return getAgent().getEvents();
    }

    public <T> T withCache(String key, Supplier<? extends T> supplier) {
        return withCache(key, supplier, defaultMode, Collections.emptySet());
    }
    public <T> T withCache(String key, Supplier<? extends T> supplier, CacheMode mode) {
        return withCache(key, supplier, mode, Collections.emptySet());
    }
    public <T> T withCache(String key, Supplier<? extends T> supplier, Set<String> groups) {
        return withCache(key, supplier, defaultMode, groups);
    }

    public <T> T withCache(String key, Function<String, ? extends T> mapper) {
        return withCache(key, mapper, defaultMode, Collections.emptySet());
    }
    public <T> T withCache(String key, Function<String, ? extends T> mapper, CacheMode mode) {
        return withCache(key, mapper, mode, Collections.emptySet());
    }
    public <T> T withCache(String key, Function<String, ? extends T> mapper, Set<String> groups) {
        return withCache(key, mapper, defaultMode, groups);
    }

    /** check if cache has been already closed and deinitialized */
    public boolean getClosed() { return isClosed; }
    /** close all items in cache */
    protected abstract void onClose();
    /** close and deinitialize cache - remove all items, disconnect from all storages, stop all timers*/
    public void close() {
        isClosed = true;
        log.info("Closing cache for GUID: " + getCacheGuid());
        onClose();
    }
}
