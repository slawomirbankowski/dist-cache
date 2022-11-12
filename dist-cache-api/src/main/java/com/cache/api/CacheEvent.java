package com.cache.api;

import java.util.Properties;
import java.util.function.Function;

/** factory to create configuration for cache  */
public class CacheEvent {

    private Object parent;
    private String method;
    private String eventType;
    private Object[] params;

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

    public static String EVENT_CACHE_START = "EVENT_CACHE_START";
    public static String EVENT_CACHE_CLEAN = "EVENT_CACHE_CLEAN";
}
