package com.cache.api;

import java.util.List;

/** confirmation of registering agent */
public class AgentConfirmation {
    /** */
    private String agentGuid;
    private boolean isNew;
    /** */
    private boolean isDeleted;
    private int totalAgentsCount;
    /** currently registered agents */
    private List<AgentSimplified> agents;
    public AgentConfirmation(String agentGuid, boolean isNew, boolean isDeleted, int totalAgentsCount, List<AgentSimplified> agents) {
        this.agentGuid = agentGuid;
        this.isNew = isNew;
        this.isDeleted = isDeleted;
        this.totalAgentsCount = totalAgentsCount;
        this.agents = agents;
    }
    public String getAgentGuid() {
        return agentGuid;
    }
}
