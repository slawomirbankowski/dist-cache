package com.cache.agent.registrations;

import com.cache.agent.AgentInstance;
import com.cache.api.*;
import com.cache.dao.DaoElasticsearchBase;
import com.cache.base.RegistrationBase;
import com.cache.base.dtos.DistAgentRegisterRow;
import com.cache.base.dtos.DistAgentServerRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** connector to Elasticsearch as agent manager - central point with registering/unregistering agents
 * TODO: implement global storage of agents in Elasticsearch
 * */
public class RegistrationElasticsearch extends RegistrationBase {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(RegistrationElasticsearch.class);

    private final String elasticUrl;
    private final String elasticUser;
    private final String elasticPass;

    private String registerIndexName = "distagentregister";
    private String serverIndexName = "distagentserver";
    /** DAO to Elasticsearch */
    private DaoElasticsearchBase elasticDao = null;

    public RegistrationElasticsearch(AgentInstance parentAgent) {
        super(parentAgent);
        elasticUrl = parentAgent.getConfig().getProperty(DistConfig.AGENT_REGISTRATION_JDBC_DIALECT, "");
        elasticUser = parentAgent.getConfig().getProperty(DistConfig.AGENT_REGISTRATION_JDBC_USER, "");
        elasticPass = parentAgent.getConfig().getProperty(DistConfig.AGENT_REGISTRATION_JDBC_PASS, "");
        onInitialize();
    }
    /** run for initialization in classes */
    @Override
    protected void onInitialize() {
        elasticDao = parentAgent.getAgentDao().getOrCreateDaoOrError(DaoElasticsearchBase.class, DaoParams.elasticsearchParams(elasticUrl, elasticUser, elasticPass));
        // check connection to Elasticsearch, if needed - create index with default name
        log.info("Connected to Elasticsearch, url: " + elasticUrl + ", indices: " + elasticDao.getIndices().size());
        elasticDao.createIndicesWithCheck(Set.of(registerIndexName, serverIndexName));
    }
    @Override
    protected boolean onIsConnected() {
        return elasticDao.getClusterInfo().size() > 0;
    }
    @Override
    protected AgentConfirmation onAgentRegister(AgentRegister register) {
        elasticDao.addOrUpdateDocument(registerIndexName, register.agentGuid, register.toMap());
        return new AgentConfirmation(register.agentGuid, true, false, 0, List.of());
    }
    protected AgentConfirmation onAgentUnregister(String agentGuid) {
        var deactiveMap = Map.of("type", "agent",
                "active", "true",
                "agentGuid", agentGuid,
                "closedDate", LocalDateTime.now().toString());
        elasticDao.addOrUpdateDocument(registerIndexName, agentGuid, deactiveMap);

        return new AgentConfirmation(agentGuid, false, true, 0, List.of());
    }
    @Override
    protected AgentPingResponse onAgentPing(AgentPing ping) {
        // TODO: refresh document in elasticsearch with current date
        elasticDao.getDocument(registerIndexName, ping.agentGuid);

        return null;
    }
    /** remove active agents without last ping date for more than X minutes */
    public boolean removeInactiveAgents(LocalDateTime beforeDate) {

        return true;
    }

    /** remove inactive agents with last ping date for more than X minutes */
    public boolean deleteInactiveAgents(LocalDateTime beforeDate) {
        return true;
    }
    /** get normalized URL for this registration */
    public String getUrl() {
        return "";
    }
    /** add issue for registration */
    public void addIssue(DistIssue issue) {

    }
    /** register server for communication */
    public void addServer(DistAgentServerRow serv) {
        elasticDao.addOrUpdateDocument(serverIndexName, serv.serverguid, serv.toMap());
    }
    /** unregister server for communication */
    public void unregisterServer(DistAgentServerRow serv) {

    }
    /** get all communication servers */
    public List<DistAgentServerRow> getServers() {
        elasticDao.searchComplex(serverIndexName, "type", "server");

        return new LinkedList<>();
    }
    /** ping given server by GUID */
    public boolean serverPing(DistAgentServerRow serv) {

        return false;
    }
    /** set active servers with last ping date before given date as inactive */
    public boolean serversCheck(LocalDateTime inactivateBeforeDate, LocalDateTime deleteBeforeDate) {

        return false;
    }

    /** get list of agents from connector */
    @Override
    protected List<AgentSimplified> onGetAgents() {
        // TODO: get all agents from Elasticsearch
        elasticDao.searchComplex(serverIndexName, "type", "agent");
        return null;
    }
    /** get agents from registration services */
    public List<DistAgentRegisterRow> getAgentsNow() {
        elasticDao.searchComplex(serverIndexName, "type", "agent");
        return new LinkedList<>();
    }
    /** get list of active agents */
    public List<AgentSimplified> getAgentsActive() {
        elasticDao.searchComplex(serverIndexName, "type", "agent");
        return null;
    }
    /** close current connector */
    @Override
    protected void onClose() {
        // TODO: implement closing this connector
        elasticDao.close();
    }
}
