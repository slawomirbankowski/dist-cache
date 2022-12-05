package com.cache.agent.connectors;

import com.cache.agent.AgentInstance;
import com.cache.api.*;
import com.cache.dtos.DistAgentRegisterRow;
import com.cache.dtos.DistCacheTableRow;
import com.cache.base.DaoBase;
import com.cache.base.RegistrationBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * TODO: implement global agent registration in JDBC database
 * */
public class RegistrationJdbc extends RegistrationBase {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(RegistrationJdbc.class);

    private DaoBase dao;
    private JdbcDialect dialect;

    public RegistrationJdbc(AgentInstance parentAgent) {
        super(parentAgent);
    }
    /** run for initialization in classes */
    @Override
    public void onInitialize() {
        var jdbcUrl = parentAgent.getConfig().getProperty(DistConfig.JDBC_URL);
        var jdbcDriver = parentAgent.getConfig().getProperty(DistConfig.JDBC_DRIVER, "");
        var jdbcUser = parentAgent.getConfig().getProperty(DistConfig.JDBC_USER, "");
        var jdbcPass = parentAgent.getConfig().getProperty(DistConfig.JDBC_PASS, "");
        var jdbcDialect = parentAgent.getConfig().getProperty(DistConfig.JDBC_DIALECT, "");
        dialect = JdbcDialect.getDialect(jdbcDriver, jdbcDialect);
        var initConnections = parentAgent.getConfig().getPropertyAsInt(DistConfig.JDBC_INIT_CONNECTIONS, 2);
        var maxActiveConnections = parentAgent.getConfig().getPropertyAsInt(DistConfig.JDBC_MAX_ACTIVE_CONNECTIONS, 10);
        dao = new DaoBase(jdbcUrl, jdbcDriver, jdbcUser, jdbcPass, initConnections, maxActiveConnections);
        log.info("Initialize JDBC registration with URL: " + jdbcUrl + ", dialect: " + dialect.dialectName);
        tryCreateAgentTable();
    }

    /** if needed - create SQL agent table */
    private void tryCreateAgentTable() {
        LinkedList<DistCacheTableRow> rows = dao.executeSelectQuery(dialect.selectAgentRegisterTable, new Object[0], x -> DistCacheTableRow.fromMap(x));
        if (rows.size() == 0) {
            log.info("Try to create agent registering table in JDBC, dialect: " + dialect.dialectName);
            dao.executeAnyQuery(dialect.createAgentRegister);
        }
    }
    @Override
    protected boolean onIsConnected() {
        // return true if there is connection to JDBC database, otherwise falsedao
        return dao.isConnected();
    }
    @Override
    protected AgentConfirmation onAgentRegister(AgentRegister register) {
        // register this agent in JDBC
        log.info("Registering new agent in JDBC, GUID: " + this.parentAgent.getAgentGuid() + ", dialect: " + dialect.dialectName);
        var createdDate = new java.util.Date();
        dao.executeAnyQuery(dialect.insertAgentRegister, new Object[] {register.agentGuid, register.hostName, register.hostIp, register.port, createdDate, createdDate, 0, 1});

        return new AgentConfirmation(register.agentGuid, true, false, 0, List.of());
    }
    protected AgentConfirmation onAgentUnregister(String agentGuid) {
        log.info("Unregistering agent: " + agentGuid);
        dao.executeAnyQuery(dialect.removeAgentRegister, new Object[] {new java.util.Date(), agentGuid});
        return new AgentConfirmation(agentGuid, true, false, 0, List.of());
    }

    @Override
    protected AgentPingResponse onAgentPing(AgentPing ping) {
        log.info("====> PINGing agent in JDBC, guid: " + ping.agentGuid);
        dao.executeUpdateQuery(dialect.pingAgentRegister, new Object[] {new java.util.Date(), ping.agentGuid});
        dao.executeUpdateQuery(dialect.checkAgentRegisters, new Object[0]);
        return new AgentPingResponse();
    }
    /** get list of agents from JDBC table */
    @Override
    protected List<AgentSimplified> onGetAgents() {
        return dao.executeSelectQuery(dialect.selectAgentRegisters, new Object[0], x -> DistAgentRegisterRow.fromMap(x).toSimplified());
    }
    /** get list of active agents from JDBC table */
    public List<AgentSimplified> getAgentsActive() {
        return dao.executeSelectQuery(dialect.selectActiveAgentRegisters, new Object[0], x -> DistAgentRegisterRow.fromMap(x).toSimplified());
    }


    /** close current connector */
    @Override
    protected void onClose() {
        dao.executeSelectQuery(dialect.removeAgentRegister, new Object[0]);
        // TODO: implement closing this connector
    }

    public static final String SQL_CREATE_AGENT_TABLE = "create table cache_agent_register(agentguid varchar(300), hostname varchar(300), hostip varchar(300), portnumber int, lastpingdate timestamp, isactive int)";

}
