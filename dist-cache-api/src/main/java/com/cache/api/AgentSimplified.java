package com.cache.api;

import java.time.LocalDateTime;

/** simplified agent information about GUID, host, port and create date */
public class AgentSimplified {
    public String agentGuid;
    public String host;
    public int port;
    public LocalDateTime createDate;

    public AgentSimplified() {
        this.agentGuid = null;
        this.host = null;
        this.port = -1;
        this.createDate = null;
    }
    public AgentSimplified(String agentGuid, String host, int port, LocalDateTime createDate) {
        this.agentGuid = agentGuid;
        this.host = host;
        this.port = port;
        this.createDate = createDate;
    }

    public String getAgentGuid() {
        return agentGuid;
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
}
