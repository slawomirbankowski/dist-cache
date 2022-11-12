package com.cache.api;

/** abstract class to be put to cache */
public interface CacheableObject {
    /** get size of this item */
    int getSize();
    int getPriority();
    /** get mode of kept for this object in cache */
    int getMode();
    /** method to release this object */
    void releaseObject();
    Object getObject();
}
