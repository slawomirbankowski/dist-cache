package com.cache.api;

/** cache modes for objects to be kept in cache */
public class CacheMode {

    /** mode of keeping item in cache */
    private final int mode;
    /** time to live milliseconds */
    private final long timeToLiveMs;
    private final int priority;
    private final boolean addToInternal;
    private final boolean addToExternal;

    public CacheMode(int m, long timeToLiveMs, boolean addToInternal, boolean addToExternal, int priority) {
        this.mode = m;
        this.timeToLiveMs = timeToLiveMs;
        this.addToInternal = addToInternal;
        this.addToExternal = addToExternal;
        this.priority = priority;
    }
    public CacheMode(int m, long timeToLiveMs, boolean addToInternal, boolean addToExternal) {
        this.mode = m;
        this.timeToLiveMs = timeToLiveMs;
        this.addToInternal = addToInternal;
        this.addToExternal = addToExternal;
        this.priority = CachePriority.PRIORITY_MEDIUM;
    }
    public CacheMode(int m, long timeToLiveMs) {
        this.mode = m;
        this.timeToLiveMs = timeToLiveMs;
        this.addToInternal = true;
        this.addToExternal = false;
        this.priority = CachePriority.PRIORITY_MEDIUM;
    }
    public CacheMode(int cacheMode) {
        this(cacheMode, TIME_FOREVER, true, false);
    }

    public long getTimeToLiveMs() { return timeToLiveMs; }

    public int getMode() { return mode; }
    /** get priority for this mode
     * priorities are defined in CachePriority class
     * default priority is PRIORITY_MEDIUM
     * */
    public int getPriority() { return priority; }
    public boolean isTtl() { return mode == MODE_TTL; }
    public boolean isRefresh() { return mode == MODE_KEEP; }
    public boolean isKeep() { return mode == MODE_REFRESH; }
    public boolean isAddToInternal() {
        return addToInternal;
    }
    public boolean isAddToExternal() {
        return addToExternal;
    }

    /** based time-to-live model where object is removed from cache after given milliseconds */
    public static int MODE_TTL = 1;
    /** mode when object is kept in cache till cleared by clear method */
    public static int MODE_KEEP = 2;
    /** mode when object is kept till object is still used for last X milliseconds, when object is
     * not used for much time */
    public static int MODE_USED = 3;
    /** refresh mode when item could be refreshed every N seconds through refreshing handler */
    public static int MODE_REFRESH = 4;
    /** object in cache is kept until there is too many objects in cache,
     * after that objects with the lowest priority would be removed  */
    public static int MODE_PRIORITY = 3;
    public static long TIME_THREE_SECONDS = 3 * 1000L;
    public static long TIME_FIVE_SECONDS = 5 * 1000L;
    public static long TIME_TEN_SECONDS = 10 * 1000L;
    public static long TIME_TWENTY_SECONDS = 20 * 1000L;
    public static long TIME_THIRTY_SECONDS = 30 * 1000L;
    public static long TIME_ONE_MINUTE = 60 * 1000L;
    public static long TIME_FIVE_MINUTES = 5 * 60 * 1000L;
    public static long TIME_TEN_MINUTES = 10 * 60 * 1000L;
    public static long TIME_TWENTY_MINUTES = 20 * 60 * 1000L;
    public static long TIME_THIRTY_MINUTES = 30 * 60 * 1000L;
    public static long TIME_ONE_HOUR = 3600 * 1000L;
    public static long TIME_TWO_HOURS = 2 * 3600 * 1000L;
    public static long TIME_THREE_HOURS = 3 * 3600 * 1000L;
    public static long TIME_SIX_HOURS = 6 * 3600 * 1000L;
    public static long TIME_ONE_DAY = 24 * 3600 * 1000L;
    public static long TIME_ONE_WEEK = 7 * 24 * 3600 * 1000L;
    public static long TIME_TWO_WEEKS = 14 * 24 * 3600 * 1000L;
    public static long TIME_FOUR_WEEKS = 28 * 24 * 3600 * 1000L;
    public static long TIME_FOREVER = Long.MAX_VALUE;

    public static CacheMode modeTtlTenSeconds = new CacheMode(MODE_TTL, TIME_TEN_SECONDS);
    public static CacheMode modeTtlTwentySeconds = new CacheMode(MODE_TTL, TIME_TWENTY_SECONDS);
    public static CacheMode modeTtlThirtySeconds = new CacheMode(MODE_TTL, TIME_THIRTY_SECONDS);
    public static CacheMode modeTtlOneMinute = new CacheMode(MODE_TTL, TIME_ONE_MINUTE);
    public static CacheMode modeTtlFiveMinutes = new CacheMode(MODE_TTL, TIME_FIVE_MINUTES);
    public static CacheMode modeTtlTenMinutes = new CacheMode(MODE_TTL, TIME_TEN_MINUTES);
    public static CacheMode modeTtlTwentyMinutes = new CacheMode(MODE_TTL, TIME_TWENTY_MINUTES);
    public static CacheMode modeTtlThirtyMinutes = new CacheMode(MODE_TTL, TIME_THIRTY_MINUTES);
    public static CacheMode modeTtlOneHour = new CacheMode(MODE_TTL, TIME_ONE_HOUR);
    public static CacheMode modeTtlTwoHours = new CacheMode(MODE_TTL, TIME_TWO_HOURS);
    public static CacheMode modeTtlThreeHours = new CacheMode(MODE_TTL, TIME_THREE_HOURS);
    public static CacheMode modeTtlSixHours = new CacheMode(MODE_TTL, TIME_SIX_HOURS);
    public static CacheMode modeTtlOneDay = new CacheMode(MODE_TTL, TIME_ONE_DAY);
    public static CacheMode modeTtlOneWeek = new CacheMode(MODE_TTL, TIME_ONE_WEEK);
    public static CacheMode modeTtlTwoWeeks = new CacheMode(MODE_TTL, TIME_TWO_WEEKS);
    public static CacheMode modeTtlFourWeeks = new CacheMode(MODE_TTL, TIME_FOUR_WEEKS);

    public static CacheMode modeKeep = new CacheMode(MODE_KEEP, TIME_FOREVER);
    public static CacheMode modeInternalOnly = new CacheMode(MODE_KEEP, TIME_FOREVER);
    public static CacheMode modeRefreshTenSeconds = new CacheMode(MODE_REFRESH, TIME_TEN_SECONDS);
    public static CacheMode modeRefreshOneMinute = new CacheMode(MODE_REFRESH, TIME_ONE_MINUTE);
    public static CacheMode modeRefreshOneHour = new CacheMode(MODE_REFRESH, TIME_ONE_HOUR);
    public static CacheMode modeRefreshSixHours = new CacheMode(MODE_REFRESH, TIME_SIX_HOURS);
}
