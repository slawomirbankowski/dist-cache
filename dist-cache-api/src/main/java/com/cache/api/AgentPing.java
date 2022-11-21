package com.cache.api;

/** ping object */
public class AgentPing {
    /** unique ID of this agent */
    public String agentGuid;

    // TODO: expand this object with other useful information about agent itself that can be used for analyzing status of that agent


    public AgentPing() {
    }

    public AgentPing(String agentGuid) {
        this.agentGuid = agentGuid;
    }


}
