package com.cache.agent.registrations;

import com.cache.agent.AgentInstance;
import com.cache.api.*;
import com.cache.base.dtos.DistAgentRegisterRow;
import com.cache.base.dtos.DistAgentServerRow;
import com.cache.base.DaoBase;
import com.cache.base.RegistrationBase;
import com.cache.jdbc.JdbcDialect;
import com.cache.jdbc.JdbcTables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        var agentRegisterTable = dao.executeSelectQuery(dialect.selectTable(), new Object[] { JdbcTables.distagentregister.name() });
        if (agentRegisterTable.size() == 0) {
            log.info("Try to create agent registering table in JDBC, dialect: " + dialect.dialectName);
            dao.executeAnyQuery(dialect.createAgentRegister());
            dao.executeAnyQuery(dialect.createAgentRegisterIndex());
        }
        var agentServerTable = dao.executeSelectQuery(dialect.selectTable(), new Object[] {JdbcTables.distagentserver.name() });
        if (agentServerTable.size() == 0) {
            log.info("Try to create distagentserver table in JDBC, dialect: " + dialect.dialectName);
            dao.executeAnyQuery(dialect.createAgentServer());
            dao.executeAnyQuery(dialect.createAgentServerIndex());
        }
        var agentConfigTable = dao.executeSelectQuery(dialect.selectTable(), new Object[] {JdbcTables.distagentconfig.name() });
        if (agentConfigTable.size() == 0) {
            log.info("Try to create distagentconfig table in JDBC, dialect: " + dialect.dialectName);
            dao.executeAnyQuery(dialect.createAgentConfig());
            dao.executeAnyQuery(dialect.createAgentConfigIndex());
        }
        var agentIssueTable = dao.executeSelectQuery(dialect.selectTable(), new Object[] {JdbcTables.distagentissue.name() });
        if (agentIssueTable.size() == 0) {
            log.info("Try to create distagentissue table in JDBC, dialect: " + dialect.dialectName);
            dao.executeAnyQuery(dialect.createAgentIssue());
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
        dao.executeAnyQuery(dialect.insertAgentRegister(), new Object[] {register.agentGuid, register.hostName, register.hostIp, register.port, createdDate, createdDate, 0, 1});
        parentAgent.getConfig().getHashMap().entrySet().stream().forEach(cfg -> {
            // agentguid,configname,configvalue,createddate,lastupdateddat
            var createDate = new java.util.Date();
            dao.executeUpdateQuery(dialect.insertAgentConfig(), new Object[] { register.agentGuid, cfg.getKey(), cfg.getValue(), createDate, createDate });
        });
        return new AgentConfirmation(register.agentGuid, true, false, 0, List.of());
    }

    protected AgentConfirmation onAgentUnregister(String agentGuid) {
        log.info("Unregistering agent in JDBC: " + agentGuid);
        dao.executeAnyQuery(dialect.removeAgentRegister(), new Object[] {new java.util.Date(), agentGuid});
        return new AgentConfirmation(agentGuid, true, false, 0, List.of());
    }

    @Override
    protected AgentPingResponse onAgentPing(AgentPing ping) {
        log.info("====> PINGing agent in JDBC, guid: " + ping.agentGuid);
        dao.executeUpdateQuery(dialect.pingAgentRegister(), new Object[] {new java.util.Date(), ping.agentGuid});
        dao.executeUpdateQuery(dialect.checkAgentRegisters(), new Object[0]);
        return new AgentPingResponse();
    }
    /** add issue for registration */
    public void addIssue(DistIssue issue) {
        try {
            dao.executeUpdateQuery(dialect.insertAgentIssue(), new Object[] { parentAgent.getAgentGuid(), issue.getMethodName(), issue.getExceptionMessage(), "", "", new java.util.Date() });
        } catch (Exception ex) {
            log.warn("Cannot register issue at JDBC, reason: " + ex.getMessage());
        }
    }
    /** register server for communication */
    public void addServer(DistAgentServerRow serv) {
        try {
            log.info("Registering server in JDBC, dialect: " + dialect.dialectName + ", server: " + serv.serverguid);
            // distagentserver(agentguid text, servertype text, serverhost text, serverip text, serverport int, serverurl text, createddate timestamp, isactive int, lastpingdate timestamp)
            dao.executeUpdateQuery(dialect.insertAgentServer(),
                    new Object[] { serv.agentguid, serv.serverguid, serv.servertype, serv.serverhost, serv.serverip,
                    serv.serverport, serv.serverurl, serv.createddate, serv.isactive, serv.lastpingdate });
        } catch (Exception ex) {
            log.warn("Cannot register server at JDBC, reason: " + ex.getMessage(), ex);
        }
    }
    /** unregister server for communication */
    public void unregisterServer(DistAgentServerRow serv) {
        try {
            log.info("Unregistering server in JDBC, dialect: " + dialect.dialectName + ", server: " + serv.serverguid);
            dao.executeUpdateQuery(dialect.deleteAgentServer(),
                    new Object[] { serv.agentguid, serv.serverguid });
        } catch (Exception ex) {
            log.warn("Cannot unregister server at JDBC, reason: " + ex.getMessage(), ex);
        }
    }
    /** get all communication servers */
    public  List<DistAgentServerRow> getServers() {
        return dao.executeSelectQuery(dialect.selectAgentServersActive(), new Object[0], x -> DistAgentServerRow.fromMap(x));
    }
    /** get agents from registration services */
    public  List<DistAgentRegisterRow> getAgentsNow() {
        return dao.executeSelectQuery(dialect.selectAgentServersActive(), new Object[0], x -> DistAgentRegisterRow.fromMap(x));
    }

    /** get list of agents from JDBC table */
    @Override
    protected List<AgentSimplified> onGetAgents() {
        return dao.executeSelectQuery(dialect.selectAgentRegisters(), new Object[0], x -> DistAgentRegisterRow.fromMap(x).toSimplified());
    }
    /** get list of active agents from JDBC table */
    public List<AgentSimplified> getAgentsActive() {
        return dao.executeSelectQuery(dialect.selectActiveAgentRegisters(), new Object[0], x -> DistAgentRegisterRow.fromMap(x).toSimplified());
    }

    /** close current connector */
    @Override
    protected void onClose() {
        log.info("Closing JDBC registration object, unregistering agent");
        dao.executeSelectQuery(dialect.removeAgentRegister(), new Object[0]);
    }

}
