package com.cache.api;

/**  wrapper over object to implement cacheable item
 * only cacheable items might be added to */
public class CacheableWrapper implements CacheableObject {
    /** get size of this item */
    public int getSize() {
        return 1;
    }
    /** get priority of this item from 1 to 9 */
    public int getPriority() {
        return 5;
    }
    public int getMode() { return CacheMode.MODE_TTL; }
}
