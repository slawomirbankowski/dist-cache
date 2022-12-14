package com.cache.utils;

import com.cache.api.AppGlobalInfo;
import com.cache.api.CacheObject;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

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


    public static String getCurrentHostAddress() {
        try {
            return java.net.InetAddress.getLocalHost().getHostAddress();
        } catch (Exception ex) {
            return "localhost";
        }
    }
    public static String getCurrentLocationPath() {
        try {
            return new java.io.File(".").getCanonicalPath();
        } catch (Exception ex) {
            return "localhost";
        }
    }
    public static final String hostName = getCurrentHostName();
    private static final AtomicLong configGuidSeq = new AtomicLong();
    public static String generateConfigGuid() {
        return "CFG_" + hostName + "_DT" + getDateTimeYYYYMMDDHHmmss() + "_X" + configGuidSeq.incrementAndGet() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }
    private static final AtomicLong cacheGuidSeq = new AtomicLong();
    public static String generateCacheGuid() {
        return "CACHE_" + hostName + "_" + UUID.randomUUID().toString().substring(0, 8);
    }
    private static final AtomicLong messageGuidSeq = new AtomicLong();
    public static String generateMessageGuid() {
        return "MSG_" + hostName + "_DT" + getDateTimeYYYYMMDDHHmmss() + "_X" + messageGuidSeq.incrementAndGet() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }
    private static final AtomicLong connectorGuidSeq = new AtomicLong();
    public static String generateConnectorGuid(String connectorClass) {
        return "CONN_" + hostName + "_CL" + connectorClass + "_DT" + getDateTimeYYYYMMDDHHmmss() + "_X" + connectorGuidSeq.incrementAndGet() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }
    private static final AtomicLong storageGuidSeq = new AtomicLong();
    public static String generateStorageGuid(String className) {
        return "ST_" + hostName + "_S" + className + "_DT" + getDateTimeYYYYMMDDHHmmss() + "_X" + storageGuidSeq.incrementAndGet() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }
    public static String generateAgentGuid() {
        return "A_" + hostName + "_" + UUID.randomUUID().toString().substring(0, 8);
    }
    public static String generateServerGuid(String servType) {
        return "SRV_" +  hostName  + "_T" + servType + "_" + UUID.randomUUID().toString().substring(0, 8);
    }

    private static final AtomicLong clientGuidSeq = new AtomicLong();
    public static String generateClientGuid(String clientType) {
        return "CL_" + clientType + "_H" + hostName + "_DT" + getDateTimeYYYYMMDDHHmmss() + "_X" + clientGuidSeq.incrementAndGet() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }
    /** initialized random object to generate random values globally */
    private static final Random rndObj = new Random();

    public static int randomInt(int n) {
        return rndObj.nextInt(n);
    }
    public static int[] randomTable(int s, int n) {
        int[] t = new int[s];
        for (int i=0; i<s; i++) {
            t[i] = randomInt(n);
        }
        return t;
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

    public static String formatDateAsYYYYMMDDHHmmss(LocalDateTime ldt) {
        return ldt.format(formatFull);
    }
    public static String formatDateAsYYYYMMDDHHmmss(java.util.Date date) {
        return dateToLocalDateTime(date).format(formatFull);
    }
    /** convert Date to LocalDateTime*/
    public static LocalDateTime dateToLocalDateTime(java.util.Date date) {
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
    /** convert LocalDateTime to Date */
    public static java.util.Date localDateTimeToDate(LocalDateTime ldt) {
        return new java.util.Date(ldt.toInstant(ZoneOffset.UTC).toEpochMilli());
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

    /** split String into name1=value1;name2=value2;name3=value3 */
    public static List<String[]> splitBySeparationEqual(String str, String splitChar, char equalsChar, boolean removeEmpty) {
        LinkedList<String[]> splitedItems = new LinkedList<>();
        String[] items = str.split(splitChar);
        for (int i=0; i<items.length; i++) {
            String equation = items[i];
            String[] t = splitByChar(equation, equalsChar);
            if (t.length == 2) {
                if (!t[0].isEmpty() && !t[1].isEmpty()) {
                    splitedItems.add(t);
                }
            }
        }
        return splitedItems;
    }
    public static String[] splitByChar(String str, char equalsChar) {
        int pos = str.indexOf(equalsChar);
        if (pos > 0) {
            String firstPart = str.substring(0, pos);
            String secondPart = str.substring(pos+1);
            if (firstPart.isEmpty()) {
                return new String[0];
            } else {
                return new String[] { firstPart, secondPart };
            }
        } else {
            return new String[0];
        }
    }
    public static String bytesToBase64(byte[] bytes) {
        return new String(Base64.getEncoder().encode(bytes));
    }
    public static String stringToBase64(String str) {
        return bytesToBase64(str.getBytes());
    }
    public static String base64ToString(String base64) {
        try {
            return new String(Base64.getDecoder().decode(base64));
        } catch (Exception ex) {
            return "";
        }
    }
    public static String baseToString(String str) {
        return str;
    }
    /** convert String to HEX String */
    public static String stringToHex(String str) {
        return bytesToHex(str.getBytes());
    }
    public static String hexToString(String hex) {
        // TODO: finish implementation
        return hex;
    }
    public static Object[] getValuesForFields(Object obj, Field[] fields) {
        return Arrays.stream(fields).map(f -> {
            try {
                f.setAccessible(true);
                Object value = f.get(obj);
                f.setAccessible(false);
                return value;
            } catch (IllegalAccessException ex) {
                return null;
            }
        }).collect(Collectors.toList()).toArray();
    }

    public static String serializeTable(Object[] o) {
        StringBuilder b = new StringBuilder();
        for (int i=0; i<o.length; i++) {
            b.append(o[i]);
            b.append(" ; ");
        }
        return b.toString();
    }

    public static String serializeBytes(byte[] o) {
        StringBuilder b = new StringBuilder();
        for (int i=0; i<o.length; i++) {
            b.append(o[i]);
            b.append(",");
        }
        return b.toString();
    }
    /** */
    public static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i=0; i<hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
    /** calculate estimate size of given object,
     * works only with lists, maps, sets, collections */
    public static int estimateSize(Object obj) {
        if (obj == null) return 0;
        else if (obj instanceof Collection) return ((Collection) obj).size();
        else if (obj.getClass().isArray()) return Array.getLength(obj);
        else return 1;
    }

    public static int itemCount(CacheObject o) {
        if (o == null) return 0;
        var size = o.getSize();
        if (size != 1) return size;
        return estimateSize(o.getValue());
    }

    /** get global info about this app */
    public static AppGlobalInfo getInfo() {
        var runTime = java.lang.Runtime.getRuntime();
        //Arrays.stream(File.listRoots()).toList();
        return new AppGlobalInfo(getCreatedDate(), getCurrentHostName(), getCurrentHostAddress(), getCurrentLocationPath(),
                java.lang.Thread.activeCount(),
                runTime.freeMemory(), runTime.maxMemory(), runTime.totalMemory(),
                runTime.freeMemory() / MEGABYTE, runTime.maxMemory() / MEGABYTE, runTime.totalMemory() / MEGABYTE);
    }

    public static final long MEGABYTE = 1024 * 1024;
}
