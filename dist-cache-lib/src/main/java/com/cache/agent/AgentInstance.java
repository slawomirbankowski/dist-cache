package com.cache.agent;

import com.cache.agent.connectors.RegistrationApplication;
import com.cache.agent.connectors.RegistrationElasticsearch;
import com.cache.agent.connectors.RegistrationJdbc;
import com.cache.agent.connectors.RegistrationKafka;
import com.cache.agent.servers.AgentServerSocket;
import com.cache.api.*;
import com.cache.base.RegistrationBase;
import com.cache.interfaces.Agent;
import com.cache.interfaces.AgentServer;
import com.cache.interfaces.DistService;
import com.cache.utils.CacheUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/** agent class to be connected to dist-cache applications, Kafka, Elasticsearch or other global agent repository
 * Agent is also connecting directly to other agents */
public class AgentInstance implements Agent {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(AgentInstance.class);
    /** date ant time of creation */
    private final LocalDateTime createDate = LocalDateTime.now();
    /** configuration for agent */
    private DistConfig config;
    /** timer to schedule important check methods */
    private final Timer timer = new Timer();
    /** if agent has been closed */
    private boolean closed = false;
    /** all registered tasks for timer */
    private final LinkedList<TimerTask> timerTasks = new LinkedList<>();
    /** generate secret of this agent to be able to put commands */
    private final String agentSecret = UUID.randomUUID().toString();
    /** GUID of agent */
    private final String agentGuid = CacheUtils.generateAgentGuid();
    /** all registered services for this agent */
    private final HashMap<String, DistService> services = new HashMap<>();
    /** all servers for connections to other agents */
    private final HashMap<String, AgentServer> servers = new HashMap<>();
    /* allows to perform ping, get current list of agents and unregister agent */
    private final HashMap<String, RegistrationBase> registrations = new HashMap<>();
    /** default working port for Socket server */
    private int workingPort = -1;
    /** all connected agents */
    private final HashMap<String, AgentSimplified> connectedAgents = new HashMap<>();
    /** callbacks - methods to be called when given event is happening
     * only one callback per event type is allowed */
    protected HashMap<String, Function<CacheEvent, String>> callbacks = new HashMap<>();
    /** queue of issues reported when using cache */
    protected final Queue<CacheIssue> issues = new LinkedList<>();
    /** queue of events that would be added to callback methods */
    protected final Queue<CacheEvent> events = new LinkedList<>();

    /** create new agent */
    public AgentInstance(DistConfig config, Map<String, Function<CacheEvent, String>> callbacksMethods) {
        this.config = config;
        callbacksMethods.entrySet().stream().forEach(cb -> callbacks.put(cb.getKey(), cb.getValue()));
    }
    /** initialize agent - server, application, jdbc, kafka */
    public void initializeAgent() {
        log.info("Initializing agent for guid: " + agentGuid);
        workingPort = config.getPropertyAsInt(DistConfig.AGENT_SOCKET_PORT, DistConfig.AGENT_SOCKET_PORT_DEFAULT_VALUE);
        openServers();
        createRegistrations();
        registerToAll();
        setUpTimerToCommunicate();
    }
    /** get configuration for this agent */
    public DistConfig getConfig() { return config; }
    /** get unique ID of this agent */
    public String getAgentGuid() { return agentGuid; }

    /** return all services assigned to this agent */
    public List<DistService> getServices() {
        return services.values().stream().collect(Collectors.toList());
    }
    /** returns true if agent has been already closed */
    public boolean isClosed() {
        return closed;
    }
    /** get list of connected agents */
    public List<AgentSimplified> getAgents() {
        return connectedAgents.values().stream().collect(Collectors.toList());
    }

    /** get secret generated or set for this agent */
    public String getAgentSecret() {
        return agentSecret;
    }
    /** get date and time of creating this agent */
    public LocalDateTime getCreateDate() {
        return createDate;
    }

    /** get working port of SocketServer to have connections from other agents */
    public int getWorkingPort() {
        return workingPort;
    }

