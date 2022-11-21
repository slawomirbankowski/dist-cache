package com.cache.api;

import java.time.LocalDateTime;

/** simplified agent information about GUID, host, port and create date */
public class AgentSimplified {
    public String agentGuid;
    public String hostName;
    public String hostIp;
    public int port;
    public LocalDateTime createDate;

    public AgentSimplified() {
        this.agentGuid = null;
        this.hostName = null;
        this.hostIp = null;
        this.port = -1;
        this.createDate = null;
    }
    public AgentSimplified(String agentGuid, String hostName, String hostIp, int port, LocalDateTime createDate) {
        this.agentGuid = agentGuid;
        this.hostName = hostName;
        this.hostIp = hostIp;
        this.port = port;
        this.createDate = createDate;
    }

    public String getAgentGuid() {
        return agentGuid;
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
}
