package com.cache.base;

import com.cache.agent.impl.Agentable;
import com.cache.api.enums.DistComponentType;
import com.cache.api.info.AgentServerInfo;
import com.cache.api.enums.DistClientType;
import com.cache.api.DistConfig;
import com.cache.base.dtos.DistAgentServerRow;
import com.cache.interfaces.Agent;
import com.cache.interfaces.AgentComponent;
import com.cache.interfaces.AgentServer;
import com.cache.utils.DistUtils;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

/** base class for any server in Agent to accept connections */
public abstract class ServerBase extends Agentable implements AgentServer, AgentComponent {

    /** GUID of server */
    protected final String serverGuid = DistUtils.generateServerGuid(this.getClass().getSimpleName());
    /** if server has been closed */
    protected boolean closed = false;
    /** sequence for number of received messages */
    protected AtomicLong receivedMessages = new AtomicLong();

    /** creates new base server with set agent */
    public ServerBase(Agent parentAgent) {
        super(parentAgent);
        parentAgent.addComponent(this);
    }

    /** initialize this server */
    public abstract void initialize();
    /** get type of clients to be connected to this server */
    public abstract DistClientType getClientType();
    /** get type of this component */
    public DistComponentType getComponentType() {
        return DistComponentType.server;
    }
    @Override
    public String getServerGuid() {
        return serverGuid;
    }
    public String getGuid() {
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

    /** get information about this server */
    public AgentServerInfo getInfo() {
        return new AgentServerInfo(getClientType(), serverGuid, getUrl(), getPort(), closed, receivedMessages.get());
    }
    /** get port of this server */
    public int getPort() {
        return -1;
    }
    /** get port of this server */
    public String getUrl() {
        return "";
    }
    public String getServerParams() {
        return "";
    }

    /** create server row for this server */
    public DistAgentServerRow createServerRow() {
        var createdDate = LocalDateTime.now();
        var hostName = DistUtils.getCurrentHostName();
        var hostIp = DistUtils.getCurrentHostAddress();
        return new DistAgentServerRow(parentAgent.getAgentGuid(), getServerGuid(), getClientType().name(), hostName, hostIp, getPort(),
                getUrl(), createdDate, 1, createdDate, getServerParams());
    }

}
