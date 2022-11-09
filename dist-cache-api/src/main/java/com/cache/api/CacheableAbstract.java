package com.cache.api;

/** abstract class to be put to cache */
public abstract class CacheableAbstract implements CacheableObject {
    /** get size of this item */
    public int getSize() { return 1; }
    public int getPriority() { return CachePriority.PRIORITY_MEDIUM; }
    /** get mode of kept for this object in cache */
    public int getMode() { return CacheMode.MODE_TTL; }
    /** method to release this object */
    public void releaseObject() { }
}
