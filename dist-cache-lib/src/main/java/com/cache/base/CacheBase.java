package com.cache.base;

import com.cache.agent.AgentObject;
import com.cache.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/** manager to connect all storages, policies, agents
 * to perform clean based on time
 * replace all cache with fresh objects
 * */
public abstract class CacheBase implements Cache {

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
    protected Properties cacheProps = null;

    public CacheBase() {
    }
    /** initialize current manager with properties
     * this is creating storages, connecting to storages
     * creating cache policy, create agent and connecting to other cache agents */
    public CacheBase(Properties p) {
        this.cacheProps = p;
        log.info("Creating new cache with GUID: " + cacheManagerGuid);
    }

    /** get unique identifier for this CacheManager object */
    public String getCacheManagerGuid() { return cacheManagerGuid; }
    /** get date and time of creation for this CacheManager */
    public LocalDateTime getCreatedDateTime() { return createdDateTime; }
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
