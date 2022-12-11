package com.cache.interfaces;

import com.cache.base.dtos.DistAgentServerRow;

import java.util.List;

/** interface for agent connectors manager */
public interface AgentConnectors {

    /** open servers for communication  */
    void openServers();
    /** check list of active servers and connect to the server if this is still not connected */
    void checkActiveServers(List<DistAgentServerRow> activeServers);
    /** get count of servers */
    int getServersCount();
    /** get all UIDs of servers */
    List<String> getServerKeys();
    /** get number of clients */
    int getClientsCount();
    /** get client keys */
    List<String> getClientKeys();
    /** register new client created local as part of server */
    void registerLocalClient(AgentClient client);
    /** close this manager */
    void close();

}
