package com.cache.base;

import com.cache.agent.impl.Agentable;
import com.cache.api.DaoParams;
import com.cache.api.enums.DistComponentType;
import com.cache.api.info.AgentDaoInfo;
import com.cache.interfaces.Agent;
import com.cache.interfaces.AgentComponent;
import com.cache.interfaces.Dao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/** BAse class for any DAO connection to any storage: JDBC, Elasticsearch, Kafka, Redis, MongoDB, Cassandra and others... */
public abstract class DaoBase extends Agentable implements Dao, AgentComponent {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(DaoBase.class);

    /** parameters for this DAO */
    protected final DaoParams params;
    /** is DAO closed */
    protected boolean closed = false;
    /** all components that are using this DAO */
    protected Map<String, AgentComponent> components = new HashMap<>();

    /** creates new DAO to JDBC database */
    public DaoBase(DaoParams params, Agent agent) {
        super(agent);
        parentAgent.addComponent(this);
        this.params = params;
    }
    /** get unique ID of this DAO */
    public String getGuid() {
        return params.getKey();
    }
    /** get type of this component */
    public DistComponentType getComponentType() {
        return DistComponentType.dao;
    }
    /** get initialization parameters */
    public DaoParams getParams() {
        return params;
    }

    /** add component that is using this DAO */
    public void usedByComponent(AgentComponent component) {
        components.put(component.getGuid(), component);
        log.info("Added component that is using DAO, DAO GUID: " + getGuid() + ", URL: " + getUrl() + ", component: " + component.getComponentType() + ", component GUID: " + component.getGuid() + ", current components: " + components.size());
    }
    /** get all structures from DAO: tables, indices, topics, Document databases, ... */
    public abstract Collection<String> getDaoStructures();
    /** get URL of this DAO */
    public abstract String getUrl();
    /** get info about DAO */
    public AgentDaoInfo getInfo() {
        return new AgentDaoInfo(createDate, params.getKey(), params.getDaoType(), getUrl(), isConnected(), getDaoStructures());
    }

}
