package com.cache.agent;

import com.cache.agent.impl.*;
import com.cache.api.*;
import com.cache.interfaces.*;
import com.cache.serializers.ComplexSerializer;
import com.cache.util.JsonUtils;
import com.cache.utils.CacheUtils;
import com.cache.utils.DistMessageProcessor;
import com.cache.utils.DistWebApiProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/** agent class to be connected to dist-cache applications, Kafka, Elasticsearch or other global agent repository
 * Agent is also connecting directly to other agents */
public class AgentInstance implements Agent, DistService {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(AgentInstance.class);
    /** date ant time of creation */
    private final LocalDateTime createDate = LocalDateTime.now();
    /** configuration for agent */
    private final DistConfig config;
    /** if agent has been closed */
    private boolean closed = false;
    /** generate secret of this agent to be able to put commands */
    private final String agentSecret = UUID.randomUUID().toString();
    /** GUID of agent */
    private final String agentGuid = CacheUtils.generateAgentGuid();

    /** manager for threads in Agent system */
    private final AgentThreads agentThreads = new AgentThreadsImpl(this);
    /** manager for timers in Agent system */
    private final AgentTimers agentTimers = new AgentTimersImpl(this);
    /** manager for registrations */
    private final AgentRegistrations agentRegistrations = new AgentRegistrationsImpl(this);
    /** manager for services registered in agent  */
    private final AgentServices agentServices = new AgentServicesImpl(this);
    /** manager for agent connections to other agents */
    private final AgentConnectors agentConnectors = new AgentConnectorsImpl(this);
    /** manager for registrations */
    private final AgentEvents agentEvents = new AgentEventsImpl(this);
    /** manager for registrations */
    private final AgentIssues agentIssues = new AgentIssuesImpl(this);
    /** manager for Web API Objects to direct synchronous communication with this agent*/
    private final AgentApi agentApi = new AgentApiImpl(this);

    /** serializer for serialization of DistMessage to external connectors */
    protected DistSerializer serializer;
    /** processor that is connecting message method with current class method to be executed */
    private final DistMessageProcessor messageProcessor = new DistMessageProcessor()
            .addMethod("ping", this::pingMethod)
            .addMethod("getRegistrationKeys", this::getRegistrationKeys);
    /** processor with functions to handle Web API requests */
    private final DistWebApiProcessor webApiProcessor = new DistWebApiProcessor()
            .addHandler("ping", createTextHandler(param -> "pong"))
            .addHandler("createdDate", (m, req) -> req.responseOkText( getCreateDate().toString()))
            .addHandler("info", createJsonHandler(param -> getAgentInfo()))
            .addHandler("config", createJsonHandler(param -> getConfig().getHashMap()))
            .addHandler("threads", createJsonHandler(param -> agentThreads.getThreadsInfo()))
            .addHandler("timers", createJsonHandler(param -> agentTimers.getInfo()))

            .addHandler("registrations", createJsonHandler(param -> agentRegistrations.getRegistrationKeys()))
            .addHandler("registration-connected-agents", createJsonHandler(param -> agentRegistrations.getAgents()))
            .addHandler("registered-servers", createJsonHandler(param -> agentRegistrations.getServers()))
            .addHandler("registration-infos", createJsonHandler(param -> agentRegistrations.getRegistrationInfos()))

            .addHandler("service-keys", createJsonHandler(param -> agentServices.getServiceKeys()))
            .addHandler("service-types", createJsonHandler(param -> agentServices.getServiceTypes()))
            .addHandler("services", createJsonHandler(param -> agentServices.getServiceInfos()))
            .addHandler("service", createJsonHandler(param -> agentServices.getServiceInfo(param)))

            .addHandler("server-keys", createJsonHandler(param -> agentConnectors.getServerKeys()))
            .addHandler("client-keys", createJsonHandler(param -> agentConnectors.getClientKeys()))

            //.addHandler("xxxxxxxxxxx", (m, req) -> req.responseOkText(JsonUtils.serialize( agentThreads.getThreadsCount())))
            //.addHandler("xxxxxxxxxxx", (m, req) -> req.responseOkText(JsonUtils.serialize( agentTimers.getTimerTasksCount())))
            //.addHandler("xxxxxxxxxxx", (m, req) -> req.responseOkText(JsonUtils.serialize( agentThreads.getThreadsCount())))
            //.addHandler("xxxxxxxxxxx", (m, req) -> req.responseOkText(JsonUtils.serialize( agentTimers.getTimerTasksCount())));
            .addHandler("guid", (m, req) -> req.responseOkText( getAgentGuid()));

