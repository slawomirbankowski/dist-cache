package com.cache.agent;

import com.cache.api.*;
import com.cache.base.CacheBase;
import com.cache.utils.HttpConnectionHelper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

/** agent class to be connected to dist-cache applications, Kafka, Elasticsearch or other global agent repository
 * Agent is also connecting directly to other agents */
public class AgentInstance {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(AgentInstance.class);
    /** date ant time of creation */
    private final LocalDateTime createDate = LocalDateTime.now();
    /** parent cache for this agent */
    private CacheBase parentCache;
    /** generate secret of this agent to be able to put commands */
    private final String agentSecret = UUID.randomUUID().toString();
    private int workingPort = -1;
    /** all connected agents */
    private HashMap<String, AgentSimplified> connectedAgents = new HashMap<>();
    /** socket server for connections from other agents */
    private java.net.ServerSocket socket = null;
    /** */
    private HttpConnectionHelper applicationConn = null;
    public AgentInstance(CacheBase parentCache) {
        this.parentCache = parentCache;
    }
    /** initialize agent - server, application, jdbc, kafka */
    public void initializeAgent() {
        log.info("Initializing agent for cache " + parentCache.getCacheGuid());
        workingPort = parentCache.getCacheConfig().getPropertyAsInt(CacheConfig.CACHE_PORT, CacheConfig.CACHE_PORT_VALUE_DEFAULT);
        openSocketServer();
        registerToApplication();
        registerToJdbc();
        registerToKafka();
    }
    /** */
    private void openSocketServer() {
        if (parentCache.getCacheConfig().hasProperty(CacheConfig.CACHE_PORT)) {
            // open socket port
            try {
                log.info("Starting socket for incoming connections from other clients on port " + workingPort);
                // TODO: create SocketServer and thread for accepting sockets
                //java.net.ServerSocket socket = new java.net.ServerSocket(workingPort);
                //socket.accept();
                //socket.getLocalPort();
                //socket.connect();
                //parentCache.addIssue();
            } catch (Exception ex) {
            }
        }
    }
    /** */
    private void registerToApplication() {
        if (parentCache.getCacheConfig().hasProperty(CacheConfig.CACHE_APPLICATION_URL)) {
            String urlString = parentCache.getCacheConfig().getProperty(CacheConfig.CACHE_APPLICATION_URL);
            try {
                log.info("Connecting to dist-cache application to register, URL: " + urlString);
                applicationConn = new HttpConnectionHelper(urlString);
                var agents = connectedAgents.values().stream().collect(Collectors.toList());
                AgentRegister register = new AgentRegister(parentCache.getCacheGuid(), agentSecret, CacheUtils.getCurrentHostName(), workingPort, createDate, parentCache.getStoragesInfo(), agents);
                ObjectMapper mapper = JsonMapper.builder()
                        .findAndAddModules()
                        .build();
                String registerBody = mapper.writeValueAsString(register);
                log.info("Try to register agent with endpoint /agent and body: " + registerBody);
                var response = applicationConn.callHttpPut("/v1/agent", registerBody);
                response.isOk();
                // TODO: save response from application

                log.info("Got response from APP: " + response.getInfo());

            } catch (Exception ex) {
                log.warn("Cannot connect to dist-cache application, reason: " + ex.getMessage(), ex);
            }
        }

    }
    private void registerToJdbc() {
        // register agent to JDBC
        if (parentCache.getCacheConfig().hasProperty(CacheConfig.JDBC_URL)) {
            var jdbcUrl = parentCache.getCacheConfig().getProperty(CacheConfig.JDBC_URL);
            var jdbcDriver = parentCache.getCacheConfig().getProperty(CacheConfig.JDBC_DRIVER);
            var jdbcUser = parentCache.getCacheConfig().getProperty(CacheConfig.JDBC_USER);
            var jdbcPass = parentCache.getCacheConfig().getProperty(CacheConfig.JDBC_PASS);
            try {
                log.info("Connecting to JDBC: " + jdbcUrl);
                // TODO: register to JDBC complicant database, create table if not exist,
                //
            } catch (Exception ex) {
                log.warn("Cannot connect to dist-cache application, reason: " + ex.getMessage(), ex);
            }
        }
    }

    private void registerToKafka() {
        if (parentCache.getCacheConfig().hasProperty(CacheConfig.KAFKA_BROKERS)) {
            var kafkaBrokers = parentCache.getCacheConfig().getProperty(CacheConfig.KAFKA_BROKERS);
            try {
                log.info("Register to Kafka: " + kafkaBrokers);
                // TODO: register to Kafka, push agent info, read other agents

            } catch (Exception ex) {
                log.warn("Cannot connect to dist-cache application, reason: " + ex.getMessage(), ex);
            }
        }
    }

    /** run by parent cache every 1 minute */
    public void onTimeCommunicate() {
        // TODO: implement communication of agent with other cache agents
        try {
            // TODO: get list of agents from application, compare them to
            if (applicationConn != null) {
                applicationConn.callHttpGet("/v1/agents");
            }
            // TODO: connect to all nearby agents, check statuses
        } catch (Exception ex) {
        }
    }

    /** close all items in this agent */
    public void close() {
        // TODO: close all items for this agent - unregister in application, notify all agents
    }

}
