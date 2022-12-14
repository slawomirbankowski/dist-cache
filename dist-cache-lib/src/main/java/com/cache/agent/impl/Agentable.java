package com.cache.agent.impl;

import com.cache.agent.AgentInstance;
import com.cache.api.DistConfig;

/** base class to keep agent */
public abstract class Agentable {
    /** parent agent for this issues manager */
    protected AgentInstance parentAgent;

    /** */
    public Agentable(AgentInstance parentAgent) {
        this.parentAgent = parentAgent;
    }
    /** */
    public AgentInstance getParentAgent() {
        return parentAgent;
    }
    /** get configuration for parent agent */
    public DistConfig getConfig() {
        return parentAgent.getConfig();
    }

}
