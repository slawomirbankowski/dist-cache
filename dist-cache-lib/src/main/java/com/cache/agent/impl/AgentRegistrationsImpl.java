package com.cache.agent.impl;

import com.cache.agent.AgentInstance;
import com.cache.agent.registrations.RegistrationApplication;
import com.cache.agent.registrations.RegistrationElasticsearch;
import com.cache.agent.registrations.RegistrationJdbc;
import com.cache.agent.registrations.RegistrationKafka;
import com.cache.api.*;
import com.cache.base.RegistrationBase;
import com.cache.base.dtos.DistAgentRegisterRow;
import com.cache.base.dtos.DistAgentServerRow;
import com.cache.interfaces.AgentRegistrations;
import com.cache.utils.CacheUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class AgentRegistrationsImpl implements AgentRegistrations {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(AgentRegistrationsImpl.class);
    /** parent agent for this registrations manager */
    private final AgentInstance parentAgent;

    /* allows to perform ping, get current list of agents and unregister agent */
    private final java.util.concurrent.ConcurrentHashMap<String, RegistrationBase> registrations = new java.util.concurrent.ConcurrentHashMap<>();
    /** all connected agents */
    private final java.util.concurrent.ConcurrentHashMap<String, AgentSimplified> connectedAgents = new java.util.concurrent.ConcurrentHashMap<>();
    /** all rows of registered servers */
    private final LinkedList<DistAgentServerRow> registeredServers = new LinkedList<>();

    public AgentRegistrationsImpl(AgentInstance parentAgent) {
        this.parentAgent = parentAgent;
    }

    /** create initial registration services
     * there are services to register agent, servers, issues, configurations */
    public void createRegistrations() {
        if (parentAgent.getConfig().hasProperty(DistConfig.CACHE_APPLICATION_URL)) {
            createAndAddRegistrations(RegistrationApplication.class.getName());
        }
        if (parentAgent.getConfig().hasProperty(DistConfig.JDBC_URL)) {
            createAndAddRegistrations(RegistrationJdbc.class.getName());
        }
        if (parentAgent.getConfig().hasProperty(DistConfig.KAFKA_BROKERS)) {
            createAndAddRegistrations(RegistrationKafka.class.getName());
        }
        if (parentAgent.getConfig().hasProperty(DistConfig.ELASTICSEARCH_URL)) {
            createAndAddRegistrations(RegistrationElasticsearch.class.getName());
        }
        log.info("Registered agent " + parentAgent.getAgentGuid() + " to all registration services, count: " + registrations.size());
        long communicateDelayMs = parentAgent.getConfig().getPropertyAsLong(DistConfig.TIMER_COMMUNICATE_DELAY, DistConfig.TIMER_COMMUNICATE_DELAY_VALUE);
        long communicatePeriodMs = parentAgent.getConfig().getPropertyAsLong(DistConfig.TIMER_COMMUNICATE_DELAY, DistConfig.TIMER_COMMUNICATE_DELAY_VALUE);
        registerToAll();
        parentAgent.getAgentTimers().setUpTimer(communicateDelayMs, communicatePeriodMs, x -> onTimeCommunicate());
    }
    /** get number of registration */
    public int getRegistrationsCount() {
        return registrations.size();
    }
    /** get UIDs for registration services */
    public List<String> getRegistrationKeys() {
        return registrations.values().stream().map(x -> x.getRegisterGuid()).collect(Collectors.toList());
    }
    /** get list of connected agents */
    public List<DistAgentRegisterRow> getAgentsNow() {
        return registrations.values().stream().flatMap(x -> x.getAgentsNow().stream()).collect(Collectors.toList());
    }
    /** get number of known agents */
    public int getAgentsCount() {
        return connectedAgents.size();
    }

    /** register server for ROW */
    public void registerServer(DistAgentServerRow servDto) {
        log.info("Registering server for GUID: " + servDto.serverguid + ", server type: " + servDto.servertype + ", servers: " + registeredServers.size() + ", registrations: " + registrations.size());
        registrations.values().stream().forEach(reg -> reg.addServer(servDto));
        registeredServers.add(servDto);
    }
    /** get all servers from registration services */
    public List<DistAgentServerRow> getServers() {
        return registrations.values().stream().flatMap(reg -> reg.getServers().stream()).collect(Collectors.toList());
    }

    /** get list of agents possible to connect */
    public List<AgentSimplified> getAgents() {
        return connectedAgents.values().stream().collect(Collectors.toList());
    }

    /** create global connector and save in connectors */
    private void createAndAddRegistrations(String className) {
        synchronized (registrations) {
            createRegistrationForClass(className).stream().forEach(reg -> {
                registrations.put(reg.getRegisterGuid(), reg);
            });
        }
    }
    /** create registration base object for current instance and given class */
    private Optional<RegistrationBase> createRegistrationForClass(String className) {
        try {
            log.info("Try to create registration for class: " + className);
            RegistrationBase registr = (RegistrationBase)Class.forName(className)
                    .getConstructor(AgentInstance.class)
                    .newInstance(parentAgent);
            return Optional.of(registr);
        } catch (Exception ex) {
            log.warn("Cannot create new registration object for class: " + className + ", reason: " + ex.getMessage(), ex);
            return Optional.empty();
        }
    }
    /** run by agent every 1 minute */
    public boolean onTimeCommunicate() {
        try {
            pingAllRegistrations();
            checkActiveAgents();
            checkServers();
            // TODO: connect to all nearby agents, check statuses
            // TODO: implement communication of agent with other cache agents
            log.info("=====----> AGENT REGISTRATION summary for guid: " + parentAgent.getAgentGuid() + ", registrations: " + registrations.size() + ", connected agents: " + connectedAgents.size() + ", registeredServers: " + registeredServers.size());
            return true;
        } catch (Exception ex) {
            log.warn("Cannot communicate with other agents, reason: " + ex.getMessage(), ex);
            return false;
        }
    }
    /** add issue to registrations */
    public void addIssue(DistIssue issue) {
        registrations.values().stream().forEach(reg -> reg.addIssue(issue));
    }
    /** close  */
    public void close() {
        unregisterFromAll();
    }


    /** ping all registrations to notify that this agent is still working */
    private void pingAllRegistrations() {
        log.info("Agent - Ping registration objects, registrations: " + registrations.size());
        registrations.entrySet().stream().forEach(e -> {
            e.getValue().agentPing(new AgentPing(parentAgent.getAgentGuid()));
        });
    }
    /** check all active agents from all registrations */
    private void checkActiveAgents() {
        log.info("********************>>> Check connected agents for agent: " + parentAgent.getAgentGuid() + ", current count: " + connectedAgents.size() + ", registrations: " + registrations.size() + ", registeredServers: " + registeredServers.size());
        registrations.entrySet().stream().forEach(e -> {
            List<AgentSimplified> activeAgents = e.getValue().getAgentsActive();
            log.info("********************>>> Get agents for register, agent: " + parentAgent.getAgentGuid() + ", registration: " + e.getKey() +", activeAgents: " + activeAgents.size());
            activeAgents.stream().forEach(ag -> {
                AgentSimplified existingAgent = connectedAgents.get(ag.agentGuid);
                if (existingAgent != null) {
                    existingAgent.update(ag);
                    log.info("********************>>> Existing client for agent: " + parentAgent.getAgentGuid() + ", update: " + ag.agentGuid);
                } else {
                    log.info("****************>>> Adding new client for agent: " + parentAgent.getAgentGuid() + ", connected to: " + ag.agentGuid);
                    connectedAgents.put(ag.agentGuid, ag);
                }
            });
        });
        log.info("********************>>> AFTER check connected agents for agent: " + parentAgent.getAgentGuid() + ", current count: " + connectedAgents.size() + ", registrations: " + registrations.size() + ", registeredServers: " + registeredServers.size());
    }
    /** check servers */
    private void checkServers() {
        List<DistAgentServerRow> servers = registrations.entrySet().stream().flatMap(e -> e.getValue().getServers().stream()).collect(Collectors.toList());
        log.info("Check servers for agent: " + parentAgent.getAgentGuid() + ", connectedAgents: " + connectedAgents.size() + ", servers from registrations: " + servers.size());
        parentAgent.getAgentConnectors().checkActiveServers(servers);
    }
    /** register this agent to all available connectors
     * this is registering agent itself,  */
    private void registerToAll() {
        log.info("Registering agent: " + parentAgent.getAgentGuid() + " to all registration objects, registrations: " + registrations.size());
        var agents = getAgents();
        AgentRegister register = new AgentRegister(parentAgent.getAgentGuid(), parentAgent.getAgentSecret(),
                CacheUtils.getCurrentHostName(), CacheUtils.getCurrentHostAddress(),
                9900, parentAgent.getCreateDate(), agents);
        try {
            synchronized (registrations) {
                log.info("Registering this agent " + parentAgent.getAgentGuid() + " on host " + register.hostName + " to all registrations: " + registrations.size());
                registrations.values().stream().forEach(c -> {
                    c.agentRegister(register);
                });
            }
        } catch (Exception ex) {
            log.warn("Cannot register agent " + parentAgent.getAgentGuid() + " to connectors, reason: " + ex.getMessage(), ex);
        }
    }

    /** unregister agent and all servers from all registration services */
    private void unregisterFromAll() {
        try {
            synchronized (registrations) {
                log.info("Unregistering this agent: " +  parentAgent.getAgentGuid() + " from all registrations: " + registrations.size());
                registrations.values().stream().forEach(reg -> {
                    reg.agentUnregister();
                    registeredServers.stream().forEach(srv -> {
                        reg.unregisterServer(srv);
                    });
                });
            }
        } catch (Exception ex) {
            log.warn("Cannot register agents to connectors, reason: " + ex.getMessage(), ex);
        }
    }

}
