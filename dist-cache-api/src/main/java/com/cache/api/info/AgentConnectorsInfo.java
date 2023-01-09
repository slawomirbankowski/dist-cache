package com.cache.api.info;

import com.cache.base.dtos.DistAgentServerRow;

import java.util.List;

/** information class about connectors in Agent: servers and clients */
public class AgentConnectorsInfo {
    private List<AgentServerInfo> createdServers;
    private List<DistAgentServerRow> serverDefinitions;
    private List<ClientInfo> clientInfos;
    public AgentConnectorsInfo(List<AgentServerInfo> createdServers, List<DistAgentServerRow> serverDefinitions, List<ClientInfo> clientInfos) {
        this.createdServers = createdServers;
        this.serverDefinitions = serverDefinitions;
        this.clientInfos = clientInfos;
    }
    public List<AgentServerInfo> getCreatedServers() {
        return createdServers;
    }
    public List<DistAgentServerRow> getServerDefinitions() {
        return serverDefinitions;
    }
    public List<ClientInfo> getClientInfos() {
        return clientInfos;
    }
}

