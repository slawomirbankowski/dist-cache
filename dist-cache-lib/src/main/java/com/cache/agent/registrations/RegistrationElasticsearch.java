package com.cache.agent.registrations;

import com.cache.agent.AgentInstance;
import com.cache.api.*;
import com.cache.base.RegistrationBase;
import com.cache.dtos.DistAgentRegisterRow;
import com.cache.dtos.DistAgentServerRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/** connector to Elasticsearch as agent manager - central point with registering/unregistering agents
 * TODO: implement global storage of agents in Elasticsearch
 * */
public class RegistrationElasticsearch extends RegistrationBase {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(RegistrationElasticsearch.class);

    public RegistrationElasticsearch(AgentInstance parentAgent) {
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
    protected AgentConfirmation onAgentUnregister(String agentGuid) {
        return new AgentConfirmation(agentGuid, true, false, 0, List.of());
    }
    @Override
    protected AgentPingResponse onAgentPing(AgentPing ping) {
        // TODO: refresh document in elasticsearch with current date
        return null;
    }
    /** add issue for registration */
    public void addIssue(DistIssue issue) {
    }
    /** register server for communication */
    public void addServer(DistAgentServerRow serv) {
    }
    /** unregister server for communication */
    public void unregisterServer(DistAgentServerRow serv) {
    }
    /** get all communication servers */
    public  List<DistAgentServerRow> getServers() {
        return new LinkedList<>();
    }
    /** get list of agents from connector */
    @Override
    protected List<AgentSimplified> onGetAgents() {
        // TODO: get all agents from Elasticsearch
        return null;
    }
    /** get agents from registration services */
    public List<DistAgentRegisterRow> getAgentsNow() {
        return new LinkedList<>();
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
