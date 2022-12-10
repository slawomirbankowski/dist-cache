package com.cache.jdbc;

public enum DialectQueries {
    dialectName,
    selectAllTables,
    selectTable,
    selectCacheTables,
    createDistCacheItemTable,
    createCacheItemIndex,
    selectFindCacheItems,
    selectCacheItemsByKey,
    insertUpdateCacheItem,
    deleteOldCacheItemsTemplate,

    selectAgentRegisterTable,
    createAgentRegister,
    createAgentRegisterIndex,
    selectAgentRegisters,
    selectActiveAgentRegisters,
    updateAgentRegister,
    pingAgentRegister,
    insertAgentRegister,
    removeAgentRegister,
    checkAgentRegisters,
    createAgentConfig,
    createAgentConfigIndex,
    selectAgentConfig,
    deleteAgentConfig,
    insertAgentConfig,

    createAgentServer,
    createAgentServerIndex,
    selectAgentServers,
    selectAgentServersActive,
    selectAgentServersForAgent,
    insertAgentServer,
    deleteAgentServer,
    createAgentIssue,
    insertAgentIssue,
    selectAgentIssue

}