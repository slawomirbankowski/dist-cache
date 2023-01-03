package com.cache.base;

import com.cache.agent.impl.Agentable;
import com.cache.api.DistClientType;
import com.cache.api.DistConfig;
import com.cache.base.dtos.DistAgentServerRow;
import com.cache.interfaces.Agent;
import com.cache.interfaces.AgentServer;
import com.cache.utils.DistUtils;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

/** base class for any server in Agent to accept connections */
public abstract class ServerBase extends Agentable implements AgentServer {

    /** date ant time of creation for this server */
    protected final LocalDateTime createDate = LocalDateTime.now();
    /** GUID of server */
    protected final String serverGuid = DistUtils.generateServerGuid(this.getClass().getSimpleName());
    /** if server has been closed */
    protected boolean closed = false;
    /** sequence for number of received messages */
    protected AtomicLong receivedMessages = new AtomicLong();

    /** creates new base server with set agent */
    public ServerBase(Agent parentAgent) {
        super(parentAgent);
    }
    /** get type of clients to be connected to this server */
    public abstract DistClientType getClientType();
    @Override
    public String getServerGuid() {
        return serverGuid;
    }
    @Override
    public DistConfig getConfig() {
        return parentAgent.getConfig();
    }
    @Override
    public boolean isClosed() {
        return closed;
    }
    @Override
    public LocalDateTime getCreateDate() {
        return createDate;
    }

    /** get port of this server */
    public int getPort() {
        return -1;
    }
    /** get port of this server */
    public String getUrl() {
        return "";
    }
    /** create server row for this server */
    public DistAgentServerRow createServerRow() {
        var createdDate = new java.util.Date();
        var hostName = DistUtils.getCurrentHostName();
        var hostIp = DistUtils.getCurrentHostAddress();
        return new DistAgentServerRow(parentAgent.getAgentGuid(), getServerGuid(), getClientType().name(), hostName, hostIp, getPort(),
                getUrl(), createdDate, 1, createdDate);
    }

}
