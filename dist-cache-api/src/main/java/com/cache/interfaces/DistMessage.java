package com.cache.interfaces;

import com.cache.api.DistServiceType;

public interface DistMessage {

    /** unique ID of this message */
    public String getMessageUid();
    /** */
    public String getSendFrom();
    public String getSendTo();
    public DistServiceType getService();
    /** get method */
    public String getMethod();
    public Object getMessage();
    public String[] getTagsTab();
    public String getTags();
}
