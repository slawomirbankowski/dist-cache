package com.cache.agent.connectors;

import com.cache.agent.AgentInstance;
import com.cache.api.*;
import com.cache.base.ConnectorBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.List;
import java.util.function.Function;

/**
 *
 * TODO: implement global agent storage in JDBC database
 * */
public class ConnectorJdbc extends ConnectorBase {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(ConnectorJdbc.class);

    /** DBCP */
    private org.apache.commons.dbcp.BasicDataSource connPool;

    public ConnectorJdbc(AgentInstance parentAgent) {
        super(parentAgent);
    }
    /** run for initialization in classes */
    @Override
    public void onInitialize() {
        var jdbcUrl = parentAgent.getParentCache().getCacheConfig().getProperty(CacheConfig.JDBC_URL);
        var jdbcDriver = parentAgent.getParentCache().getCacheConfig().getProperty(CacheConfig.JDBC_DRIVER);
        var jdbcUser = parentAgent.getParentCache().getCacheConfig().getProperty(CacheConfig.JDBC_USER);
        var jdbcPass = parentAgent.getParentCache().getCacheConfig().getProperty(CacheConfig.JDBC_PASS);
        var initConnections = parentAgent.getParentCache().getCacheConfig().getPropertyAsInt(CacheConfig.JDBC_INIT_CONNECTIONS, 2);
        var maxActiveConnections = parentAgent.getParentCache().getCacheConfig().getPropertyAsInt(CacheConfig.JDBC_MAX_ACTIVE_CONNECTIONS, 10);
        try {
            connPool = new org.apache.commons.dbcp.BasicDataSource();
            connPool.setUrl(jdbcUrl);
            connPool.setUsername(jdbcUser);
            connPool.setPassword(jdbcPass);
            connPool.setDriverClassName(jdbcDriver);
            connPool.setInitialSize(initConnections);
            connPool.setMaxActive(maxActiveConnections);
            log.info("Connecting to Connection Pool, URL=" + jdbcUrl);
        } catch (Exception ex) {
            log.warn("Cannot connect to JDBC at URL:" + jdbcUrl + ", reason: " + ex.getMessage(), ex);
        }
    }
    /** perform operation with connection*/
    private void withConnection(Function<Connection, Boolean> toDo) {
        try {
            Connection conn = connPool.getConnection();
            toDo.apply(conn);
            conn.close();
        } catch (Exception ex) {
            log.warn("Cannot perform operation with connection, reason: " + ex.getMessage(), ex);
        }
    }
    /** if needed - create SQL agent table */
    private void tryCreateAgentTable() {
        // TODO: if needed - create SQL agent table
        //withConnection(conn -> {
        //    conn.prepareCall(SQL_CREATE_AGENT_TABLE);
        //});
    }
    @Override
    protected boolean onIsConnected() {
        // TODO: return true if there is connection to JDBC database, otherwise false
        return false;
    }
    @Override
    protected AgentConfirmation onAgentRegister(AgentRegister register) {
        // TODO: register this agent in JDBC
        return null;
    }
    @Override
    protected AgentPingResponse onAgentPing(AgentPing ping) {
        // TODO: update refresh date in database
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

    public static final String SQL_CREATE_AGENT_TABLE = "create table agent_register(agentguid varchar(300), hostname varchar(300), hostip varchar(300), portnumber int, lastpingdate timestamp)";

}
