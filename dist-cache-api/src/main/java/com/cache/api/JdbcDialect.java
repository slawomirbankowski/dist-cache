package com.cache.api;

import java.util.HashMap;
import java.util.Map;

/** dialects for different databases like Postgres, MySql, MS SQL Server, Oracle, IBM DB2 */
public class JdbcDialect {

    public String dialectName;
    public String selectAllTables;
    public String selectCacheTables;
    public String createDistCacheItemTable;
    public String ddlCreateCacheItemIndex;
    public String selectFindCacheItems;
    public String selectCacheItemsByKey;
    public String insertUpdateCacheItem;
    public String deleteOldCacheItemsTemplate = "delete from distcacheitem where cachekey in (???)";

    public String selectAgentRegisterTable;
    public String createAgentRegister;
    public String createAgentRegisterIndex;
    public String selectAgentRegisters;
    public String selectActiveAgentRegisters;
    public String updateAgentRegister;
    public String pingAgentRegister;
    public String insertAgentRegister;
    public String removeAgentRegister;
    public String checkAgentRegisters;

    public JdbcDialect(String dialectName, String selectAllTables, String selectCacheTables,
                       String createDistCacheItemTable, String ddlCreateCacheItemIndex,
                       String selectFindCacheItems, String ddlSelectCacheItemByKey, String insertUpdateCacheItem,
                       String deleteOldCacheItemsTemplate,
                       String selectAgentRegisterTable,
                       String createAgentRegister, String createAgentRegisterIndex, String selectAgentRegisters, String selectActiveAgentRegisters,
                       String updateAgentRegister, String pingAgentRegister,
                       String insertAgentRegister, String removeAgentRegister, String checkAgentRegisters) {
        this.dialectName = dialectName;
        this.selectAllTables = selectAllTables;
        this.selectCacheTables = selectCacheTables;
        this.createDistCacheItemTable = createDistCacheItemTable;
        this.ddlCreateCacheItemIndex = ddlCreateCacheItemIndex;
        this.selectFindCacheItems = selectFindCacheItems;
        this.selectCacheItemsByKey = ddlSelectCacheItemByKey;
        this.insertUpdateCacheItem = insertUpdateCacheItem;
        this.deleteOldCacheItemsTemplate = deleteOldCacheItemsTemplate;
        this.selectAgentRegisterTable = selectAgentRegisterTable;
        this.createAgentRegister = createAgentRegister;
        this.createAgentRegisterIndex = createAgentRegisterIndex;
        this.selectAgentRegisters = selectAgentRegisters;
        this.selectActiveAgentRegisters = selectActiveAgentRegisters;
        this.updateAgentRegister = updateAgentRegister;
        this.pingAgentRegister = pingAgentRegister;
        this.insertAgentRegister = insertAgentRegister;
        this.removeAgentRegister = removeAgentRegister;
        this.checkAgentRegisters = checkAgentRegisters;
    }


