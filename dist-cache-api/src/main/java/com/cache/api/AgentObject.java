package com.cache.api;

import java.time.LocalDateTime;

/** representing management object for agent
 * class is utilized in keeping Agent list in other Agent, that list can be used to communicate with this agent
 * */
public class AgentObject {

    /** create date of this object representing agent */
    public LocalDateTime createDate;
    /** registering object for agent */
    private final AgentRegister register;

    // TODO: add more useful info like last ping time, updated storages, connection network from this agent

    public AgentObject(AgentRegister register) {
        this.register = register;
    }
    /** get GUID for agent */
    public String getAgentGuid() {
        return register.agentGuid;
    }
    /** unregister this agent */
    public void unregister() {
        // TODO: unregister this agent from cache
    }
    /** update existing agent with new information */
    public void update(AgentRegister register) {
        // TODO: update existing agent
    }
    /** get simplified version of agent in cache */
    public AgentSimplified toSimplified() {
        // TODO: get simplified object of agent with only the most important items - add current information of agent
        return register.toSimplified();
    }

}
