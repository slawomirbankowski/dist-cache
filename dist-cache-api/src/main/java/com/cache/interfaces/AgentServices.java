package com.cache.interfaces;

import com.cache.api.AgentWebApiRequest;
import com.cache.api.AgentWebApiResponse;
import com.cache.api.DistMessage;
import com.cache.api.DistServiceInfo;

import java.util.List;
import java.util.Set;

/** interface for service manager
 * there are many services that would be working with Dist system, example:
 * cache, reports, flow, measures, space, scheduler, security, config
 * Full list of services are in DistServiceType.
 * */
public interface AgentServices {

    /** return all services assigned to this agent */
    List<DistService> getServices();
    /** get number of services */
    int getServicesCount();
    /** get keys of registered services */
    List<String> getServiceKeys();
    /** get types of registered services */
    Set<String> getServiceTypes();
    /** get basic information about service for given type of UID */
    DistServiceInfo getServiceInfo(String serviceUid);
    /** get basic information about all services */
    List<DistServiceInfo> getServiceInfos();
    /** register service to this agent */
    void registerService(DistService service) ;
    /** receive message from connector or server, need to find service and process that message on service */
    void receiveMessage(DistMessage msg);
    /** handle API request in this Web API for Agent */
    AgentWebApiResponse handleRequest(AgentWebApiRequest request);
    /** close  */
    void close();

}
