package com.cache.interfaces;

import com.cache.api.DistConfig;

import java.time.LocalDateTime;

/** interface for communication server between agent - this could be as Socket server, UDP server, REST server, or anything for direct communication\
 * direct communication helps to communicate fast without delay */
public interface AgentServer {
    /** get unique ID of this server */
    String getServerGuid();
    /** get configuration for this agent */
    DistConfig getConfig();
    /** returns true if agent has been already closed */
    boolean isClosed();
    /** get date and time of creating this agent */
    LocalDateTime getCreateDate();
    /** close all items in this agent */
    void close();

}
