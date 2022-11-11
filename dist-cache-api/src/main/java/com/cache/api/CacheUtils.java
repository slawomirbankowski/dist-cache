package com.cache.api;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

public class CacheUtils {

    /** */
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
    public static void sleep(long timeMs) {
        try {
            Thread.sleep(10L);
        } catch (Exception ex) {
        }
    }

}
