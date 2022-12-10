package com.cache.interfaces;

import com.cache.api.DistMessageStatus;

import java.util.List;

/** interface for service manager */
public interface AgentServices {

    /** return all services assigned to this agent */
    List<DistService> getServices();
    /** register service to this agent */
    void registerService(DistService service) ;
    /** receive message from connector or server, need to find service and process that message on service */
    DistMessageStatus receiveMessage(DistMessage msg);
    /** close  */
    void close();

}
