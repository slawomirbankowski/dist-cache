package com.cache.utils;

/** response of HTTP(s) call - in case of OK or ERROR */
public class HttpResponseContent {

    /** true when call returned 2xx, false otherwise - if call returned 3xx or 4xx or 5xx or there is no connection or timeout */
    private boolean isOk;
    /** final HTTP code returned OR -1 if there is no connection */
    private int code;
    /** read object from call - this is the response body */
    private Object outObject;
    /** lenght of output object */
    private long outLength;
    private String contentType;
    private String error;
    /** total request-response time in milliseconds */
    private long totalTimeMs;

    /** creates new HTTP response content */
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