    public static final JdbcDialect dialectDefault = new JdbcDialect(
            "default",
            "select * from information_schema.tables",
            "select * from information_schema.tables where table_name = 'distcacheitem'",
            "create table distcacheitem(\n" +
                    "cachekey varchar(4000), \n" +
                    "cachevalue text, \n" +
                    "objectclassname text, \n" +
                    "inserteddate timestamp default (now()), \n" +
                    "cacheguid text, \n" +
                    "lastusedate timestamp default (now()),\n" +
                    "enddate timestamp default (now()),\n" +
                    "createdtimems bigint,\n" +
                    "objectseq bigint,\n" +
                    "objsize bigint,\n" +
                    "acquiretimems bigint,\n" +
                    "cachemode int,\n" +
                    "cachepriority int,\n" +
                    "groupslist text\n" +
                    ")",
            "create unique index idx_distcacheitem_cachekey on distcacheitem(cachekey)",
            "select * from distcacheitem tables where cachekey like ?",
            "select * from distcacheitem tables where cachekey=? limit 1",
            "insert into distcacheitem(cachekey, cachevalue, objectclassname, inserteddate, cacheguid, lastusedate) values ('key1', 'value1', now(), 'GUID', now()) on conflict (cachekey) do update set cachevalue = EXCLUDED.cachevalue, lastUseDate=EXCLUDED.lastUseDate",
            "delete from distcacheitem where cachekey in (???)",
            "select * from information_schema.tables where table_name = 'distagentregister'",
            "create table distagentregister(agentguid varchar(300), hostname varchar(300), hostip varchar(300), portnumber int, createddate timestamp, lastpingdate timestamp, pingscount int, isactive int, closedate timestamp)",
            "select * from information_Schema.tables where table_name = 'distagentregister'",
            "",
            "select * from distagentregister where isactive=1",
            "update distagentregister set lastpingdate=? where agentguid=?",
            "update distagentregister set lastpingdate=?, pingscount=pingscount+1 where agentguid=?",
            "insert into distagentregister(agentguid, hostname, hostip, portnumber, createddate, lastpingdate, pingscount, isactive) values (?,?,?,?,?,?,?,?)",
            "update distagentregister set isactive=0, closedate=? where agentguid=?",
            "update distagentregister set isactive=0 where isactive = 1 and lastpingdate < now() - interval '10 minutes'"
    );
    // cachekey, cachevalue, objectclassname, inserteddate, cacheguid, lastusedate, enddate,createdtimems,objectseq,objsize,acquiretimems,cachemode,cachepriority,groupslist
    public static final JdbcDialect dialectPostgres = new JdbcDialect(
            "postgres",
            "select * from information_schema.tables",
            "select * from information_schema.tables where table_name = 'distcacheitem'",
            "create table distcacheitem(\n" +
                    "cachekey varchar(4000), \n" +
                    "cachevalue text, \n" +
                    "objectclassname text, \n" +
                    "inserteddate timestamp default (now()), \n" +
                    "cacheguid text, \n" +
                    "lastusedate timestamp default (now()),\n" +
                    "enddate timestamp default (now()),\n" +
                    "createdtimems bigint,\n" +
                    "objectseq bigint,\n" +
                    "objsize bigint,\n" +
                    "acquiretimems bigint,\n" +
                    "cachemode int,\n" +
                    "cachepriority int,\n" +
                    "groupslist text\n" +
                    ")",
            "create unique index idx_distcacheitem_cachekey on distcacheitem(cachekey)",
            "select * from distcacheitem tables where cachekey like ?",
            "select * from distcacheitem tables where cachekey=? limit 1",
            "insert into distcacheitem(cachekey, cachevalue, objectclassname, inserteddate, cacheguid, lastusedate, enddate,createdtimems,objectseq,objsize,acquiretimems,cachemode,cachepriority,groupslist) " +
                    "values (?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?) " +
                    "on conflict (cachekey) do update " +
                    "set cachevalue = EXCLUDED.cachevalue, lastUseDate=EXCLUDED.lastUseDate, objectclassname=EXCLUDED.objectclassname, enddate=EXCLUDED.enddate",
            "delete from distcacheitem where cachekey in (???)",
            "select * from information_schema.tables where table_name = 'distagentregister'",
            "create table distagentregister(agentguid varchar(300), hostname text, hostip text, portnumber int, lastpingdate timestamp, isactive int)",
            "create unique index idx_distagentregister_cachekey on distagentregister(agentguid)",
            "select * from distagentregister",
            "select * from distagentregister where isactive=1",
            "",
            "update distagentregister set lastpingdate=?, pingscount=pingscount+1 where agentguid=?",
            "insert into distagentregister(agentguid, hostname, hostip, portnumber, createddate, lastpingdate, pingscount, isactive) values (?,?,?,?,?,?,?,?)",
            "update distagentregister set isactive=0, closedate=? where agentguid=?",
            ""
    );

    public static final JdbcDialect dialectOracle = new JdbcDialect(
            "oracle",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    );

    /** all supported dialects for databases */
    private static final Map<String, JdbcDialect> dialects = createDialectsMap();
    private static Map<String, JdbcDialect> createDialectsMap() {
        HashMap<String, JdbcDialect> map = new HashMap<>();
        map.put("postgres", dialectPostgres);
        map.put("postgresql", dialectPostgres);
        map.put("org.postgresql.driver", dialectPostgres);
        map.put("oracle", dialectOracle);
        map.put("com.oracle.drivermanager", dialectOracle);
        map.put("default", dialectDefault);
        map.put("", dialectDefault);
        return map;
    }

    /** get dialect by dialect name or driver class */
    public static JdbcDialect getDialect(String jdbcDriver, String jdbcDialect) {
        JdbcDialect dialect = dialects.get(jdbcDialect.toLowerCase());
        if (dialect == null) {
            dialect = dialects.get(jdbcDriver.toLowerCase());
        }
        if (dialect == null) {
            dialect = dialectDefault;
        }
        return dialect;
    }

}