    private BiFunction<String, AgentWebApiRequest, AgentWebApiResponse> createTextHandler(Function<String, Object> methodToGetObj) {
        return (m, req) -> req.responseOkText(JsonUtils.serialize(methodToGetObj.apply(req.getParamOne())));
    }
    private BiFunction<String, AgentWebApiRequest, AgentWebApiResponse> createJsonHandler(Function<String, Object> methodToGetObj) {
        return (m, req) -> req.responseOkJson(JsonUtils.serialize( methodToGetObj.apply(req.getParamOne())));
    }
    /** create new agent */
    public AgentInstance(DistConfig config, Map<String, Function<CacheEvent, String>> callbacksMethods, HashMap<String, DistSerializer> serializers) {
        this.config = config;
        // self register of agent as service
        agentServices.registerService(this);
        agentEvents.addCallbackMethods(callbacksMethods);
        serializer = ComplexSerializer.createSerializer(serializers);
    }

    /** get type of service: cache, measure, report, flow, space, ... */
    public DistServiceType getServiceType() {
        return DistServiceType.agent;
    }
    /** get unique ID of this service */
    public String getServiceUid() {
        return agentGuid;
    }
    /** get basic information about service */
    public DistServiceInfo getServiceInfo() {
        // DistServiceType serviceType, String getServiceClass, String serviceGuid, LocalDateTime createdDateTime, boolean closed, Map<String, String> customAttributes
        return new DistServiceInfo(getServiceType(), getClass().getName(), getServiceUid(), createDate, closed, Map.of());
    }
    /** initialize agent - server, application, jdbc, kafka */
    public void initializeAgent() {
        log.info("Initializing agent for guid: " + agentGuid);
        agentRegistrations.createRegistrations();
        agentConnectors.openServers();
        agentApi.openApis();
    }
    /** get this Agent */
    public Agent getAgent() {
        return this;
    }
    /** get configuration for this agent */
    public DistConfig getConfig() { return config; }
    /** get unique ID of this agent */
    public String getAgentGuid() { return agentGuid; }
    /** get date and time of creating this agent */
    public LocalDateTime getCreateDate() {
        return createDate;
    }
    /** get secret generated or set for this agent */
    public String getAgentSecret() {
        return agentSecret;
    }

    /** get serializer/deserializer helper to serialize/deserialize objects when sending through connectors or saving to external storages */
    public DistSerializer getSerializer() {
        return serializer;
    }
    /** get high-level information about this agent */
    public AgentInfo getAgentInfo() {
        return new AgentInfo(agentGuid, createDate, closed,
                agentConnectors.getServersCount(), agentConnectors.getServerKeys(),
                agentConnectors.getClientsCount(), agentConnectors.getClientKeys(),
                agentServices.getServicesCount(), agentServices.getServiceKeys(),
                agentRegistrations.getRegistrationsCount(), agentRegistrations.getRegistrationKeys(),
                getAgentTimers().getTimerTasksCount(), getAgentThreads().getThreadsCount(),
                getAgentEvents().getEvents().size(),
                getAgentIssues().getIssues().size());
    }

    /** get agent threads manager */
    public AgentThreads getAgentThreads() {
        return agentThreads;
    }
    /** get agent timers manager */
    public AgentTimers getAgentTimers() {
        return agentTimers;
    }

    /** get agent service manager */
    public AgentServices getAgentServices() {
        return agentServices;
    }
    /** get agent connector manager to manage direct connections to other agents, including sending and receiving messages */
    public AgentConnectors getAgentConnectors() {
        return agentConnectors;
    }
    /** get agent registration manager to register this agent in global repositories (different types: JDBC, Kafka, App, Elasticsearch, ... */
    public AgentRegistrations getAgentRegistrations() {
        return agentRegistrations;
    }
    /** get agent events manager to add events and set callbacks */
    public AgentEvents getAgentEvents() {
        return agentEvents;
    }
    /** get agent issue manager for adding issues */
    public AgentIssues getAgentIssues() {
        return agentIssues;
    }

    /** returns true if agent has been already closed */
    public boolean isClosed() {
        return closed;
    }

    /** close all items in this agent */
    public void close() {
        log.info("Closing agent: " + agentGuid);
        closed = true;
        // TODO: close all items for this agent - unregister in application, notify all agents
        agentApi.close();
        agentThreads.close();
        agentTimers.close();
        agentServices.close();
        agentConnectors.close();
        agentRegistrations.close();
        agentEvents.close();
        agentIssues.close();
    }

