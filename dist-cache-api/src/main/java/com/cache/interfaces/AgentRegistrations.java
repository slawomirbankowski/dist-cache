package com.cache.interfaces;

import com.cache.api.AgentSimplified;
import com.cache.api.DistIssue;
import com.cache.dtos.DistAgentServerRow;

import java.util.List;
import java.util.stream.Collectors;

/** basic interface for registration manager */
public interface AgentRegistrations {

    /** create initial registration services */
    void createRegistrations();
    /** register this server */
    void registerServer(DistAgentServerRow servDto);
    /** get all servers from registration services */
    List<DistAgentServerRow> getServers();
    /** get list of agents possible to connect */
    List<AgentSimplified> getAgents();
    /** add issue to registrations */
    void addIssue(DistIssue issue);
    /** close registrations */
    void close();
}
