package com.cache.agent.impl;

import com.cache.api.DistConfig;
import com.cache.api.DistMessage;
import com.cache.interfaces.Agent;
import com.cache.utils.ResolverManager;

import java.time.LocalDateTime;

/** base class to keep agent - this is for classes that are keeping parent Agent object */
public abstract class Agentable {

    /** date ant time of creation for this server */
    protected final LocalDateTime createDate = LocalDateTime.now();
    /** parent agent for this class */
    protected Agent parentAgent;

    /**  creates new Agentable class with parent Agent */
    public Agentable(Agent parentAgent) {
        this.parentAgent = parentAgent;
    }
    /** get agent */
    public Agent getParentAgent() {
        return parentAgent;
    }
    /** get agent */
    public Agent getAgent() {
        return parentAgent;
    }
    /** get date and time of creation */
    public LocalDateTime getCreateDate() {
        return createDate;
    }

    /** get configuration for parent agent */
    public DistConfig getConfig() {
        return parentAgent.getConfig();
    }
    /** get resolver manager to resolve names, values, properties */
    public ResolverManager getResolverManager() {
        return parentAgent.getConfig().getResolverManager();
    }
    /** method to resolve any parameter, value, setting from raw value with ${key} into final value translated and resolved using all defined Resolvers for this Agent */
    public String resolve(String value) {
        return parentAgent.getConfig().getResolverManager().resolve(value);
    }
    /** get GUID of parent agent */
    public String getParentAgentGuid() {
        return parentAgent.getAgentGuid();
    }
    /** receive message from connector or server and process by parent Agent,
     * need to find service and process that message on service */
    protected void receiveMessageToAgent(DistMessage receivedMsg) {
        parentAgent.getAgentServices().receiveMessage(receivedMsg);
    }

}
