package com.cache.agent.impl;

import com.cache.agent.AgentInstance;
import com.cache.api.DistConfig;
import com.cache.api.DistMessageFull;
import com.cache.interfaces.Agent;

/** base class to keep agent - this is for classes that are keeping parent Agent object */
public abstract class Agentable {

    /** parent agent for this issues manager */
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

}
