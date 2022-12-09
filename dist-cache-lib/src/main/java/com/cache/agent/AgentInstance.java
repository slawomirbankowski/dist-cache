package com.cache.agent;

import com.cache.agent.clients.SocketServerClient;
import com.cache.agent.connectors.RegistrationApplication;
import com.cache.agent.connectors.RegistrationElasticsearch;
import com.cache.agent.connectors.RegistrationJdbc;
import com.cache.agent.connectors.RegistrationKafka;
import com.cache.agent.servers.AgentServerSocket;
import com.cache.api.*;
import com.cache.base.RegistrationBase;
import com.cache.dtos.DistAgentRegisterRow;
import com.cache.dtos.DistAgentServerRow;
import com.cache.interfaces.*;
import com.cache.utils.CacheUtils;
import com.cache.utils.HashMapMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/** agent class to be connected to dist-cache applications, Kafka, Elasticsearch or other global agent repository
 * Agent is also connecting directly to other agents */
public class AgentInstance implements Agent, DistService {

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
    /** all rows of registered servers */
    private final LinkedList<DistAgentServerRow> registeredServers = new LinkedList();
    /** all servers for connections to other agents */
    private final HashMap<String, DistAgentServerRow> agentServers = new HashMap<>();
    /** map of map of clients connected to different agents
     * key1 = agentGUID
     * key2 = serverGUID
     * value = client to transfer messages to this agent */
    private final HashMapMap<String, String, AgentClient> serverConnectors = new HashMapMap<>();

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
    protected final Queue<DistIssue> issues = new LinkedList<>();
    /** queue of events that would be added to callback methods */
    protected final Queue<CacheEvent> events = new LinkedList<>();

    /** create new agent */
    public AgentInstance(DistConfig config, Map<String, Function<CacheEvent, String>> callbacksMethods) {
        this.config = config;
        // self register of agent as service
        services.put(DistServiceType.agent.name(), this);
        callbacksMethods.entrySet().stream().forEach(cb -> callbacks.put(cb.getKey(), cb.getValue()));
    }

    /** get type of service: cache, measure, report, flow, space, ... */
    public DistServiceType getServiceType() {
        return DistServiceType.agent;
    }
    /** get unique ID of this service */
    public String getServiceUid() {
        return agentGuid;
    }

