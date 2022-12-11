package com.cache.interfaces;

import com.cache.api.DistConfig;
import com.cache.api.DistMessageStatus;
import com.cache.api.DistServiceType;

import java.time.LocalDateTime;

/** basic interface for service in distributed environment
 * service is a module or class that is cooperating with agent, could be registered  */
public interface DistService {

    /** get date and time of creating service */
    public LocalDateTime getCreateDate();
    /** get type of service: cache, measure, report, flow, space, ... */
    DistServiceType getServiceType();
    /** process message, returns status */
    DistMessageStatus processMessage(DistMessage msg);
    /** get unique ID of this service */
    String getServiceUid();
    /** get configuration for cache */
    DistConfig getConfig();
    /** close and deinitialize service */
    void close();

}
