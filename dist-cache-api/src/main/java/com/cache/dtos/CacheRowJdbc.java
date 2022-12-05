package com.cache.dtos;

import java.util.Map;


public class CacheRowJdbc {
    public String cachekey;
    public String cachevalue;
    public java.util.Date inserteddate;

    public CacheRowJdbc(String cachekey, String cachevalue, java.util.Date inserteddate) {
        this.cachekey = cachekey;
        this.cachevalue = cachevalue;
        this.inserteddate = inserteddate;
    }
    public CacheRowJdbc(String cachekey, String cachevalue) {
        this.cachekey = cachekey;
        this.cachevalue = cachevalue;
        this.inserteddate = new java.util.Date();
    }
    public static CacheRowJdbc fromMap(Map<String, Object> map) {
        return new CacheRowJdbc(map.getOrDefault("cachekey", "").toString(),
                map.getOrDefault("cachevalue", "").toString());
    }
}
