package com.cache.api;

import java.time.LocalDateTime;
import java.util.List;

/** registering object that can be used */
public class AgentRegister {
    /** unique ID of this agent */
    public String agentGuid;
    /** secret of this agent */
    public String agentSecret;
    /** host for socket/direct connections */
    public String host;
    /** port for socket/direct connections */
    public int port;
    /** create date of this agent */
    public LocalDateTime createDate;
    /** list of storages */
    public List<StorageInfo> storages;
    /** other agents that this agent is already connected to */
    public List<AgentSimplified> agents;

    public AgentRegister() {
    }

    public AgentRegister(String agentGuid, String agentSecret, String host, int port, LocalDateTime createDate, List<StorageInfo> storages, List<AgentSimplified> agents) {
        this.agentGuid = agentGuid;
        this.agentSecret = agentSecret;
        this.host = host;
        this.port = port;
        this.createDate = createDate;
        this.storages = storages;
        this.agents = agents;
    }

    public AgentSimplified toSimplified() {
        // TODO: get simplified object of agent with only the most important items
        return new AgentSimplified(agentGuid, host, port, createDate);
    }

    public String getAgentGuid() {
        return agentGuid;
    }
    public String getAgentSecret() {
        return agentSecret;
    }
    public String getHost() {
        return host;
    }
    public int getPort() {
        return port;
    }
    public LocalDateTime getCreateDate() {
        return createDate;
    }
    public List<StorageInfo> getStorages() {
        return storages;
    }
    public List<AgentSimplified> getAgents() {
        return agents;
    }
}
