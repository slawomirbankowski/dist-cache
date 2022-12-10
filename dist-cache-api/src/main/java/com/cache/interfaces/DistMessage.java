package com.cache.interfaces;

import com.cache.api.DistServiceType;

/** interface for message sent by dist service to another service between agent
 * message should be serialized and sent using known servers-clients like Socket, Datagram, Kafka, HTTP, ...
 *  */
public interface DistMessage {

    /** unique ID of this message */
    String getMessageUid();
    /** get UID of agent that send this message */
    String getSendFrom();
    /** */
    String getSendTo();
    /** get destination service dedicated for this message */
    public DistServiceType getService();
    /** get method */
    String getMethod();
    /** get message as Object */
    Object getMessage();
    /** get tags for this message */
    String[] getTagsTab();
    /** get comma-separated list of tags for this message */
    String getTags();
}
