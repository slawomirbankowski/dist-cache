package com.cache.interfaces;

import com.cache.api.*;

import java.time.LocalDateTime;

/** interfaces for agent in distributed environment
 * this is to communicate among all distributed services */
public interface Agent {
    /** get unique ID of this agent */
    String getAgentGuid();
    /** get configuration for this agent */
    DistConfig getConfig();
    /** get high-level information about this agent */
    AgentInfo getAgentInfo();

    /** returns true if agent has been already closed */
    boolean isClosed();
    /** initialize agent - server, application, jdbc, kafka */
    void initializeAgent();
    /** get date and time of creating this agent */
    LocalDateTime getCreateDate();

    /** get agent threads manager */
    AgentThreads getAgentThreads();
    /** get agent timers manager */
    AgentTimers getAgentTimers();

    /** get agent service manager */
    AgentServices getAgentServices();
    /** get agent connector manager to manage direct connections to other agents, including sending and receiving messages */
    AgentConnectors getAgentConnectors();
    /** get agent registration manager to register this agent in global repositories (different types: JDBC, Kafka, App, Elasticsearch, ... */
    AgentRegistrations getAgentRegistrations();
    /** get agent events manager to add events and set callbacks */
    AgentEvents getAgentEvents();
    /** get agent issuer manager add issues */
    AgentIssues getAgentIssues();
    /** receive message from connector or server, need to find service and process that message on service */
    DistMessageStatus receiveMessage(DistMessage msg);
    /** close all items in this agent */
    void close();

}
