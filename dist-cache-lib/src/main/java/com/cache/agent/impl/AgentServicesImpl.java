package com.cache.agent.impl;

import com.cache.agent.AgentInstance;
import com.cache.api.DistMessageStatus;
import com.cache.interfaces.AgentServices;
import com.cache.interfaces.DistMessage;
import com.cache.interfaces.DistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class AgentServicesImpl implements AgentServices {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(AgentServicesImpl.class);
    /** parent agent for this services manager */
    private AgentInstance parentAgent;
    /** all registered services for this agent */
    private final HashMap<String, DistService> services = new HashMap<>();

    /** creates service manager for agent with parent agent assigned */
    public AgentServicesImpl(AgentInstance parentAgent) {
        this.parentAgent = parentAgent;
    }

    /** return all services assigned to this agent */
    public List<DistService> getServices() {
        return services.values().stream().collect(Collectors.toList());
    }
    /** get number of services */
    public int getServicesCount() {
        return services.size();
    }
    /** get keys of registered services */
    public List<String> getServiceKeys() {
        return services.values().stream().map(x -> x.getServiceUid()).collect(Collectors.toList());
    }
    /** register service to this agent */
    public void registerService(DistService service) {
        // TODO: register new service like cache, report, measure, ...
        synchronized (services) {
            services.put(service.getServiceType().name(), service);
        }
    }
    /** receive message from connector or server, need to find service and process that message on service */
    public DistMessageStatus receiveMessage(DistMessage msg) {
        DistService serviceToProcessMessage = services.get(msg.getService());
        if (serviceToProcessMessage != null) {
            return serviceToProcessMessage.processMessage(msg);
        } else {
            return new DistMessageStatus();
        }
    }
    /** close */
    public void close() {
        log.info("Closing all registered services with agent, services: " + services.size());
    }

}
