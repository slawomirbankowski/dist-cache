package com.cache.base;

import com.cache.agent.impl.Agentable;
import com.cache.api.DaoParams;
import com.cache.api.info.AgentDaoInfo;
import com.cache.interfaces.Agent;
import com.cache.interfaces.Dao;

import java.util.Collection;
import java.util.List;

/** BAse class for any DAO connection to any storage: JDBC, Elasticsearch, Kafka, Redis, MongoDB, Cassandra and others... */
public abstract class DaoBase extends Agentable implements Dao {

    /** parameters for this DAO */
    protected final DaoParams params;

    /** creates new DAO to JDBC database */
    public DaoBase(DaoParams params, Agent agent) {
        super(agent);
        this.params = params;
    }
    /** get unique ID of this DAO */
    public String getGuid() {
        return params.getKey();
    }
    /** get initialization parameters */
    public DaoParams getParams() {
        return params;
    }
    /** get all structures from DAO: tables, indices, topics, Document databases, ... */
    public abstract Collection<String> getDaoStructures();
    /** get info about DAO */
    public AgentDaoInfo getInfo() {
        return new AgentDaoInfo(createDate, params.getKey(), params.getDaoType());
    }

}
