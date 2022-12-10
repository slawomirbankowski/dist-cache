package com.cache.interfaces;

import com.cache.dtos.DistAgentServerRow;

import java.util.List;

/** interface for agent connectors manager */
public interface AgentConnectors {

    /** open servers for communication  */
    void openServers();
    /** check list of active servers and connect to the server if this is still not connected */
    void checkActiveServers(List<DistAgentServerRow> activeServers);
    /** close this manager */
    void close();
}
