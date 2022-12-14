package com.cache.interfaces;

import com.cache.api.DistMessage;

import java.util.List;

/** interface for service manager
 * there are many services that would be working with Dist system, example:
 * cache, reports, flow, measures, space, scheduler, security, config */
public interface AgentServices {

    /** return all services assigned to this agent */
    List<DistService> getServices();
    /** get number of services */
    int getServicesCount();
    /** get keys of registered services */
    List<String> getServiceKeys();
    /** register service to this agent */
    void registerService(DistService service) ;
    /** receive message from connector or server, need to find service and process that message on service */
    void receiveMessage(DistMessage msg);
    /** close  */
    void close();

}
