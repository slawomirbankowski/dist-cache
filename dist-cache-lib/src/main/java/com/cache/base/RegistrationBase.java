package com.cache.base;

import com.cache.agent.AgentInstance;
import com.cache.api.*;
import com.cache.base.dtos.DistAgentRegisterRow;
import com.cache.base.dtos.DistAgentServerRow;
import com.cache.utils.CacheHitRatio;
import com.cache.utils.CacheUtils;

import java.time.LocalDateTime;
import java.util.List;

/** base class to connect to global storage that is managing agents
 * connector should be able to register agent, ping it, check health
 * */
public abstract class RegistrationBase {

    /** date and time of creation */
    private final LocalDateTime createdDate = LocalDateTime.now();
    /** global unique ID */
    private final String registerGuid = CacheUtils.generateConnectorGuid(this.getClass().getSimpleName());

    /** parent agent instance that is handling this connector */
    protected AgentInstance parentAgent;
    /** confirmation of registration of this agent to connector */
    protected AgentConfirmation registerConfirmation;
    /** flat to indicate if connector is initialized */
    private boolean initialized = false;
    /** */
    private boolean closed = true;
    /** status of last connection OR false if there were no connections yet */
    private boolean lastConnected = false;
    /** connection ratio */
    private CacheHitRatio connectRatio = new CacheHitRatio();

    /** constructor to save parent agent */
    public RegistrationBase(AgentInstance parentAgent) {
        this.parentAgent = parentAgent;
        initialize();
    }
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    /** get global ID of this connector */
    public String getRegisterGuid() {
        return registerGuid;
    }

    /** initialize */
    private void initialize() {
        onInitialize();
        initialized = true;
    }
    /** run for initialization in classes */
    protected abstract void onInitialize();
    /** returns status of initialized */
    public boolean isInitialized() {
        return initialized;
    }

    /** if connector is connected */
    public boolean isConnected() {
        // TODO: calculate connection OK ratio
        boolean connected = onIsConnected();
        lastConnected = connected;
        if (connected) {
            connectRatio.hit();
        } else {
            connectRatio.miss();
        }
        return connected;
    }
    /** last status of connection */
    public boolean isLastConnected() {
        return lastConnected;
    }

    /** if connector is connected */
    protected abstract boolean onIsConnected();

    /** register this agent to connector */
    public AgentConfirmation agentRegister(AgentRegister register) {
        AgentConfirmation cfm =  onAgentRegister(register);
        registerConfirmation = cfm;
        return cfm;
    }
    /** add issue for registration */
    public abstract void addIssue(DistIssue issue);
    /** register server for communication */
    public abstract void addServer(DistAgentServerRow serv);
    /** unregister server for communication */
    public abstract void unregisterServer(DistAgentServerRow serv);
    /** agent registration to be implemented in specific connector*/
    protected abstract AgentConfirmation onAgentRegister(AgentRegister register);

    /** ping from this agent to connector */
    public AgentPingResponse agentPing(AgentPing ping) {
        AgentPingResponse pingResp = onAgentPing(ping);
        // TODO: register latest ping response
        return pingResp;
    }
    public AgentConfirmation agentUnregister() {
        AgentConfirmation cfm =  onAgentUnregister(parentAgent.getAgentGuid());
        closed = true;
        return cfm;
    }
    protected abstract AgentConfirmation onAgentUnregister(String agentGuid);

    /** ping from this agent to connector */
    protected abstract AgentPingResponse onAgentPing(AgentPing ping);

    /** get all agents */
    public List<AgentSimplified> getAgents() {
        return onGetAgents();
    }
    /** get agents from registration services */
    public abstract List<DistAgentRegisterRow> getAgentsNow();

    /** get all communication servers */
    public abstract List<DistAgentServerRow> getServers();

    /** get list of agents from connector */
    protected abstract List<AgentSimplified> onGetAgents();
    /** get list of active agents from JDBC table */
    public abstract List<AgentSimplified> getAgentsActive();

    /** close current connector */
    public void close() {
        onClose();

    }
    /** close current connector */
    protected abstract void onClose();

}
