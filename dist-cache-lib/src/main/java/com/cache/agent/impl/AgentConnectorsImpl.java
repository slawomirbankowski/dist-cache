package com.cache.agent.impl;

import com.cache.agent.AgentInstance;
import com.cache.agent.clients.SocketServerClient;
import com.cache.agent.servers.AgentServerSocket;
import com.cache.api.*;
import com.cache.base.dtos.DistAgentServerRow;
import com.cache.interfaces.AgentClient;
import com.cache.interfaces.AgentConnectors;
import com.cache.interfaces.AgentServer;
import com.cache.utils.CacheUtils;
import com.cache.utils.DistMapTimeStorage;
import com.cache.utils.HashMapMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

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
    /** map of local connectors as part of local clients
     * key=clientGUID
     * value= client
     * */
    private final java.util.concurrent.ConcurrentHashMap<String, AgentClient> localConnectors = new java.util.concurrent.ConcurrentHashMap<>();
    /** queue with clients */
    private final java.util.concurrent.ConcurrentLinkedQueue<AgentClient> clientQueue = new ConcurrentLinkedQueue<>();
    /** table with all clients  */
    private final java.util.ArrayList<AgentClient> clientTable = new ArrayList<>();
    /** messages already sent with callbacks */
    private final DistMapTimeStorage<DistMessageFull> sentMessages = new DistMapTimeStorage();
    /** create new connectors */
    public AgentConnectorsImpl(AgentInstance parentAgent) {
        this.parentAgent = parentAgent;
    }
    /** open servers for communication  */
    public void openServers() {
        if (parentAgent.getConfig().hasProperty(DistConfig.AGENT_SOCKET_PORT)) {
            int portNum = parentAgent.getConfig().getPropertyAsInt(DistConfig.AGENT_SOCKET_PORT, DistConfig.AGENT_SOCKET_PORT_VALUE_SEQ.incrementAndGet());
            log.info("SERVER SOCKET opening for agent: " + parentAgent.getAgentGuid() + " at port: " + portNum + ", current servers: " + servers.size());
            AgentServerSocket serv = new AgentServerSocket(parentAgent);
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

    /** get count of servers */
    public int getServersCount() {
        return servers.size();
    }
    /** get all UIDs of servers */
    public List<String> getServerKeys() {
        return servers.values().stream().map(v -> v.getServerGuid()).collect(Collectors.toList());
    }
    /** get number of clients */
    public int getClientsCount() {
        return serverConnectors.totalSize();
    }

    /** get client keys */
    public List<String> getClientKeys() {
        return serverConnectors.getAllValues().stream().map(x -> x.getClientGuid()).collect(Collectors.toList());
    }
    /** check list of active servers and connect to the server if this is still not connected */
    public void checkActiveServers(List<DistAgentServerRow> activeServers) {
        log.info("%%%%%%%%%%%%%%>>> Connectors updating servers from registers for agent: " + parentAgent.getAgentGuid() + ", count: " + activeServers.size() + ", agentServers: " + agentServers.size() + ", servers: " + servers.size() + ", clients: " + serverConnectors.totalSize());
        for (DistAgentServerRow srv: activeServers) {
            agentServers.putIfAbsent(srv.serverguid, srv);
        }
        // check all servers
        agentServers.values().stream().forEach(srv -> {
            if (!srv.agentguid.equals(parentAgent.getAgentGuid())) {
                Optional<AgentClient> client = serverConnectors.getValue(srv.agentguid, srv.serverguid);
                if (client.isEmpty()) {
                    log.info("%%%%%%%%%%%%%%>>> Connectors from agent: " + parentAgent.getAgentGuid() +  ", NO client to agent: " + srv.agentguid + ", server: " + srv.serverguid + ", type" + srv.servertype + ", creating NEW ONE !!!!!!!!!");
                    var createdClient = createClient(srv);
                    if (createdClient.isPresent()) {
                        serverConnectors.add(srv.agentguid, srv.serverguid, createdClient.get());
                        clientQueue.add(createdClient.get());
                        clientTable.add(createdClient.get());
                    }
                } else {
                    // client.get().send("CHECK SERVERS - PING AGENT FROM " + parentAgent.getAgentGuid());
                }
            }
        });
        log.info("%%%%%%%%%%%%%%>>> Connectors AFTER check servers for agent: " + parentAgent.getAgentGuid() + ", agentServers: " + agentServers.size() + ", servers: " + servers.size() + ", clients: " + serverConnectors.totalSize());
    }
    /** register new client created local as part of server */
    public void registerLocalClient(AgentClient client) {
        localConnectors.put(client.getClientGuid(), client);
        log.info("Register new local client for agent: " + parentAgent.getAgentGuid() + ", client UID: " + client.getClientGuid() + ", total local clients: " + localConnectors.size());
    }
    /** message send to agents, directed to services, selected method */
    public void sendMessage(DistMessage msg) {
        sendMessage(msg.withNoCallbacks());
    }
    /** message send to agents, directed to services, selected method, add callbacks to be called when response would be back */
    public void sendMessage(DistMessageFull msg) {
        log.debug("...................Sending message: " + msg.getMessage().toString());
        if (msg.getMessage().isTypeRequest()) {
            sentMessages.addItem(msg.getMessage().getMessageUid(), msg, msg.getMessage().getValidTill());
        }
        if (msg.getMessage().isSentToBroadcast()) {
            sendMessageBroadcast(msg);
        } else if (msg.getMessage().isSentToRandom()) {
            sendMessageRandom(msg);
        } else if (msg.getMessage().isSentRoundRobin()) {
            sendMessageRoundRobin(msg);
        } else if (msg.getMessage().isSentToTag()) {
            sendMessageTag(msg);
        } else {
            sendMessageAgent(msg);
        }
    }
    /** message send broadcast */
    public void sendMessageBroadcast(DistMessageFull msg) {
        // sending broadcast - to all known agents
        var allClients = serverConnectors.getAllValues();
        log.info("..................Sending broadcast message to all clients: " + allClients.size() + ", message UID: " + msg.getMessage().getMessageUid());
        allClients.stream().forEach(client -> {
            var res = client.send(msg.getMessage());
            msg.addClient(client);
            msg.sendWithResult(res);
        });
    }
    /** message send to random agent from clients */
    public void sendMessageRandom(DistMessageFull msg) {
        // TODO: add to message queue and map for responses
        synchronized (clientTable) {
            if (clientTable.size() > 0) {
                int clientId = CacheUtils.randomInt(clientTable.size());
                AgentClient cl = clientTable.get(clientId);
                if (cl != null) {
                    cl.send(msg.getMessage());
                }
            } else {
                msg.applyCallback(DistCallbackType.onClientNotFound);
                // TODO: add to call later
            }
        }
    }
    /** message send broadcast */
    public void sendMessageRoundRobin(DistMessageFull msg) {
        AgentClient client = clientQueue.poll();
        if (client != null) {
            clientQueue.add(client);
            client.send(msg.getMessage());
            msg.addClient(client);
            msg.sendWithSuccess();
        } else {
            msg.applyCallback(DistCallbackType.onClientNotFound);
            //msgCallbacks.getCallbacks().applyErrorCallback(msg);
        }
    }
    /** message send to clients with tags */
    public void sendMessageTag(DistMessageFull msg) {
         // TODO: get all agents for given tags and send them message
        String[] tags = msg.getMessage().getTags().split(",");
        serverConnectors.getAllValues().stream().filter(client -> client.hasTags(tags)).forEach(client -> {
            client.send(msg.getMessage());
            client.getClientGuid();
        });
    }
    /** message send to only one agent by GUID */
    public void sendMessageAgent(DistMessageFull msg) {
        // sending to exactly one agent
        var clientMap = serverConnectors.get(msg.getMessage().getToAgent());
        if (clientMap == null || clientMap.size() == 0) {
            msg.applyCallback(DistCallbackType.onClientNotFound);
        } else {
            // TODO: check if it should be send to any client for that agent or just one of clients
            clientMap.values().stream().forEach(x -> x.send(msg.getMessage()));
        }
    }
    /** mark response for this message, it is executing callbacks onResponse */
    public void markResponse(DistMessage msg) {
        try {
            DistMessageFull msgFull = sentMessages.getByUid(msg.getMessageUid());
            if (msgFull != null) {
                log.debug("Mark response for message: " + msg.getMessageUid() + ", sentMessages: " + sentMessages.getItemsCount() + ", callbacks: " + msgFull.getCallbacks().getCallbacksCount());
                msgFull.applyCallback(DistCallbackType.onResponse, msg);
                sentMessages.removeByUid(msg.getMessageUid(), msgFull.getMessage().getValidTill());
            } else {
                log.debug("Mark response for message: " + msg.getMessageUid() + ", NO original message, messages: " + sentMessages.getItemsCount());
            }
        } catch (Exception ex) {
            parentAgent.getAgentIssues().addIssue("AgentConnectorsImpl.markResponse", ex);
        }
    }

    /** close all connectors, clients, servers  */
    public void close() {
        log.info("Closing connectors for agent: " + this.parentAgent.getAgentGuid() + ", servers: " + servers.size() + ", clients: " + serverConnectors.totalSize());
        servers.values().stream().forEach(serv -> {
            serv.close();
        });
        serverConnectors.getAllValues().stream().forEach(cli -> cli.close());
        log.info("Closed all connectors for agent: " + this.parentAgent.getAgentGuid());
    }

    /** create new client that would be used for connecting to given server */
    private Optional<AgentClient> createClient(DistAgentServerRow srv) {
        // TODO: create client based on server
        if (srv.servertype.equals("socket")) {
            log.info("%%%%%%%%%%%>>> Creating new socket client that would be connected to agent: " + srv.agentguid + ", type: " + srv.servertype + ", host: " + srv.serverhost);
            var client = new SocketServerClient(parentAgent, srv);
            // DistMessageType messageType, String fromAgent, DistServiceType fromService, String toAgent, DistServiceType toService, String method, Object message
            DistMessage welcomeMsg = DistMessage.createMessage(DistMessageType.welcome, parentAgent.getAgentGuid(), DistServiceType.agent, srv.agentguid, DistServiceType.agent, "welcome",  "");
            client.send(welcomeMsg);
            return Optional.of(client);
        }
        // TODO: create other types of clients based on servertype that client should connect to
        return Optional.empty();
    }

}
