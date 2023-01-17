package com.cache.api;

import com.cache.base.dtos.DistAgentRegisterRow;
import com.cache.utils.DistUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/** Agent registering object that can be used to register Agent to Registration service
 * Registration service is keeping list of Agents, Servers, Services in given storage like DB, Elasticsearch, Redis, Kafka, ... */
public class AgentRegister {
    /** unique ID of this agent */
    private String agentGuid;
    /** secret of this agent */
    private String agentSecret;
    /** host for socket/direct connections */
    private String hostName;
    private String hostIp;
    /** port for socket/direct connections */
    private int port;
    /** create date of this agent */
    private LocalDateTime createDate;
    private LocalDateTime lastPingDate = LocalDateTime.now();
    /** other agents that this agent is already connected to */
    private List<DistAgentRegisterRow> agents;
    private boolean active;

    public AgentRegister() {
    }

    public AgentRegister(String agentGuid, String agentSecret, String hostName, String hostIp, int port,
                         LocalDateTime createDate, List<DistAgentRegisterRow> agents, boolean active) {
        this.agentGuid = agentGuid;
        this.agentSecret = agentSecret;
        this.hostName = hostName;
        this.hostIp = hostIp;
        this.port = port;
        this.createDate = createDate;
        this.agents = agents;
        this.active = active;
    }
    public AgentRegister(String agentGuid, boolean active) {
        this.agentGuid = agentGuid;
        this.agentSecret = "";
        this.hostName = "";
        this.hostIp = "";
        this.port = -1;
        this.createDate = LocalDateTime.now();
        this.agents = List.of();
        this.active = active;
    }
    public DistAgentRegisterRow toAgentRegisterRow() {
        return new DistAgentRegisterRow(createDate, agentGuid, hostName, hostIp, port, lastPingDate, (active)?1:0);
    }

    public void updatePingDate() {
        lastPingDate = LocalDateTime.now();
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
    public LocalDateTime getLastPingDate() {
        return lastPingDate;
    }
    public boolean isActive() {
        return active;
    }
    /** */
    public void deactivate() {
        active = false;
    }
    public int getPort() {
        return port;
    }
    public LocalDateTime getCreateDate() {
        return createDate;
    }
    public List<DistAgentRegisterRow> getAgents() {
        return agents;
    }

}
