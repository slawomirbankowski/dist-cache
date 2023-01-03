package com.cache.agent.servers;

import com.cache.api.DistClientType;
import com.cache.api.DistConfig;
import com.cache.base.ServerBase;
import com.cache.interfaces.Agent;
import com.cache.interfaces.AgentServer;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/** Kafka to exchange messages between Agents */
public class AgentKafkaServer extends ServerBase implements AgentServer {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(AgentServerSocket.class);

    private String brokers;
    private String topicTemplate;
    /** */
    private Thread mainThread;

    public AgentKafkaServer(Agent parentAgent) {
        super(parentAgent);
        brokers = parentAgent.getConfig().getProperty(DistConfig.AGENT_SERVER_KAFKA_BROKERS, "localhost:9092");
        topicTemplate = parentAgent.getConfig().getProperty(DistConfig.AGENT_SERVER_KAFKA_TOPIC, "dist-agent-");
        initializeServer();
    }
    public void initializeServer() {
        try {

            log.info("Started HTTP server!!!");
        } catch (Exception ex) {
            log.info("Cannot start HTTP server, reason: " + ex.getMessage());
            parentAgent.getAgentIssues().addIssue("AgentHttpServer", ex);
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

    /** get port of this server */
    public String getUrl() {
        return brokers;
    }

    @Override
    public void close() {
        try {
            log.info("Try to close HTTP server for Agent: " + parentAgent.getAgentGuid());

        } catch (Exception ex) {
        }
    }
}
