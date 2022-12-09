package com.cache.api;

import com.cache.interfaces.DistMessage;
import com.cache.utils.CacheUtils;

import java.io.Serializable;

public class DistMessageAdvanced implements DistMessage, Serializable {

    private String messageUid = CacheUtils.generateCacheGuid();
    private String sendFrom;
    private String sendTo;
    private DistServiceType service;
    /** method to execute */
    private String method;
    private Object message;
    private String tags;

    public DistMessageAdvanced(String sendFrom, String sendTo, DistServiceType service, String method, Object message, String tags) {
        this.sendFrom = sendFrom;
        this.sendTo = sendTo;
        this.service = service;
        this.method = method;
        this.message = message;
        this.tags = tags;
    }

    public String getMessageUid() {
        return messageUid;
    }
    public String getSendFrom() {
        return sendFrom;
    }
    public String getSendTo() {
        return sendTo;
    }
    public DistServiceType getService() {
        return service;
    }
    /** get method */
    public String getMethod() {
        return method;
    }
    public Object getMessage() {
        return message;
    }
    public String[] getTagsTab() {
        return tags.split(",");
    }
    public String getTags() {
        return tags;
    }
}
