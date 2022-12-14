package com.cache.agent.impl;

import com.cache.agent.AgentInstance;
import com.cache.api.CacheEvent;
import com.cache.api.DistConfig;
import com.cache.interfaces.AgentEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.function.Function;

/** implementation of issues manager */
public class AgentEventsImpl extends Agentable implements AgentEvents {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(AgentEventsImpl.class);
    /** callbacks - methods to be called when given event is happening
     * only one callback per event type is allowed */
    protected java.util.concurrent.ConcurrentHashMap<String, Function<CacheEvent, String>> callbacks = new java.util.concurrent.ConcurrentHashMap<>();
    /** queue of events that would be added to callback methods */
    protected final Queue<CacheEvent> events = new LinkedList<>();

    /** creates new manager for events in agent */
    public AgentEventsImpl(AgentInstance parentAgent) {
        super(parentAgent);
    }

    /** add callback method to call back when there is any event going on */
    public void addCallbackMethods(Map<String, Function<CacheEvent, String>> callbacksMethods) {
        callbacksMethods.entrySet().stream().forEach(cb -> callbacks.put(cb.getKey(), cb.getValue()));
    }

    /** add new event and distribute it to callback methods,
     * event could be useful information about change of cache status, new connection, refresh of cache, clean */
    public void addEvent(CacheEvent event) {
        synchronized (events) {
            events.add(event);
            while (events.size() > parentAgent.getConfig().getPropertyAsLong(DistConfig.CACHE_EVENTS_MAX_COUNT, DistConfig.CACHE_EVENTS_MAX_COUNT_VALUE)) {
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

    /** get all recent events added to cache */
    public Queue<CacheEvent> getEvents() {
        return events;
    }

    /** close */
    public void close() {
        events.clear();
    }

}
