package com.cache.interfaces;

/** interface for client connected to other agent - this could be any client based on socket, udp, http, Kafka, JDBC, ... */
public interface AgentClient {

    /** initialize client - connecting or reconnecting */
    boolean initialize();
    /** close this client */
    void close();
    /** true if client is still working */
    boolean isWorking();
    /** get GUID for this client */
    String getClientGuid();

}