    /** initialize agent - server, application, jdbc, kafka */
    public void initializeAgent() {
        log.info("Initializing agent for guid: " + agentGuid);
        workingPort = config.getPropertyAsInt(DistConfig.AGENT_SOCKET_PORT, DistConfig.AGENT_SOCKET_PORT_DEFAULT_VALUE);
        createRegistrations();
        openServers();
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
    /** get list of connected agents */
    public List<DistAgentRegisterRow> getAgentsNow() {
        return registrations.values().stream().flatMap(x -> x.getAgentsNow().stream()).collect(Collectors.toList());
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
    /** run by agent every 1 minute */
    public void onTimeCommunicate() {
        try {
            System.out.println("Agent - Ping registration objects!!!!!!!");
            registrations.entrySet().stream().forEach(e -> {
                e.getValue().agentPing(new AgentPing(this.agentGuid));
            });
            System.out.println("Agent - check connected agents, current count: " + connectedAgents.size());
            registrations.entrySet().stream().forEach(e -> {
                List<AgentSimplified> activeAgents = e.getValue().getAgentsActive();
                System.out.println("Agent - from register: " + e.getKey() +", GOT agents: " + activeAgents.size());
                activeAgents.stream().forEach(ag -> {
                    AgentSimplified existingAgent = connectedAgents.get(ag.agentGuid);
                    if (existingAgent != null) {
                        existingAgent.update(ag);
                        System.out.println("=====---->Existing agent update: " + ag.agentGuid);
                    } else {
                        System.out.println("=====---->New agent add: " + ag.agentGuid);
                        connectedAgents.put(ag.agentGuid, ag);
                    }
                });
                List<DistAgentServerRow> activeServers = e.getValue().getServers();
                for (DistAgentServerRow srv: activeServers) {
                    agentServers.putIfAbsent(srv.serverguid, srv);
                }
                agentServers.values().stream().forEach(srv -> {
                    if (!srv.agentguid.equals(agentGuid)) {
                        Optional<AgentClient> client = serverConnectors.getValue(srv.agentguid, srv.serverguid);
                        if (client.isEmpty()) {
                            System.out.println("=====----> Not yet client to agent: " + srv.agentguid + " to server: " + srv.serverguid + ", creating NEW ONE !!!!!!!!!");
                            var createdClient = createClient(srv);
                            if (createdClient.isPresent()) {
                                serverConnectors.add(srv.agentguid, srv.serverguid, createdClient.get());
                            }
                        }
                    }
                });
                System.out.println("=====----> New agents from registration: " + e.getKey() + ", agents: " + activeAgents.size());
            });
            System.out.println("=====----> AGENT summary gor guid: " + agentGuid + ", registrations: " + registrations.size() + ", connected agents: " + connectedAgents.size() + ", servers: " + agentServers.size());
            // TODO: connect to all nearby agents, check statuses

            // TODO: implement communication of agent with other cache agents

        } catch (Exception ex) {
            log.warn("Cannot communicate with other agents, reason: " + ex.getMessage());
        }
    }

    private Optional<AgentClient> createClient(DistAgentServerRow srv) {
        if (srv.servertype.equals("socket")) {
            return Optional.of(new SocketServerClient(this, srv));
        }
        return Optional.empty();
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
        serverConnectors.getAllValues().stream().forEach(cli -> cli.close());
        log.info("Clearing events in agent: " +agentGuid);
        events.clear();
        log.info("Clearing issues in agent: " +agentGuid);
        issues.clear();
    }
    /** add issue to cache manager to be revoked by parent
     * issue could be Exception, Error, problem with connecting to storage,
     * internal error, not consistent state that is unknown and could be used by parent manager */
    public void addIssue(DistIssue issue) {
        synchronized (issues) {
            issues.add(issue);
            // add issue for registration services
            registrations.values().stream().forEach(reg -> reg.addIssue(issue));
            while (issues.size() > config.getPropertyAsLong(DistConfig.CACHE_ISSUES_MAX_COUNT, DistConfig.CACHE_ISSUES_MAX_COUNT_VALUE)) {
                issues.poll();
            }
        }
    }
    /** add issue with method and exception */
    public void addIssue(String methodName, Exception ex) {
        addIssue(new DistIssue(this, methodName, ex));
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
    /** get all servers from registration services */
    public List<DistAgentServerRow> getServers() {
        return registrations.values().stream().flatMap(reg -> reg.getServers().stream()).collect(Collectors.toList());
    }
    /** get all recent issues with cache */
    public Queue<DistIssue> getIssues() {
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
            // register server for communication
            var createdDate = new java.util.Date();
            var hostName = CacheUtils.getCurrentHostName();
            var hostIp = CacheUtils.getCurrentHostAddress();
            var servDto = new DistAgentServerRow(agentGuid, serv.getServerGuid(), "socket", hostName, hostIp, portNum,
                    "socket://" + hostName + ":" + portNum + "/", createdDate, 1, createdDate);
            System.out.println("Registering server for GUID: " + servDto.serverguid +", registrations: " + registrations.size());
            registrations.values().stream().forEach(reg -> reg.addServer(servDto));
            registeredServers.add(servDto);
        }
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
                    registeredServers.stream().forEach(srv -> {
                        reg.unregisterServer(srv);
                    });

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
    /** receive message from connector or server, need to find service and process that message on service */
    public DistMessageStatus receiveMessage(DistMessage msg) {
        DistService serviceToProcessMessage =  services.get(msg.getService());
        if (serviceToProcessMessage != null) {
            return serviceToProcessMessage.processMessage(msg);
        } else {
            return new DistMessageStatus();
        }
    }
    /** process message by this agent service, choose method and , returns status */
    public DistMessageStatus processMessage(DistMessage msg) {
        // TODO: process message in this agent, there could be many methods to process system agent messages
        return new DistMessageStatus();
    }
    /** message send to agents, directed to services, selected method */
    public DistMessageStatus sendMessage(DistMessage msg) {
        // TODO: check destination by agent and tags
        return new DistMessageStatus();
    }
    /** create broadcast message send to all clients */
    public DistMessage createMessageBroadcast(DistServiceType service, String method, Object message) {
        return new DistMessageAdvanced(agentGuid, "*", service, method, message, "");
    }
    /** */
    public DistMessage createMessage(String sendTo, DistServiceType service, String method, Object message) {
        return new DistMessageAdvanced(agentGuid, sendTo, service, method, message, "");
    }
    /** */
    public DistMessage createMessage(String sendTo, DistServiceType service, String method, Object message, String tags) {
        DistMessageAdvanced dist = new DistMessageAdvanced(agentGuid, sendTo, service, method, message, tags);
        dist.getService();
        return dist;
    }


}
