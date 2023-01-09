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

    int numPartitions = 1;
    short replicationFactor = 1;
    private DistAgentServerRow srv;
    private DaoKafkaBase daoKafka;

    /** creates new Kafka client  */
    public AgentKafkaClient(Agent parentAgent, DistAgentServerRow srv) {
        super(parentAgent);
        this.srv = srv;
        this.connectedAgentGuid = srv.agentguid;
        initialize();
    }
    /** get type of client - socket, http, datagram, ... */
    public DistClientType getClientType() {
        return DistClientType.datagram;
    }
    /** get unified URL of this client */
    public String getUrl() {
        return srv.serverurl;
    }
    /** initialize client - connecting or reconnecting */
    public boolean initialize() {
        try {
            String clientId = getParentAgentGuid();
            String groupId = getParentAgentGuid();
            //new DaoKafkaParams(, parentAgent.getAgentIssues());
            var params =  DaoParams.kafkaParams(srv.serverurl, numPartitions, replicationFactor, clientId, groupId);
            daoKafka = parentAgent.getAgentDao().getOrCreateDao(DaoKafkaBase.class, params).get(); //.orElseGet(() -> throw new IllegalArgumentException("Cannot create or get DAO for Kafka"));
            log.info("Created new KAFKA client for server: " + srv.servertype + ", url: " + srv.serverurl + ", host: " + srv.serverhost + ", port: " + srv.serverport);
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
            byte[] sendBuf  = parentAgent.getSerializer().serialize(msg);
            log.info("Writing line to be sent using DATAGRAM client: " + clientGuid + ", SIZE=" + sendBuf.length + ", serializer: " + parentAgent.getSerializer().getClass().getName() + ", message: " + msg.toString());




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