package com.cache.agent;

import com.cache.agent.impl.*;
import com.cache.api.*;
import com.cache.interfaces.*;
import com.cache.utils.CacheUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

/** agent class to be connected to dist-cache applications, Kafka, Elasticsearch or other global agent repository
 * Agent is also connecting directly to other agents */
public class AgentInstance implements Agent, DistService {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(AgentInstance.class);
    /** date ant time of creation */
    private final LocalDateTime createDate = LocalDateTime.now();
    /** configuration for agent */
    private final DistConfig config;
    /** if agent has been closed */
    private boolean closed = false;
    /** generate secret of this agent to be able to put commands */
    private final String agentSecret = UUID.randomUUID().toString();
    /** GUID of agent */
    private final String agentGuid = CacheUtils.generateAgentGuid();

    /** manager for threads in Agent system */
    private final AgentThreads agentThreads = new AgentThreadsImpl(this);
    /** manager for timers in Agent system */
    private final AgentTimers agentTimers = new AgentTimersImpl(this);
    /** manager for registrations */
    private final AgentRegistrations agentRegistrations = new AgentRegistrationsImpl(this);
    /** manager for services registered in agent  */
    private final AgentServices agentServices = new AgentServicesImpl(this);
    /** manager for agent connections to other agents */
    private final AgentConnectors agentConnectors = new AgentConnectorsImpl(this);
    /** manager for registrations */
    private final AgentEvents agentEvents = new AgentEventsImpl(this);
    /** manager for registrations */
    private final AgentIssues agentIssues = new AgentIssuesImpl(this);

    /** create new agent */
    public AgentInstance(DistConfig config, Map<String, Function<CacheEvent, String>> callbacksMethods) {
        this.config = config;
        // self register of agent as service
        agentServices.registerService(this);
        agentEvents.addCallbackMethods(callbacksMethods);
    }

    /** get type of service: cache, measure, report, flow, space, ... */
    public DistServiceType getServiceType() {
        return DistServiceType.agent;
    }
    /** get unique ID of this service */
    public String getServiceUid() {
        return agentGuid;
    }
    /** initialize agent - server, application, jdbc, kafka */
    public void initializeAgent() {
        log.info("Initializing agent for guid: " + agentGuid);
        agentRegistrations.createRegistrations();
        agentConnectors.openServers();
    }

    /** get configuration for this agent */
    public DistConfig getConfig() { return config; }
    /** get unique ID of this agent */
    public String getAgentGuid() { return agentGuid; }
    /** get date and time of creating this agent */
    public LocalDateTime getCreateDate() {
        return createDate;
    }
    /** get secret generated or set for this agent */
    public String getAgentSecret() {
        return agentSecret;
    }

    /** get agent threads manager */
    public AgentThreads getAgentThreads() {
        return agentThreads;
    }
    /** get agent timers manager */
    public AgentTimers getAgentTimers() {
        return agentTimers;
    }

    /** get agent service manager */
    public AgentServices getAgentServices() {
        return agentServices;
    }
    /** get agent connector manager to manage direct connections to other agents, including sending and receiving messages */
    public AgentConnectors getAgentConnectors() {
        return agentConnectors;
    }
    /** get agent registration manager to register this agent in global repositories (different types: JDBC, Kafka, App, Elasticsearch, ... */
    public AgentRegistrations getAgentRegistrations() {
        return agentRegistrations;
    }
    /** get agent events manager to add events and set callbacks */
    public AgentEvents getAgentEvents() {
        return agentEvents;
    }
    /** get agent issue manager for adding issues */
    public AgentIssues getAgentIssues() {
        return agentIssues;
    }

    /** returns true if agent has been already closed */
    public boolean isClosed() {
        return closed;
    }

    /** close all items in this agent */
    public void close() {
        log.info("Closing agent: " + agentGuid);
        closed = true;
        // TODO: close all items for this agent - unregister in application, notify all agents
        agentThreads.close();
        agentTimers.close();
        agentServices.close();
        agentConnectors.close();
        agentRegistrations.close();
        agentEvents.close();
        agentIssues.close();
    }

    /** receive message from connector or server, need to find service and process that message on service */
    public DistMessageStatus receiveMessage(DistMessage msg) {
        return agentServices.receiveMessage(msg);
    }

    /** process message by this agent service, choose method and , returns status */
    public DistMessageStatus processMessage(DistMessage msg) {
        // TODO: process message in this agent, there could be many methods to process system agent messages

        return new DistMessageStatus();
    }
    /** message send to agents, directed to services, selected method */
    public DistMessageStatus sendMessage(DistMessage msg) {
        // TODO: check destination by agent and tags
        return new DistMessageStatus();
    }
    /** create broadcast message send to all clients */
    public DistMessage createMessageBroadcast(DistServiceType service, String method, Object message) {
        return new DistMessageAdvanced(agentGuid, "*", service, method, message, "");
    }
    /** */
    public DistMessage createMessage(String sendTo, DistServiceType service, String method, Object message) {
        return new DistMessageAdvanced(agentGuid, sendTo, service, method, message, "");
    }
    /** */
    public DistMessage createMessage(String sendTo, DistServiceType service, String method, Object message, String tags) {
        DistMessageAdvanced dist = new DistMessageAdvanced(agentGuid, sendTo, service, method, message, tags);
        dist.getService();
        return dist;
    }


}
