package com.cache.api;

/** internal issue in Cache - this is full version of object */
public class CacheIssue {

    private final Object parent;
    private final String methodName;
    private final Exception ex;
    private final Object[] params;

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

    public Object getParent() {
        return parent;
    }

    public String getMethodName() {
        return methodName;
    }

    public Exception getEx() {
        return ex;
    }

    public Object[] getParams() {
        return params;
    }

    public static String ISSUE_INTERNAL_EXCEPTION = "ISSUE_INTERNAL_EXCEPTION";
    public static String ISSUE_ALREADY_CLOSED = "ISSUE_ALREADY_CLOSED";


}
