package com.cache.agent.impl;

import com.cache.agent.AgentInstance;
import com.cache.agent.apis.WebSimpleApi;
import com.cache.agent.clients.SocketServerClient;
import com.cache.agent.servers.AgentServerSocket;
import com.cache.api.*;
import com.cache.base.AgentWebApi;
import com.cache.base.dtos.DistAgentServerRow;
import com.cache.interfaces.*;
import com.cache.utils.CacheUtils;
import com.cache.utils.DistMapTimeStorage;
import com.cache.utils.HashMapMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/** manager for connections inside agent - servers and clients */
public class AgentApiImpl implements AgentApi {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(AgentApiImpl.class);
    /** parent agent for this connection manager */
    private AgentInstance parentAgent;
    /** all servers for connections to other agents */
    private final java.util.concurrent.ConcurrentHashMap<String, AgentWebApi> apiConnectors = new java.util.concurrent.ConcurrentHashMap<>();

    /** create new connectors */
    public AgentApiImpl(AgentInstance parentAgent) {
        this.parentAgent = parentAgent;
    }

    /** get parent agent connected to this API implementation */
    public Agent getAgent() {
        return parentAgent;
    }
    /** open all known APIs for this agent */
    public void openApis() {
        log.info("Opening Agent Web APIs for agent: " + parentAgent.getAgentGuid());
        if (parentAgent.getConfig().hasProperty(DistConfig.AGENT_API_PORT)) {
            log.info("Creates new WebApi interface for Agent: " + parentAgent.getAgentGuid());
            WebSimpleApi api = new WebSimpleApi(this);
            apiConnectors.put("", api);
        }
    }

    /** get count of APIs */
    public int getApisCount() {
        return apiConnectors.size();
    }
    /** get all types of registered Web APIs */
    public List<String> getApiTypes() {
        return apiConnectors.values().stream().map(v -> v.getApiType()).collect(Collectors.toList());
    }
    /** check all registered APIs */
    public void checkApis() {
        log.info("%%%%%%%%%%%%%%>>> Connectors updating servers from registers for agent: " + parentAgent.getAgentGuid());

        log.info("%%%%%%%%%%%%%%>>> Connectors AFTER check servers for agent: " + parentAgent.getAgentGuid() );
    }
    /** close all connectors, clients, servers  */
    public void close() {
        log.info("Closing APIs for agent: " + this.parentAgent.getAgentGuid() + ", count: " + apiConnectors.size());
        apiConnectors.values().stream().forEach(api -> api.close());
    }

}
