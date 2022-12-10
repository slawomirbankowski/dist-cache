package com.cache.agent.impl;

import com.cache.agent.AgentInstance;
import com.cache.agent.clients.SocketServerClient;
import com.cache.agent.servers.AgentServerSocket;
import com.cache.api.DistConfig;
import com.cache.dtos.DistAgentServerRow;
import com.cache.interfaces.AgentClient;
import com.cache.interfaces.AgentConnectors;
import com.cache.interfaces.AgentServer;
import com.cache.utils.CacheUtils;
import com.cache.utils.HashMapMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Optional;

/** manager for connections inside agent - servers and clients */
public class AgentConnectorsImpl implements AgentConnectors {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(AgentConnectorsImpl.class);
    /** parent agent for this connection manager */
    private AgentInstance parentAgent;
    /** all servers for connections to other agents */
    private final java.util.concurrent.ConcurrentHashMap<String, AgentServer> servers = new java.util.concurrent.ConcurrentHashMap<>();

    /** all servers for connections to other agents */
    private final java.util.concurrent.ConcurrentHashMap<String, DistAgentServerRow> agentServers = new java.util.concurrent.ConcurrentHashMap<>();
    /** map of map of clients connected to different agents
     * key1 = agentGUID
     * key2 = serverGUID
     * value = client to transfer messages to this agent */
    private final HashMapMap<String, String, AgentClient> serverConnectors = new HashMapMap<>();

    /** create new connectors */
    public AgentConnectorsImpl(AgentInstance parentAgent) {
        this.parentAgent = parentAgent;
    }
    /** open servers for communication  */
    public void openServers() {
        if (parentAgent.getConfig().hasProperty(DistConfig.AGENT_SOCKET_PORT)) {
            int portNum = parentAgent.getConfig().getPropertyAsInt(DistConfig.AGENT_SOCKET_PORT, DistConfig.AGENT_SOCKET_PORT_VALUE_SEQ.incrementAndGet());
            log.info("SERVER SOCKET opening at port: " + portNum + " for agent: " + parentAgent.getAgentGuid());
            AgentServerSocket serv = new AgentServerSocket(parentAgent);
            serv.initializeServer(portNum);
            servers.put(serv.getServerGuid(), serv);
            // register server for communication
            var createdDate = new java.util.Date();
            var hostName = CacheUtils.getCurrentHostName();
            var hostIp = CacheUtils.getCurrentHostAddress();
            var servDto = new DistAgentServerRow(parentAgent.getAgentGuid(), serv.getServerGuid(), "socket", hostName, hostIp, portNum,
                    "socket://" + hostName + ":" + portNum + "/", createdDate, 1, createdDate);
            parentAgent.getAgentRegistrations().registerServer(servDto);
        }
    }
    /** check list of active servers and connect to the server if this is still not connected */
    public void checkActiveServers(List<DistAgentServerRow> activeServers) {
        log.info("Connectors updating servers from registers, count: " + activeServers.size() + ", unique agentServers: " + agentServers.size() + ", object servers count: " + servers.size() + ", clients count: " + serverConnectors.totalSize());
        for (DistAgentServerRow srv: activeServers) {
            agentServers.putIfAbsent(srv.serverguid, srv);
        }
        agentServers.values().stream().forEach(srv -> {
            if (!srv.agentguid.equals(parentAgent.getAgentGuid())) {
                Optional<AgentClient> client = serverConnectors.getValue(srv.agentguid, srv.serverguid);
                if (client.isEmpty()) {
                    log.info("=====----> NO client to agent: " + srv.agentguid + ", server: " + srv.serverguid + ", type" + srv.servertype + ", creating NEW ONE !!!!!!!!!");
                    var createdClient = createClient(srv);
                    if (createdClient.isPresent()) {
                        serverConnectors.add(srv.agentguid, srv.serverguid, createdClient.get());
                    }
                }
            }
        });
    }
    /** close all connectors, clients, servers  */
    public void close() {
        servers.values().stream().forEach(serv -> {
            serv.close();
        });
        serverConnectors.getAllValues().stream().forEach(cli -> cli.close());
    }

    /** create new client that would be used for connecting to given server */
    private Optional<AgentClient> createClient(DistAgentServerRow srv) {
        if (srv.servertype.equals("socket")) {
            log.info("Creating new socket client that would be connected to agent: " + srv.agentguid + ", type: " + srv.servertype + ", host: " + srv.serverhost);
            return Optional.of(new SocketServerClient(parentAgent, srv));
        }
        return Optional.empty();
    }



}
