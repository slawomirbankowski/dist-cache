package com.cache.api;

import java.io.Serializable;

/** message sending as welcome from server to client to gain more information about Agent on the other side s*/
public class AgentWelcomeMessage implements Serializable {

    /** information about Agent */
    private AgentInfo agentInfo;
    /** information about Client */
    private ClientInfo clientInfo;

    public AgentWelcomeMessage(AgentInfo agentInfo, ClientInfo clientInfo) {
        this.agentInfo = agentInfo;
        this.clientInfo = clientInfo;
    }
    public AgentInfo getAgentInfo() {
        return agentInfo;
    }

    public ClientInfo getClientInfo() {
        return clientInfo;
    }
}
