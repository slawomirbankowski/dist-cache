package com.cache.utils;

public class HttpResponseContent {


    private boolean isOk;
    private int code;
    private Object outObject;
    private long outLength;
    private String contentType;
    private String error;
    private long totalTimeMs;

    public HttpResponseContent(boolean isOk, int code, Object outObject, long outLength, String contentType, String error, long totalTimeMs) {
        this.isOk = isOk;
        this.code = code;
        this.outObject = outObject;
        this.outLength = outLength;
        this.contentType = contentType;
        this.error = error;
        this.totalTimeMs = totalTimeMs;
    }

    public boolean isOk() {
        return isOk;
    }

    public int getCode() {
        return code;
    }

    public Object getOutObject() {
        return outObject;
    }

    public long getOutLength() {
        return outLength;
    }

    public String getContentType() {
        return contentType;
    }

    public String getError() {
        return error;
    }

    public long getTotalTimeMs() {
        return totalTimeMs;
    }
    public String getInfo() {
        return "code=" + code + ", time=" + totalTimeMs + ", len=" + outLength;
    }

}
