package com.cache.agent.connectors;

import com.cache.agent.AgentInstance;
import com.cache.api.*;
import com.cache.base.RegistrationBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/** *
 *
 * TODO: implement global agent registration in Kafka
 *
 */
public class RegistrationKafka extends RegistrationBase {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(RegistrationKafka.class);

    public RegistrationKafka(AgentInstance parentAgent) {
        super(parentAgent);
    }
    /** run for initialization in classes */
    @Override
    protected void onInitialize() {
        var kafkaBrokers = parentAgent.getConfig().getProperty(DistConfig.KAFKA_BROKERS);
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
    protected AgentConfirmation onAgentUnregister(String agentGuid) {
        return new AgentConfirmation(agentGuid, true, false, 0, List.of());
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

    /** get list of active agents */
    public List<AgentSimplified> getAgentsActive() {
        return null;
    }

    /** close current connector */
    @Override
    protected void onClose() {
        // TODO: implement closing this connector
    }
}