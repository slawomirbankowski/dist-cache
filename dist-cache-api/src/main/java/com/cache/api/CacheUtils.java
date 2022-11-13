package com.cache.api;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/** Utilities for dist-cache - different static methods to be used in projects */
public class CacheUtils {

    /** time and date of utils creation - this could be considered as date/time of cache initialization first time */
    private static final LocalDateTime createdDate = LocalDateTime.now();

    /** get creation date of this utils */
    public static LocalDateTime getCreatedDate() {
        return createdDate;
    }

    /** unique global ID of this cache utils */
    private static final String cacheGuid = UUID.randomUUID().toString();
    /** get unique ID of cache */
    public static String getCacheGuid() {
        return cacheGuid;
    }

    public static String getCurrentHostName() {
        try {
            return java.net.InetAddress.getLocalHost().getHostName();
        } catch (Exception ex) {
            return "localhost";
        }
    }
    public static final String hostName = getCurrentHostName();
    private static final AtomicLong configGuidSeq = new AtomicLong();
    public static String generateConfigGuid() {
        return "CONFIG_H" + getCurrentHostName() + "_DT" + getDateTimeYYYYMMDDHHmmss() + "_X" + configGuidSeq.incrementAndGet() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }
    private static final AtomicLong cacheGuidSeq = new AtomicLong();
    public static String generateCacheGuid() {
        return "CACHE_H" + getCurrentHostName() + "_DT" + getDateTimeYYYYMMDDHHmmss() + "_X" + cacheGuidSeq.incrementAndGet() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }
    private static AtomicLong storageGuidSeq = new AtomicLong();
    public static String generateStorageGuid(String className) {
        return "STORAGE_H" + getCurrentHostName() + "_S" + className + "_DT" + getDateTimeYYYYMMDDHHmmss() + "_X" + storageGuidSeq.incrementAndGet() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }
    /** initialized random object to generate random values globally */
    private static final Random rndObj = new Random();

    public static int randomInt(int n) {
        return rndObj.nextInt(n);
    }
    public static long randomLong() {
        return rndObj.nextLong();
    }
    public static double randomDouble() {
        return rndObj.nextDouble();
    }
    private static DateTimeFormatter formatFull = java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    public static String getDateTimeYYYYMMDDHHmmss() {
        return LocalDateTime.now().format(formatFull);
    }


    /** sleep for given time in milliseconds, catch exception */
    public static void sleep(long timeMs) {
        try {
            Thread.sleep(timeMs);
        } catch (Exception ex) {
        }
    }
    /** get object from method and object */
    public static Object getFromMethod(Method method, Object obj) {
        try {
            return method.invoke(obj);
        } catch (Exception ex) {
            return null;
        }
    }

    public static long parseLong(String str, long defaultValue) {
        try {
            return Long.parseLong(str);
        } catch (Exception ex) {
            return defaultValue;
        }
    }
    public static double parseDouble(String str, double defaultValue) {
        try {
            return Double.parseDouble(str);
        } catch (Exception ex) {
            return defaultValue;
        }
    }
    public static int parseInt(String str, int defaultValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception ex) {
            return defaultValue;
        }
    }
    /** calculate estimate size of given object,
     * works only with lists, maps, sets, collections */
    public static int estimateSize(Object obj) {
        try {
            if (obj instanceof List<?>) {
                return ((List)obj).size();
            } else if (obj instanceof Map<?,?>) {
                return ((Map)obj).size();
            } else if (obj instanceof Set<?>) {
                return ((Set)obj).size();
            } else if (obj instanceof Collection<?>) {
                return ((Collection)obj).size();
            } else {
                return 1;
            }
        } catch (Exception ex) {
            return 1;
        }
    }
}
