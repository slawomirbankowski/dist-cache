package com.cache.base;

import com.cache.agent.AgentInstance;
import com.cache.agent.impl.Agentable;
import com.cache.api.*;
import com.cache.api.info.AgentRegistrationInfo;
import com.cache.base.dtos.DistAgentRegisterRow;
import com.cache.base.dtos.DistAgentServerRow;
import com.cache.utils.CacheHitRatio;
import com.cache.utils.DistUtils;

import java.time.LocalDateTime;
import java.util.List;

/** base class to connect to registration service - global storage that is managing agents
 * connector should be able to register agent, ping it, check health
 * */
public abstract class RegistrationBase extends Agentable {

    /** global unique ID */
    private final String registerGuid = DistUtils.generateConnectorGuid(this.getClass().getSimpleName());

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
        super(parentAgent);
        initialize();
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
    /** inactivate active agents with last ping date for more than X minutes */
    public abstract boolean removeInactiveAgents(LocalDateTime beforeDate);
    /** remove inactive agents with last ping date for more than X minutes */
    public abstract boolean deleteInactiveAgents(LocalDateTime beforeDate);


    /** get normalized URL for this registration */
    public abstract String getUrl();
    /** get information about registration object */
    public AgentRegistrationInfo getInfo() {
        // String registerGuid, String registrationType, LocalDateTime createdDate, boolean initialized, boolean closed, boolean lastConnected, String url, AgentConfirmation confirmation
        return new AgentRegistrationInfo(registerGuid, getClass().getSimpleName(), getCreateDate(), initialized, closed, lastConnected, getUrl(), registerConfirmation);
    }
    /** get all agents */
    public List<AgentSimplified> getAgents() {
        return onGetAgents();
    }
    /** get agents from registration services */
    public abstract List<DistAgentRegisterRow> getAgentsNow();

    /** get all communication servers */
    public abstract List<DistAgentServerRow> getServers();
    /** ping given server by GUID */
    public abstract boolean serverPing(DistAgentServerRow serv);
    /** set active servers with last ping date before given date as inactive */
    public abstract boolean serversCheck(LocalDateTime inactivateBeforeDate, LocalDateTime deleteBeforeDate);

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
