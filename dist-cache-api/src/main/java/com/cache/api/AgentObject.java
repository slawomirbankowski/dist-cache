package com.cache.api;

import java.time.LocalDateTime;

/** representing management object for agent */
public class AgentObject {

    /** create date of this object representing agent */
    public LocalDateTime createDate;
    /** registering object for agent */
    private final AgentRegister register;

    public AgentObject(AgentRegister register) {
        this.register = register;
    }
    /** get GUID for agent */
    public String getAgentGuid() {
        return register.agentGuid;
    }
    /** unregister this agent */
    public void unregister() {

    }
    /** update existing agent with new information */
    public void update(AgentRegister register) {
        // TODO: update existing agent
    }
    /** */
    public AgentSimplified toSimplified() {
        // TODO: get simplified object of agent with only the most important items
        return register.toSimplified();
    }

}
