package com.cache.interfaces;

import com.cache.api.DistClientType;
import com.cache.api.DistMessage;

/** Interface for client connected to other agent
 * This could be any client based on socket, udp, http, Kafka, JDBC, ...
 * Client could be connected to given Agent, so can send message to that Agent only
 *
 * */
public interface AgentClient {

    /** initialize client - connecting or reconnecting */
    boolean initialize();
    /** close this client */
    void close();
    /** true if client is still working */
    boolean isWorking();
    /** get GUID for this client */
    String getClientGuid();
    /** get type of client - socket, http, datagram, ... */
    DistClientType getClientType();
    /** get unified URL of this client */
    String getUrl();
    /** check if this client has given tag */
    boolean hasTag(String tag);
    /** check if this client has any of tags given */
    boolean hasTags(String[] tags);
    /** send message using this client */
    boolean send(DistMessage msg);
}
