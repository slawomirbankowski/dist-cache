package com.cache.api;

import java.time.LocalDateTime;

/** event from cache - full format with parent object, method, type and parameters  */
public class CacheEvent {

    /** date and time of event */
    private final LocalDateTime createdDateTime = LocalDateTime.now();
    private final Object parent;
    private final String method;
    private final String eventType;
    private final Object[] params;

    public CacheEvent(Object parent, String method, String eventType, Object... params) {
        this.parent = parent;
        this.method = method;
        this.eventType = eventType;
        this.params = params;
    }
    public CacheEvent(Object parent, String method, String eventType) {
        this.parent = parent;
        this.method = method;
        this.eventType = eventType;
        this.params = new Object[0];
    }
    public Object getParent() {
        return parent;
    }
    public String getMethod() {
        return method;
    }
    public String getEventType() {
        return eventType;
    }
    public Object[] getParams() {
        return params;
    }

    public static CacheEvent startCache() {
        return new CacheEvent(null, "", EVENT_CACHE_START);
    }

    public static String EVENT_INITIALIZE_STORAGES = "EVENT_INITIALIZE_STORAGES";
    public static String EVENT_INITIALIZE_STORAGE = "EVENT_INITIALIZE_STORAGE";
    public static String EVENT_DISPOSE_STORAGE = "EVENT_DISPOSE_STORAGE";
    public static String EVENT_INITIALIZE_AGENT = "EVENT_INITIALIZE_AGENT";
    public static String EVENT_INITIALIZE_POLICIES = "EVENT_INITIALIZE_POLICIES";
    public static String EVENT_INITIALIZE_TIMERS = "EVENT_INITIALIZE_TIMERS";
    public static String EVENT_INITIALIZE_TIMER_CLEAN = "EVENT_INITIALIZE_TIMER_CLEAN";
    public static String EVENT_INITIALIZE_TIMER_COMMUNICATE = "EVENT_INITIALIZE_TIMER_COMMUNICATE";
    public static String EVENT_INITIALIZE_TIMER_RATIO = "EVENT_INITIALIZE_TIMER_RATIO";
    public static String EVENT_TIMER_CLEAN = "EVENT_TIMER_CLEAN";
    public static String EVENT_TIMER_COMMUNICATE = "EVENT_TIMER_COMMUNICATE";
    public static String EVENT_CACHE_START = "EVENT_CACHE_START";
    public static String EVENT_CACHE_CLEAN = "EVENT_CACHE_CLEAN";
    public static String EVENT_CLOSE_BEGIN = "EVENT_CLOSE_BEGIN";
    public static String EVENT_CLOSE_END = "EVENT_CLOSE_END";
}
