package com.cache.interfaces;

import com.cache.api.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;

/** interfaces for agent in distributed environment
 * this is to communicate among all distributed services */
public interface Agent {
    /** get unique ID of this agent */
    String getAgentGuid();
    /** get configuration for this agent */
    DistConfig getConfig();
    /** returns true if agent has been already closed */
    boolean isClosed();

    /** get list of connected agents */
    List<AgentSimplified> getAgents();
    /** get date and time of creating this agent */
    LocalDateTime getCreateDate();

    /** add issue to cache manager to be revoked by parent
     * issue could be Exception, Error, problem with connecting to storage,
     * internal error, not consistent state that is unknown and could be used by parent manager */
    void addIssue(CacheIssue issue);
    /** add issue with method and exception */
    public void addIssue(String methodName, Exception ex);
    /** add new event and distribute it to callback methods,
     * event could be useful information about change of cache status, new connection, refresh of cache, clean */
    void addEvent(CacheEvent event);
    /** set new callback method for events for given type */
    void setCallback(String eventType, Function<CacheEvent, String> callback);
    /** get all recent issues with cache */
    public Queue<CacheIssue> getIssues();
    /** get all recent events added to cache */
    public Queue<CacheEvent> getEvents();

    /** return all services assigned to this agent */
    List<DistService> getServices();

    /** register service to this agent */
    void registerService(DistService service);

    /** close all items in this agent */
    void close();

}