    /** register service to this agent */
    public void registerService(DistService service) {
        // TODO: register new service like cache, report, measure, ...
        synchronized (services) {
            services.put(service.toString(), service);
        }
    }
    /** run by parent cache every 1 minute */
    public void onTimeCommunicate() {
        try {
            log.info("Agent - Ping registration objects!!!!!!!");
            registrations.entrySet().stream().forEach(e -> {
                e.getValue().agentPing(new AgentPing(this.agentGuid));
            });
            log.info("Agent - check connected agents, current count: " + connectedAgents.size());
            registrations.entrySet().stream().forEach(e -> {
                List<AgentSimplified> activeAgents = e.getValue().getAgentsActive();
                activeAgents.stream().forEach(ag -> {
                    AgentSimplified existingAgent = connectedAgents.get(ag.agentGuid);
                    if (existingAgent != null) {
                        existingAgent.update(ag);
                        log.info("=====---->Existing agent update: " + ag.agentGuid);
                    } else {
                        log.info("=====---->New agent add: " + ag.agentGuid);
                        connectedAgents.put(ag.agentGuid, ag);
                    }
                });
                log.info("=====----> New agents from registration: " + e.getKey() + ", count: " + activeAgents.size());
            });
            // TODO: connect to all nearby agents, check statuses

            // TODO: implement communication of agent with other cache agents

        } catch (Exception ex) {
            log.warn("Cannot communicate with other agents, reason: " + ex.getMessage());
        }
    }

    /** close all items in this agent */
    public void close() {
        log.info("Closing agent: " + agentGuid);
        closed = true;
        log.info("Canceling timers in agent: " +agentGuid);
        timerTasks.forEach(TimerTask::cancel);
        // TODO: close all items for this agent - unregister in application, notify all agents
        unregisterFromAll();
        servers.values().stream().forEach(serv -> {
            serv.close();
        });
        log.info("Clearing events in agent: " +agentGuid);
        events.clear();
        log.info("Clearing issues in agent: " +agentGuid);
        issues.clear();
    }
    /** add issue to cache manager to be revoked by parent
     * issue could be Exception, Error, problem with connecting to storage,
     * internal error, not consistent state that is unknown and could be used by parent manager */
    public void addIssue(CacheIssue issue) {
        synchronized (issues) {
            issues.add(issue);
            while (issues.size() > config.getPropertyAsLong(DistConfig.CACHE_ISSUES_MAX_COUNT, DistConfig.CACHE_ISSUES_MAX_COUNT_VALUE)) {
                issues.poll();
            }
        }
    }
    /** add issue with method and exception */
    public void addIssue(String methodName, Exception ex) {
        addIssue(new CacheIssue(this, methodName, ex));
    }
    /** add new event and distribute it to callback methods,
     * event could be useful information about change of cache status, new connection, refresh of cache, clean */
    public void addEvent(CacheEvent event) {
        synchronized (events) {
            events.add(event);
            while (events.size() > config.getPropertyAsLong(DistConfig.CACHE_EVENTS_MAX_COUNT, DistConfig.CACHE_EVENTS_MAX_COUNT_VALUE)) {
                events.poll();
            }
        }
        Function<CacheEvent, String> callback = callbacks.get(event.getEventType());
        if (callback != null) {
            try {
                callback.apply(event);
            } catch (Exception ex) {
                log.warn("Exception while running callback for event " + event.getEventType());
            }
        }
    }
    /** set new callback method for events for given type */
    public void setCallback(String eventType, Function<CacheEvent, String> callback) {
        log.info("Set callback method for events" + eventType);
        callbacks.put(eventType, callback);
    }
    /** get all recent issues with cache */
    public Queue<CacheIssue> getIssues() {
        return issues;
    }
    /** get all recent events added to cache */
    public Queue<CacheEvent> getEvents() {
        return events;
    }

