package com.cache.utils;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/** advanced read-only map with special GET features
 * it is for easier get of long, double, int, date, boolean values from properties map */
public class AdvancedMap {

    /** map to advanced functions */
    private Map<String, Object> map;
    public AdvancedMap(Map<String, Object> map) {
        this.map = map;
    }
    public String getString(String key, String defaultValue) {
        return map.getOrDefault(key, defaultValue).toString();
    }
    public String getStringOrEmpty(String key) {
        return getString(key, "");
    }
    public long getLong(String key, long defaultValue) {
        return CacheUtils.parseLong(map.getOrDefault(key, defaultValue).toString(), defaultValue);
    }
    public long getLongOrZero(String key) {
        return getLong(key, 0L);
    }
    public int getInt(String key, int defaultValue) {
        return CacheUtils.parseInt(""+map.getOrDefault(key, defaultValue), defaultValue);
    }
    public int getIntOrZero(String key) {
        return getInt(key, 0);
    }
    public double getDouble(String key, double defaultValue) {
        return CacheUtils.parseDouble(map.getOrDefault(key, defaultValue).toString(), defaultValue);
    }
    public double getDoubleOrZero(String key, double defaultValue) {
        return getDouble(key, 0.0);
    }
    public boolean getBoolean(String key, boolean defaultValue) {
        return defaultValue;
    }
    public java.util.Date getDate(String key, java.util.Date defaultValue) {
        return defaultValue;
    }
    public java.util.Date getDateOrNow(String key) {
        return new java.util.Date();
    }
    /** */
    public Set<String> getWithSplit(String key, String splitChar) {
        return Arrays.stream(getString(key, "").split(splitChar)).collect(Collectors.toSet());
    }

    public static AdvancedMap fromMap(Map<String, Object> map) {
        return new AdvancedMap(map);
    }
}
