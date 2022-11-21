package com.cache.agent.connectors;

import com.cache.agent.AgentInstance;
import com.cache.api.*;
import com.cache.base.ConnectorBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/** connector to Elasticsearch as agent manager - central point with registering/unregistering agents
 * TODO: implement global storage of agents in Elasticsearch
 * */
public class ConnectorElasticsearch extends ConnectorBase {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(ConnectorElasticsearch.class);

    public ConnectorElasticsearch(AgentInstance parentAgent) {
        super(parentAgent);
    }
    /** run for initialization in classes */
    @Override
    protected void onInitialize() {
        // check connection to Elasticsearch, if needed - create index with default name
    }
    @Override
    protected boolean onIsConnected() {
        // TODO: check if there is connection to Elasticsearch
        return false;
    }
    @Override
    protected AgentConfirmation onAgentRegister(AgentRegister register) {
        // TODO: implement registering to Elasticsearch - it is adding document to index with agents
        return null;
    }
    @Override
    protected AgentPingResponse onAgentPing(AgentPing ping) {
        // TODO: refresh document in elasticsearch with current date
        return null;
    }
    /** get list of agents from connector */
    @Override
    protected List<AgentSimplified> onGetAgents() {
        // TODO: get all agents from Elasticsearch
        return null;
    }
    /** close current connector */
    @Override
    protected void onClose() {
        // TODO: implement closing this connector
    }
}
