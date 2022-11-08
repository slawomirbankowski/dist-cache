package com.cache.api;

/**  wrapper over object to implement cacheable item
 * only cacheable items might be added to */
public class CacheableWrapper implements CacheableObject {
    private int priority = 5;
    private int objSize  = 1;
    /** */
    private Object objInCache;
    public CacheableWrapper(Object o) {
        this.objInCache = o;
    }
    public CacheableWrapper(Object o, int pr) {
        this.objInCache = o;
        this.priority = pr;
    }
    /** get size of this item */
    public int getSize() {
        return objSize;
    }
    /** get priority of this item from 1 to 9 */
    public int getPriority() {
        return priority;
    }
    public int getMode() { return CacheMode.MODE_TTL; }

}
