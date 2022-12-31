package com.cache.agent.impl;

import com.cache.agent.AgentInstance;
import com.cache.api.*;
import com.cache.interfaces.AgentServices;
import com.cache.interfaces.Cache;
import com.cache.interfaces.DistService;
import com.cache.interfaces.Reports;
import com.cache.managers.CacheManager;
import com.cache.report.ReportsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** */
public class AgentServicesImpl extends Agentable implements AgentServices {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(AgentServicesImpl.class);
    /** all registered services for this agent */
    private final HashMap<String, DistService> services = new HashMap<>();
    /** policy to add cache Objects to storages and changing mode, ttl, priority, tags */
    protected CachePolicy policy;
    /** cache service */
    private Cache cache;
    /** service for reports */
    private Reports reports;

    /** creates service manager for agent with parent agent assigned */
    public AgentServicesImpl(AgentInstance parentAgent) {
        super(parentAgent);
    }
    /** set new policy for services */
    public void setPolicy(CachePolicy policy) {
        this.policy = policy;
    }
    /** return all services assigned to this agent */
    public List<DistService> getServices() {
        return services.values().stream().collect(Collectors.toList());
    }
    /** get or create cache connected with this Agent */
    public Cache getCache() {
        if (cache != null) {
            return cache;
        }
        synchronized (this) {
            if (cache == null) {
                cache = new CacheManager(getParentAgent(), policy);
                registerService(cache);
            }
            return cache;
        }
    }
    /** get or create service for reports to create, update, remove or execute reports */
    public Reports getReports() {
        if (reports != null) {
            return reports;
        }
        synchronized (this) {
            if (reports == null) {
                reports = new ReportsImpl(getParentAgent());
                registerService(reports);
            }
            return reports;
        }
    }
    /** get number of services */
    public int getServicesCount() {
        return services.size();
    }
    /** get keys of registered services */
    public List<String> getServiceKeys() {
        return services.values().stream().map(DistService::getServiceUid).collect(Collectors.toList());
    }
    /** get types of registered services */
    public Set<String> getServiceTypes() {
        return services.keySet();
    }
    /** get basic information about service for given type of UID */
    public DistServiceInfo getServiceInfo(String serviceUid) {
        DistService srv = services.get(serviceUid);
        if (srv != null) {
            srv.getServiceInfo();
        }
        return null;
    }
    /** get basic information about all services */
    public List<DistServiceInfo> getServiceInfos() {
        return services.values().stream().map(DistService::getServiceInfo).collect(Collectors.toList());
    }
    /** register service to this agent */
    public void registerService(DistService service) {
        // TODO: register new service like cache, report, measure, ...
        synchronized (services) {
            services.put(service.getServiceType().name(), service);
        }
    }
    /** receive message from connector or server, need to find service and process that message on service */
    public void receiveMessage(DistMessage msg) {
        log.info("Receive message to be processes, message: " + msg.toString());
        if (msg.isTypeRequest()) {
            DistMessage response = processMessage(msg);
            // got response, send it to requestor agent
            parentAgent.getAgentConnectors().sendMessage(response);
        } else if (msg.isTypeResponse()) {
            if (msg.isTypeResponse()) {
                processMessage(msg);
            }
            parentAgent.getAgentConnectors().markResponse(msg);
        } else {
            // incorrect message type - log Issue
            parentAgent.getAgentIssues().addIssue("receiveMessage", new Exception("Unknown message type: " + msg.getMessageType().name()));
        }
    }
    /** process message - find service and execute on method  */
    public DistMessage processMessage(DistMessage msg) {
        DistService serviceToProcessMessage = services.get(msg.getToService().name());
        if (serviceToProcessMessage != null) {
            return serviceToProcessMessage.processMessage(msg);
        } else {
            return msg.serviceNotFound();
        }
    }

    /** handle API request in this Web API for Agent */
    public AgentWebApiResponse handleRequest(AgentWebApiRequest request) {
        DistService service = services.get(request.getServiceName());
        if (service != null) {
            return service.handleRequest(request);
        }
        //  no service found, returning 404
        return new AgentWebApiResponse(404, AgentWebApiRequest.headerText, "No service for name: " + request.getServiceName());
    }
    /** close */
    public void close() {
        log.info("Closing all registered services with agent, services: " + services.size());
    }

}
