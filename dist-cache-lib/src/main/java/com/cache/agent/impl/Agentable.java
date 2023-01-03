package com.cache.agent.impl;

import com.cache.api.DistConfig;
import com.cache.api.DistMessage;
import com.cache.interfaces.Agent;

/** base class to keep agent - this is for classes that are keeping parent Agent object */
public abstract class Agentable {

    /** parent agent for this class */
    protected Agent parentAgent;

    /** */
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
    /** get configuration for parent agent */
    public DistConfig getConfig() {
        return parentAgent.getConfig();
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