    /** open socket server  */
    private void openServers() {
        if (config.hasProperty(DistConfig.AGENT_SOCKET_PORT)) {
            int portNum = config.getPropertyAsInt(DistConfig.AGENT_SOCKET_PORT, DistConfig.AGENT_SOCKET_PORT_VALUE_SEQ.incrementAndGet());
            log.info("SERVER SOCKET opening at port: " + portNum + " for agent: " + agentGuid);
            AgentServerSocket serv = new AgentServerSocket(this);
            serv.initializeServer(portNum);
            servers.put(serv.getServerGuid(), serv);
        }
    }
    private void createSocketServer() {

    }
    private void createRegistrations() {
        if (config.hasProperty(DistConfig.CACHE_APPLICATION_URL)) {
            createAndAddRegistrations(RegistrationApplication.class.getName());
        }
        if (config.hasProperty(DistConfig.JDBC_URL)) {
            createAndAddRegistrations(RegistrationJdbc.class.getName());
        }
        if (config.hasProperty(DistConfig.KAFKA_BROKERS)) {
            createAndAddRegistrations(RegistrationKafka.class.getName());
        }
        if (config.hasProperty(DistConfig.ELASTICSEARCH_URL)) {
            createAndAddRegistrations(RegistrationElasticsearch.class.getName());
        }
    }
    /** create global connector and save in connectors */
    private void createAndAddRegistrations(String className) {
        synchronized (registrations) {
            createRegistrationForClass(className).stream().forEach(reg -> {
                registrations.put(reg.toString(), reg);
            });
        }
    }

    /** create registration base object for current instance and given class */
    private Optional<RegistrationBase> createRegistrationForClass(String className) {
        try {
            log.info("Try to create registration for class: " + className);
            RegistrationBase registr = (RegistrationBase)Class.forName(className)
                    .getConstructor(AgentInstance.class)
                    .newInstance(this);
            return Optional.of(registr);
        } catch (Exception ex) {
            log.info("Cannot create new registration object for class: " + className + ", reason: " + ex.getMessage());
            return Optional.empty();
        }
    }
    /** register this agent to all available connectors */
    private void registerToAll() {
        log.info("Registering agent to all registration objects, count: " + registrations.size());
        var agents = getAgents();
        AgentRegister register = new AgentRegister(agentGuid, getAgentSecret(),
                CacheUtils.getCurrentHostName(), CacheUtils.getCurrentHostAddress(),
                getWorkingPort(), getCreateDate(), agents);
        try {
            synchronized (registrations) {
                log.info("Registering this agent " + agentGuid + " on host " + register.hostName + " to all registrations: " + registrations.size());
                registrations.values().stream().forEach(c -> {
                    c.agentRegister(register);
                });
            }
        } catch (Exception ex) {
            log.info("Cannot register agents to connectors, reason: " + ex.getMessage());
        }
    }

    private void unregisterFromAll() {
        try {
            synchronized (registrations) {
                log.info("Unregistering this agent " +  agentGuid + " to all registrations: " + registrations.size());
                registrations.values().stream().forEach(reg -> {
                    reg.agentUnregister();
                });
            }
        } catch (Exception ex) {
            log.warn("Cannot register agents to connectors, reason: " + ex.getMessage());
        }
    }
    private void setUpTimerToCommunicate() {
        // initialization for communicate
        long communicateDelayMs = config.getPropertyAsLong(DistConfig.TIMER_COMMUNICATE_DELAY, DistConfig.TIMER_COMMUNICATE_DELAY_VALUE);
        long communicatePeriodMs = config.getPropertyAsLong(DistConfig.TIMER_COMMUNICATE_DELAY, DistConfig.TIMER_COMMUNICATE_DELAY_VALUE);
        log.info("Scheduling communicating timer task for agent: " + getAgentGuid());
        TimerTask onTimeCommunicateTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    onTimeCommunicate();
                } catch (Exception ex) {
                    // TODO: mark exception
                    //parentCache.addIssue("initializeTimer:communicate", ex);
                }
            }
        };
        timerTasks.add(onTimeCommunicateTask);
        timer.scheduleAtFixedRate(onTimeCommunicateTask, communicateDelayMs, communicatePeriodMs);
    }

}
