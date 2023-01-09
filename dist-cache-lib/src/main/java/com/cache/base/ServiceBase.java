package com.cache.base;

import com.cache.agent.impl.Agentable;
import com.cache.api.DistServiceInfo;
import com.cache.interfaces.Agent;
import com.cache.interfaces.DistService;
import com.cache.utils.DistUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/** base class for any service connected to Agent */
public abstract class ServiceBase extends Agentable implements DistService {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(ServiceBase.class);
    /** UUID for service - globally unique */
    protected String guid = DistUtils.generateCacheGuid();

    /** if service has been already closed */
    protected boolean isClosed = false;

    /** creates new service with agent */
    public ServiceBase(Agent parentAgent) {
        super(parentAgent);
    }


    /** get unique ID of this service */
    public String getServiceUid() {
        return guid;
    }
    /** create new service UID for this service */
    protected abstract String createServiceUid();
    /** get basic information about service */
    public DistServiceInfo getServiceInfo() {
        return new DistServiceInfo(getServiceType(), getClass().getName(), getServiceUid(), createDate, isClosed, getServiceInfoCustomMap());
    }
    /** get custom map of info about service */
    public Map<String, String> getServiceInfoCustomMap() {
        return Map.of();
    }
    /** check if service has been already closed and deinitialized */
    public boolean getClosed() { return isClosed; }
    /** close all items in this service */
    protected abstract void onClose();
    /** close and deinitialize service - remove all items, disconnect from all storages, stop all timers */
    public final void close() {
        if (isClosed) {
            log.warn("Service is already closed for UID: " + getServiceUid());
        } else {
            isClosed = true;
            log.info("Closing cache for GUID: " + getServiceUid());
            onClose();

        }
    }

}
