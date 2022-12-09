package com.cache.interfaces;

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
