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
    /** get serializer/deserializer helper to serialize/deserialize objects when sending through connectors or saving to external storages */
    DistSerializer getSerializer();
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


    /** create new message builder starting this agent */
    DistMessageBuilder createMessageBuilder();
    /** message send to agent(s) */
    DistMessageFull sendMessage(DistMessageFull msgCallbacks);
    /** send message to agents */
    DistMessageFull sendMessage(DistMessage msg, DistCallbacks callbacks);
    /** create message send to all clients */
    DistMessageFull sendMessageBroadcast(DistMessageType messageType, DistServiceType fromService, DistServiceType toService, String method, Object message, String tags, LocalDateTime validTill, DistCallbacks callbacks);
    DistMessageFull sendMessageBroadcast(DistMessageType messageType, DistServiceType fromService, DistServiceType toService, String method, Object message, String tags, DistCallbacks callbacks);
    DistMessageFull sendMessageBroadcast(DistMessageType messageType, DistServiceType fromService, DistServiceType toService, String method, Object message, DistCallbacks callbacks);
    DistMessageFull sendMessageBroadcast(DistServiceType fromService, DistServiceType toService, String method, Object message, DistCallbacks callbacks);
    DistMessageFull sendMessage(DistService fromService, String toAgent, DistServiceType toService, String method, Object message, DistCallbacks callbacks);
    DistMessageFull sendMessageAny(DistService fromService, DistServiceType toService, String method, Object message, DistCallbacks callbacks);

    /** close all items in this agent */
    void close();

}
