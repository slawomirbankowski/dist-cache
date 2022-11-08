package com.cache.api;

/** cache modes for objects to be kept in cache */
public class CacheMode {

    /** based time-to-live model where object is removed from cache after given milliseconds */
    public static int MODE_TTL = 1;
    /** mode when object is kept in cache till cleared by clear method */
    public static int MODE_KEEP = 2;
    /** mode when object is kept till object is still used for last X milliseconds, when object is
     * not used for much time */
    public static int MODE_USED = 3;

}
