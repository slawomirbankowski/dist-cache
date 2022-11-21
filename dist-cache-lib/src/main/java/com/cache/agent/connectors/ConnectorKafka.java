package com.cache.agent.connectors;

import com.cache.agent.AgentInstance;
import com.cache.api.*;
import com.cache.base.CacheBase;
import com.cache.base.ConnectorBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/** *
 *
 * TODO: implement global agent storage in Kafka
 *
 */
public class ConnectorKafka extends ConnectorBase {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(ConnectorKafka.class);

    public ConnectorKafka(AgentInstance parentAgent) {
        super(parentAgent);
    }
    /** run for initialization in classes */
    @Override
    protected void onInitialize() {
        var kafkaBrokers = parentAgent.getParentCache().getCacheConfig().getProperty(CacheConfig.KAFKA_BROKERS);
        try {
            log.info("Register to Kafka: " + kafkaBrokers);
            // TODO: register to Kafka, push agent info, read other agents
        } catch (Exception ex) {
            log.warn("Cannot connect to dist-cache application, reason: " + ex.getMessage(), ex);
        }
    }
    @Override
    protected boolean onIsConnected() {
        return false;
    }
    @Override
    protected AgentConfirmation onAgentRegister(AgentRegister register) {
        return null;
    }
    @Override
    protected AgentPingResponse onAgentPing(AgentPing ping) {
        return null;
    }
    /** get list of agents from connector */
    @Override
    protected List<AgentSimplified> onGetAgents() {
        return null;
    }

    /** close current connector */
    @Override
    protected void onClose() {
        // TODO: implement closing this connector
    }
}
