package com.cache.dtos;

import java.util.Map;

public class DistCacheAgentRow {

    public String cachekey;
    public String cachevalue;
    public java.util.Date inserteddate;

    public DistCacheAgentRow(String cachekey, String cachevalue, java.util.Date inserteddate) {
        this.cachekey = cachekey;
        this.cachevalue = cachevalue;
        this.inserteddate = inserteddate;
    }
    public DistCacheAgentRow(String cachekey, String cachevalue) {
        this.cachekey = cachekey;
        this.cachevalue = cachevalue;
        this.inserteddate = new java.util.Date();
    }
    public static DistCacheAgentRow fromMap(Map<String, Object> map) {
        return new DistCacheAgentRow(map.getOrDefault("cachekey", "").toString(),
                map.getOrDefault("cachevalue", "").toString());
    }
}
