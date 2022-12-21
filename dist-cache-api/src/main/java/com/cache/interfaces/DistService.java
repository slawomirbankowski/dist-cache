package com.cache.interfaces;

import com.cache.api.*;

import java.time.LocalDateTime;

/** basic interface for service in distributed environment
 * service is a module or class that is cooperating with agent, could be registered  */
public interface DistService {

    /** get date and time of creating service */
    public LocalDateTime getCreateDate();
    /** get type of service: cache, measure, report, flow, space, ... */
    DistServiceType getServiceType();
    /** get parent Agent */
    Agent getAgent();
    /** process message, returns status */
    DistMessage processMessage(DistMessage msg);
    /** handle API request in this Web API for this service */
    AgentWebApiResponse handleRequest(AgentWebApiRequest request);
    /** get unique ID of this service */
    String getServiceUid();
    /** get basic information about service */
    DistServiceInfo getServiceInfo();
    /** get configuration for cache */
    DistConfig getConfig();
    /** close and deinitialize service */
    void close();

}
