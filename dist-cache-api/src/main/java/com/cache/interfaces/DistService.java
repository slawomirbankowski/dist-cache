package com.cache.interfaces;

import com.cache.api.DistConfig;
import com.cache.api.DistMessageStatus;
import com.cache.api.DistServiceType;

/** basic interface for service in distributed environment */
public interface DistService {

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
