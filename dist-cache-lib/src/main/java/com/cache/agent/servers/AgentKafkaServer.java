package com.cache.agent.servers;

import com.cache.api.DaoParams;
import com.cache.api.enums.DistClientType;
import com.cache.api.DistConfig;
import com.cache.api.DistMessage;
import com.cache.dao.DaoKafkaBase;
import com.cache.base.ServerBase;
import com.cache.interfaces.Agent;
import com.cache.interfaces.AgentServer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/** Kafka to exchange messages between Agents */
public class AgentKafkaServer extends ServerBase implements AgentServer {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(AgentServerSocket.class);

    private String brokers;
    private String topicTemplate;
    private DaoKafkaBase daoKafka;
    int numPartitions = 1;
    short replicationFactor = 1;
    private String agentDedicatedTopicName;

    /** */
    public AgentKafkaServer(Agent parentAgent) {
        super(parentAgent);
        brokers = parentAgent.getConfig().getProperty(DistConfig.AGENT_SERVER_KAFKA_BROKERS, DistConfig.AGENT_SERVER_KAFKA_BROKERS_DEFAULT_VALUE);
        topicTemplate = parentAgent.getConfig().getProperty(DistConfig.AGENT_SERVER_KAFKA_TOPIC, "dist-agent-");
        agentDedicatedTopicName = "dist-agent-" + parentAgent.getAgentShortGuid();
        initializeServer();
    }
    public void initializeServer() {
        try {
            String clientId = getParentAgentGuid();
            String groupId = getParentAgentGuid();
            var params = DaoParams.kafkaParams(brokers, numPartitions, replicationFactor, clientId, groupId);
            parentAgent.getAgentDao().getOrCreateDao(DaoKafkaBase.class, params);
            daoKafka = new DaoKafkaBase(params, parentAgent);
            daoKafka.getNonExistingTopics(Set.of(agentDedicatedTopicName));
            daoKafka.createKafkaConsumer(agentDedicatedTopicName, this::receiveMessages);
            log.info("Started Kafka server using Brokers: " + brokers +", agent topic: " + agentDedicatedTopicName + ", agent: " + getParentAgentGuid());
        } catch (Exception ex) {
            log.info("Cannot start HTTP server, reason: " + ex.getMessage());
            parentAgent.getAgentIssues().addIssue("rAgentKafkaServer.initializeServer", ex);
        }
    }

    /** receive message from Kafka */
    public Boolean receiveMessages(ConsumerRecord<String, String> kafkaMsg) {
        try {
            DistMessage msg = (DistMessage)parentAgent.getSerializer().deserializeFromString(DistMessage.class.getName(), kafkaMsg.value());
            receivedMessages.incrementAndGet();
            log.info("Receive message in Kafka server for agent: " + parentAgent.getAgentGuid() + ", message: " + msg.toString());
            if (msg.isSystem()) {
                // parseWelcomeMessage(msg);
            } else {
                parentAgent.getAgentServices().receiveMessage(msg);
            }
            return true;
        } catch (Exception ex) {
            parentAgent.getAgentIssues().addIssue("rAgentKafkaServer.eceiveMessages", ex);
            return false;
        }
    }
    /** get type of clients to be connected to this server */
    public DistClientType getClientType() {
        return DistClientType.kafka;
    }
    /** get port of this server */
    public int getPort() {
        return 9092;
    }

    /** get URL of this server */
    public String getUrl() {
        return brokers;
    }

    @Override
    public void close() {
        try {
            log.info("Try to close Kafka server for Agent: " + parentAgent.getAgentGuid());
            daoKafka.close();
        } catch (Exception ex) {

        }
    }
}
