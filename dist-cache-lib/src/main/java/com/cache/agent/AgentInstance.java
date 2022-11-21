package com.cache.agent;

import com.cache.agent.connectors.ConnectorApplication;
import com.cache.agent.connectors.ConnectorElasticsearch;
import com.cache.agent.connectors.ConnectorJdbc;
import com.cache.agent.connectors.ConnectorKafka;
import com.cache.api.*;
import com.cache.base.CacheBase;
import com.cache.base.ConnectorBase;
import com.cache.utils.CacheUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
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
    /** all connector for registration, pings, unregistrations, get list of agents */
    private final HashMap<String, ConnectorBase> connectors = new HashMap<>();
    private int workingPort = -1;
    /** all connected agents */
    private final HashMap<String, AgentSimplified> connectedAgents = new HashMap<>();

    public AgentInstance(CacheBase parentCache) {
        this.parentCache = parentCache;
    }
    /** initialize agent - server, application, jdbc, kafka */
    public void initializeAgent() {
        log.info("Initializing agent for cache " + parentCache.getCacheGuid());
        workingPort = parentCache.getCacheConfig().getPropertyAsInt(CacheConfig.CACHE_PORT, CacheConfig.CACHE_PORT_VALUE_DEFAULT);
        openSocketServer();
        createConnectors();
        registerToConnectors();
    }
    /** get parent cache object */
    public CacheBase getParentCache() {
        return parentCache;
    }
    /** get list of connected agents */
    public List<AgentSimplified> getAgents() {
        return connectedAgents.values().stream().collect(Collectors.toList());
    }
    /** get secret generated or set for this agent */
    public String getAgentSecret() {
        return agentSecret;
    }
    /** get date and time of creating this agent */
    public LocalDateTime getCreateDate() {
        return createDate;
    }
    /** get working port of SocketServer to have connections from other agents */
    public int getWorkingPort() {
        return workingPort;
    }

    /** open socket server  */
    private void openSocketServer() {
        if (parentCache.getCacheConfig().hasProperty(CacheConfig.CACHE_PORT)) {
        }
    }
    private void createConnectors() {
        if (parentCache.getCacheConfig().hasProperty(CacheConfig.CACHE_APPLICATION_URL)) {
            createAndAddConnector(ConnectorApplication.class.getName());
        }
        if (parentCache.getCacheConfig().hasProperty(CacheConfig.JDBC_URL)) {
            createAndAddConnector(ConnectorJdbc.class.getName());
        }
        if (parentCache.getCacheConfig().hasProperty(CacheConfig.KAFKA_BROKERS)) {
            createAndAddConnector(ConnectorKafka.class.getName());
        }
        if (parentCache.getCacheConfig().hasProperty(CacheConfig.ELASTICSEARCH_URL)) {
            createAndAddConnector(ConnectorElasticsearch.class.getName());
        }
    }
    /** create global connector and save in connectors */
    private void createAndAddConnector(String className) {
        synchronized (connectors) {
            createConnectorForClass(className).stream().forEach(connector -> {
                connectors.put(connector.toString(), connector);
            });
        }
    }
    /** create connector for current instance and given class */
    private Optional<ConnectorBase> createConnectorForClass(String className) {
        try {
            log.info("Try to create connector for class: " + className);
            ConnectorBase connector = (ConnectorBase)Class.forName(className)
                    .getConstructor(AgentInstance.class)
                    .newInstance(this);
            return Optional.of(connector);
        } catch (Exception ex) {
            return Optional.empty();
        }
    }
    /** register this agent to all available connectors */
    private void registerToConnectors() {
        var agents = getAgents();
        AgentRegister register = new AgentRegister(parentCache.getCacheGuid(), getAgentSecret(),
                CacheUtils.getCurrentHostName(), CacheUtils.getCurrentHostAddress(),
                getWorkingPort(), getCreateDate(), parentCache.getStoragesInfo(), agents);
        try {
            synchronized (connectors) {
                log.info("Registering this agent " + parentCache.getCacheGuid() + " on host " + register.hostName + " to all connectors: " + connectors.size());
                connectors.values().stream().forEach(c -> {
                    c.agentRegister(register);
                });
            }
        } catch (Exception ex) {
            log.warn("Cannot register agents to connectors, reason: " + ex.getMessage(), ex);
        }
    }

    /** run by parent cache every 1 minute */
    public void onTimeCommunicate() {
        // TODO: implement communication of agent with other cache agents
        try {
            connectors.entrySet().stream().forEach(e -> {
                e.getKey();
                e.getValue().agentPing(null);
            });

            // TODO: connect to all nearby agents, check statuses
        } catch (Exception ex) {
        }
    }

    /** close all items in this agent */
    public void close() {
        // TODO: close all items for this agent - unregister in application, notify all agents
    }

}
