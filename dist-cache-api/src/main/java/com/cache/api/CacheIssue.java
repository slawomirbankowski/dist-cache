package com.cache.api;

/** factory to create configuration for cache  */
public class CacheIssue {

    private Object parent;
    private String methodName;
    private Exception ex;
    private Object[] params;

    public CacheIssue(Object parent, String methodName, Exception ex, Object... params) {
        this.parent = parent;
        this.methodName = methodName;
        this.ex = ex;
        this.params = params;
    }
    public CacheIssue(Object parent, String methodName, Exception ex) {
        this.parent = parent;
        this.methodName = methodName;
        this.ex = ex;
        this.params = new Object[0];
    }


    public static String ISSUE_INTERNAL_EXCEPTION = "ISSUE_INTERNAL_EXCEPTION";
    public static String ISSUE_ALREADY_CLOSED = "ISSUE_ALREADY_CLOSED";


}