    /** process message by this agent service, choose method and , returns status */
    public DistMessage processMessage(DistMessage msg) {
        log.info("Process message by AgentInstance, message: " + msg);
        return messageProcessor.process(msg.getMethod(), msg);
    }
    /** handle API request in this Web API for this service */
    public AgentWebApiResponse handleRequest(AgentWebApiRequest request) {
        return webApiProcessor.handleRequest(request);
    }
    /** create new message builder starting this agent */
    public DistMessageBuilder createMessageBuilder() {
        return DistMessageBuilder.empty().fromAgent(this);
    }

    /** message send to agent(s) */
    public DistMessageFull sendMessage(DistMessageFull msg) {
        log.info("SENDING MESSAGE " + msg.getMessage());
        getAgentConnectors().sendMessage(msg);
        return msg;
    }
    /** send message to agents */
    public DistMessageFull sendMessage(DistMessage msg, DistCallbacks callbacks) {
        return sendMessage(msg.withCallbacks(callbacks));
    }
    /** create broadcast message send to all clients */
    public DistMessageFull sendMessageBroadcast(DistMessageType messageType, DistServiceType fromService,
                                                DistServiceType toService, String requestMethod, Object message, String tags, LocalDateTime validTill, DistCallbacks callbacks) {
        // DistMessageType messageType, String fromAgent, DistServiceType fromService, String toAgent, DistServiceType toService, String method, Object message,  String tags, LocalDateTime validTill
        DistMessage msg = DistMessage.createMessage(messageType, agentGuid, fromService, "*", toService, requestMethod, message, tags, validTill);
        return sendMessage(msg.withCallbacks(callbacks));
    }
    public DistMessageFull sendMessageBroadcast(DistMessageType messageType, DistServiceType fromService,
                                                DistServiceType toService, String requestMethod, Object message, String tags, DistCallbacks callbacks) {
        DistMessage msg = DistMessage.createMessage(messageType, agentGuid, fromService, "*", toService, requestMethod, message, tags, LocalDateTime.MAX);
        return sendMessage(msg.withCallbacks(callbacks));
    }
    public DistMessageFull sendMessageBroadcast(DistMessageType messageType, DistServiceType fromService,
                                                DistServiceType toService, String requestMethod, Object message, DistCallbacks callbacks) {
        DistMessage msg = DistMessage.createMessage(messageType, agentGuid, fromService, "*", toService, requestMethod, message, "", LocalDateTime.MAX);
        return sendMessage(msg.withCallbacks(callbacks));
    }
    public DistMessageFull sendMessageBroadcast(DistServiceType fromService, DistServiceType toService, String requestMethod, Object message, DistCallbacks callbacks) {
        DistMessage msg = DistMessage.createMessage(DistMessageType.request, agentGuid, fromService, "*", toService, requestMethod, message, "", LocalDateTime.MAX);
        DistMessageFull full = msg.withCallbacks(callbacks);
        return sendMessage(full);
    }
    public DistMessageFull sendMessage(DistService fromService, String toAgent, DistServiceType toService, String method, Object message,
                                   DistCallbacks callbacks) {
        DistMessage msg = DistMessage.createMessage(DistMessageType.request, agentGuid, fromService.getServiceType(), toAgent, toService, method, message, "", LocalDateTime.MAX);
        return sendMessage(msg.withCallbacks(callbacks));
    }
    public DistMessageFull sendMessageAny(DistService fromService, DistServiceType toService, String method, Object message,
                                      DistCallbacks callbacks) {
        DistMessage msg = DistMessage.createMessage(DistMessageType.request, agentGuid, fromService.getServiceType(), "?", toService, method, message, "", LocalDateTime.MAX);
        return sendMessage(msg.withCallbacks(callbacks));
    }


    /** ping this agent, return is pong */
    private DistMessage pingMethod(String methodName, DistMessage msg) {
        log.info("METHOD PING from agent: " + msg.getFromAgent());
        if (msg.isTypeRequest()) {
            return msg.pong(getAgentGuid());
        } else {

            return msg.pong(getAgentGuid());
        }
    }

    /** method to get registration keys for this agent */
    private DistMessage getRegistrationKeys(String methodName, DistMessage msg) {
        getAgentRegistrations().getRegistrationKeys();
        // TODO: create response message with registration keys
        return msg.pong(getAgentGuid());
    }

}
