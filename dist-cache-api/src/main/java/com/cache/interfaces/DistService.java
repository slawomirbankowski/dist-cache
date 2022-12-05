package com.cache.interfaces;

import com.cache.api.DistConfig;

/** basic interface for service in distributed environment */
public interface DistService {

    /** get type of service: cache, measure, report, */
    String getServiceType();
    /** get unique ID of this service */
    String getServiceUid();
    /** get configuration for cache */
    DistConfig getConfig();
    /** close and deinitialize service */
    void close();

}
