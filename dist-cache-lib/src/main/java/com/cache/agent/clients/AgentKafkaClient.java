package com.cache.agent.clients;

import com.cache.api.*;
import com.cache.api.enums.DistClientType;
import com.cache.api.enums.DistMessageType;
import com.cache.api.enums.DistServiceType;
import com.cache.base.AgentClientBase;
import com.cache.dao.DaoKafkaBase;
import com.cache.base.dtos.DistAgentServerRow;
import com.cache.interfaces.Agent;
import com.cache.interfaces.AgentClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** HTTP client with client communications */
public class AgentKafkaClient extends AgentClientBase implements AgentClient {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(AgentKafkaClient.class);

    protected int numPartitions = 1;
    protected short replicationFactor = 1;
    /** DAO for Kafka connector to send messages from client */
    private DaoKafkaBase daoKafka;

    /** creates new Kafka client  */
    public AgentKafkaClient(Agent parentAgent, DistAgentServerRow srv) {
        super(parentAgent, srv);
        this.serverRow = srv;
        this.connectedAgentGuid = srv.agentguid;
        initialize();
    }
    /** get type of client - socket, http, datagram, ... */
    public DistClientType getClientType() {
        return DistClientType.datagram;
    }
    /** get unified URL of this client */
    public String getUrl() {
        return serverRow.serverurl;
    }
    /** initialize client - connecting or reconnecting */
    public boolean initialize() {
        try {
            var params =  DaoParams.kafkaParams(serverRow.serverurl, numPartitions, replicationFactor);
            daoKafka = parentAgent.getAgentDao().getOrCreateDaoOrError(DaoKafkaBase.class, params);
            daoKafka.usedByComponent(this);
            log.info("Created new KAFKA client for server: " + serverRow.servertype + ", url: " + serverRow.serverurl + ", host: " + serverRow.serverhost + ", port: " + serverRow.serverport);
            AgentWelcomeMessage welcome = new AgentWelcomeMessage(parentAgent.getAgentInfo(), getClientInfo());
            DistMessage welcomeMsg = DistMessage.createMessage(DistMessageType.system, parentAgent.getAgentGuid(), DistServiceType.agent, connectedAgentGuid, DistServiceType.agent, "welcome",  welcome);
            send(welcomeMsg);
            return true;
        } catch (Exception ex) {
            log.warn("Cannot initialize client " + clientGuid + ", agent: " + this.parentAgent.getAgentGuid() + ", Exception at Start: "+ex.getMessage(), ex);
            return false;
        }
    }

    /** send message to this client */
    public boolean send(DistMessage msg) {
        try {
            String msgSerialized  = parentAgent.getSerializer().serializeToString(msg);
            log.info("Writing line to be sent using Kafka client: " + clientGuid + ", SIZE=" + msgSerialized.length() + ", serializer: " + parentAgent.getSerializer().getClass().getName() + ", message: " + msg.toString());
            daoKafka.send(msg.getToAgent(), msg.getMessageUid(), msgSerialized);
            return true;
        } catch (Exception ex) {
            log.warn("Error while sending Datagram packet for client: " + clientGuid + ", reason: " + ex.getMessage(), ex);
            parentAgent.getAgentIssues().addIssue("DatagramClient.send", ex);
            return false;
        }
    }

    /** close this client */
    public void close() {
        log.info("Closing Kafka client for GUID: " + clientGuid);
        try {
            AgentWelcomeMessage welcome = new AgentWelcomeMessage(parentAgent.getAgentInfo(), getClientInfo());
            DistMessage closeMsg = DistMessage.createMessage(DistMessageType.system, parentAgent.getAgentGuid(), DistServiceType.agent, connectedAgentGuid, DistServiceType.agent, "close",  welcome);
            send(closeMsg);
            working = false;
        } catch (Exception ex) {
            log.info(" Error while closing HTTP client connection, reason: "+ex.getMessage());

        }
    }

}