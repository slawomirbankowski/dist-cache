package com.cache.api;

import java.time.LocalDateTime;
import java.util.List;

/** Agent registering object that can be used to register Agent to Registration service
 * Registration service is keeping list of Agents, Servers, Services in given storage like DB, Elasticsearch, Redis, Kafka, ... */
public class AgentRegister {
    /** unique ID of this agent */
    public String agentGuid;
    /** secret of this agent */
    public String agentSecret;
    /** host for socket/direct connections */
    public String hostName;
    public String hostIp;
    /** port for socket/direct connections */
    public int port;
    /** create date of this agent */
    public LocalDateTime createDate;

    /** other agents that this agent is already connected to */
    public List<AgentSimplified> agents;

    public AgentRegister() {
    }

    public AgentRegister(String agentGuid, String agentSecret, String hostName, String hostIp, int port,
                         LocalDateTime createDate, List<AgentSimplified> agents) {
        this.agentGuid = agentGuid;
        this.agentSecret = agentSecret;
        this.hostName = hostName;
        this.hostIp = hostIp;
        this.port = port;
        this.createDate = createDate;
        this.agents = agents;
    }

    public AgentSimplified toSimplified() {
        // TODO: get simplified object of agent with only the most important items
        return new AgentSimplified(agentGuid, hostName, hostIp, port, createDate);
    }

    public String getAgentGuid() {
        return agentGuid;
    }
    public String getAgentSecret() {
        return agentSecret;
    }
    public String getHostName() {
        return hostName;
    }
    public String getHostIp() {
        return hostIp;
    }

    public int getPort() {
        return port;
    }
    public LocalDateTime getCreateDate() {
        return createDate;
    }
    public List<AgentSimplified> getAgents() {
        return agents;
    }
}
