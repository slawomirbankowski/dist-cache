package com.cache.api;

import java.io.Serializable;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**  serializable version of cache object - contains simple values for all important fields */
public class CacheObjectSerialized implements Serializable {

    private long objectSeq;
    private long createdTimeMs;
    private long lastUseTime;
    private long lastRefreshTime;
    private String key;
    private byte[] objectInCache;
    private int objSize = 1;
    private long acquireTimeMs;
    private long usages;
    private long refreshes;
    /** mode of keeping item in cache */
    private int mode;
    private long timeToLiveMs;
    private int priority;
    private boolean addToInternal;
    private boolean addToExternal;
    private Set<String> groups;

    /** empty constructor for reflection */
    public CacheObjectSerialized() {
        this.objectSeq = 1;
        this.createdTimeMs = 0;
        this.lastUseTime = 0;
        this.lastRefreshTime = 0;
        this.key = "";
        this.objectInCache = null;
        this.objSize = 0;
        this.acquireTimeMs = 0;
        this.usages = 0;
        this.refreshes = 0;
        this.mode = 0;
        this.timeToLiveMs = 0;
        this.priority = 0;
        this.addToInternal = false;
        this.addToExternal = false;
        this.groups = null;
    }
    public CacheObjectSerialized(long objectSeq, long createdTimeMs, long lastUseTime, long lastRefreshTime, String key, byte[] objectInCache, int objSize, long acquireTimeMs, long usages, long refreshes, int mode, long timeToLiveMs, int priority, boolean addToInternal, boolean addToExternal, Set<String> groups) {
        this.objectSeq = objectSeq;
        this.createdTimeMs = createdTimeMs;
        this.lastUseTime = lastUseTime;
        this.lastRefreshTime = lastRefreshTime;
        this.key = key;
        this.objectInCache = objectInCache;
        this.objSize = objSize;
        this.acquireTimeMs = acquireTimeMs;
        this.usages = usages;
        this.refreshes = refreshes;
        this.mode = mode;
        this.timeToLiveMs = timeToLiveMs;
        this.priority = priority;
        this.addToInternal = addToInternal;
        this.addToExternal = addToExternal;
        this.groups = groups;
    }

    public long getObjectSeq() {
        return objectSeq;
    }
    public long getCreatedTimeMs() {
        return createdTimeMs;
    }
    public long getLastUseTime() {
        return lastUseTime;
    }
    public long getLastRefreshTime() {
        return lastRefreshTime;
    }
    public String getKey() {
        return key;
    }
    public byte[] getObjectInCache() {
        return objectInCache;
    }
    public int getObjSize() {
        return objSize;
    }
    public long getAcquireTimeMs() {
        return acquireTimeMs;
    }
    public long getUsages() {
        return usages;
    }
    public long getRefreshes() {
        return refreshes;
    }
    public int getMode() {
        return mode;
    }
    public long getTimeToLiveMs() {
        return timeToLiveMs;
    }
    public int getPriority() {
        return priority;
    }
    public boolean isAddToInternal() {
        return addToInternal;
    }
    public boolean isAddToExternal() {
        return addToExternal;
    }
    public Set<String> getGroups() {
        return groups;
    }

    public CacheObject toCacheObject(CacheSerializer serializer) {
        Object obj = "";
        CacheableMethod mta = new CacheableMethod() {
            @Override
            public Object get(String key) {
                return obj;
            }
        };
        CacheMode cm = new CacheMode(mode, timeToLiveMs, addToInternal, addToExternal, priority);
        return new CacheObject(objectSeq, createdTimeMs, lastUseTime, lastRefreshTime, key,
                obj, mta, objSize, acquireTimeMs, usages, refreshes, cm, groups);
    }
    /** get map with current values */
    public Map<String, String> getSerializedMap(CacheSerializer serializer) {
        HashMap<String, String> map = new HashMap<>();
        map.put("objectSeq", ""+objectSeq);
        map.put("createdTimeMs", ""+createdTimeMs);
        map.put("lastUseTime", ""+lastUseTime);
        map.put("lastRefreshTime", ""+lastRefreshTime);
        map.put("key", key);
        map.put("objectInCache", new String(Base64.getEncoder().encode(objectInCache)));
        map.put("objSize", ""+objSize);
        map.put("acquireTimeMs", ""+acquireTimeMs);
        map.put("usages", ""+usages);
        map.put("refreshes", ""+refreshes);
        map.put("mode", ""+mode);
        map.put("timeToLiveMs", ""+timeToLiveMs);
        map.put("priority", ""+priority);
        map.put("addToInternal", ""+addToInternal);
        map.put("addToExternal", ""+addToExternal);
        map.put("groups", groups.stream().reduce((x,y) -> x + ";;;" + y).orElse(""));
        return map;
    }


}
