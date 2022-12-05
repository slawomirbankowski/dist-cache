package com.cache.api;

import com.cache.interfaces.CacheSerializer;
import com.cache.utils.AdvancedMap;
import com.cache.utils.CacheUtils;

import java.io.Serializable;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**  serializable version of cache object - contains simple values for all important fields */
public class CacheObjectSerialized implements Serializable {

    private long objectSeq;
    private long createdTimeMs;
    private long lastUseTime;
    private long lastRefreshTime;
    private String key;
    private byte[] objectInCache;
    private String objectClassName;
    private int objSize = 1;
    private long acquireTimeMs;
    private long usages;
    private long refreshes;
    /** mode of keeping item in cache */
    private CacheMode.Mode mode;
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
        this.objectClassName = "";
        this.objSize = 0;
        this.acquireTimeMs = 0;
        this.usages = 0;
        this.refreshes = 0;
        this.mode = CacheMode.Mode.TTL;
        this.timeToLiveMs = 0;
        this.priority = 0;
        this.addToInternal = false;
        this.addToExternal = false;
        this.groups = null;
    }
    public CacheObjectSerialized(long objectSeq, long createdTimeMs, long lastUseTime, long lastRefreshTime, String key,
                                 byte[] objectInCache, String objectClassName,
                                 int objSize, long acquireTimeMs, long usages, long refreshes, CacheMode.Mode mode, long timeToLiveMs, int priority, boolean addToInternal, boolean addToExternal, Set<String> groups) {
        this.objectSeq = objectSeq;
        this.createdTimeMs = createdTimeMs;
        this.lastUseTime = lastUseTime;
        this.lastRefreshTime = lastRefreshTime;
        this.key = key;
        this.objectInCache = objectInCache;
        this.objectClassName = objectClassName;
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
    public String getObjectInCacheAsString() {
        return CacheUtils.bytesToBase64(objectInCache);
    }
    public String getObjectClassName() {
        return objectClassName;
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
    public CacheMode.Mode getMode() {
        return mode;
    }
    public long getTimeToLiveMs() {
        if (mode == CacheMode.Mode.KEEP) {
            return CacheMode.TIME_ONE_YEAR;
        } else {
            return timeToLiveMs;
        }
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
        Object obj = serializer.deserialize(objectInCache);
        Function<String, ?> mta = (key) -> obj;
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

    /** create instance of class from row map */
    public static CacheObjectSerialized fromMap(Map<String, Object> map) {
        AdvancedMap amap = AdvancedMap.fromMap(map);
        byte[] objectInCache = CacheUtils.base64ToString(amap.getString("cachevalue", "")).getBytes();
        CacheObjectSerialized cos = new CacheObjectSerialized(amap.getLong("objectseq", 0L),
                amap.getLong("createdtimems", 0L),
                amap.getLong("lastUseTime", 0L),
                amap.getLong("lastRefreshTime", 0L),
                amap.getString("cachekey", ""),
                objectInCache,
                amap.getString("objclassname", ""),
                amap.getInt("objsize", 1),
                amap.getLong("acquiretimems", 0L),
                amap.getLong("usages", 0L),
                amap.getLong("refreshes", 0L),
                CacheMode.Mode.TTL,
                amap.getLong("timetolivems", 0L),
                amap.getInt("cachepriority", 0), true, false,
                amap.getWithSplit("groupslist", ","));
        return cos;
    }

    public String toString() {
        return "key=" + key + ", seq=" + objectSeq+ ", objSize="+ objSize+ ", acquireTimeMs="+ acquireTimeMs+ ", priority=" +priority + "";
    }
}
