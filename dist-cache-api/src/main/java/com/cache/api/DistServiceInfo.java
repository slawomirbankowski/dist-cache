package com.cache.api;

import java.time.LocalDateTime;
import java.util.Map;

/** class to keep information about Dist Service */
public class DistServiceInfo {
    private DistServiceType serviceType;
    private String getServiceClass;
    private String serviceGuid;
    private LocalDateTime createdDateTime;
    private boolean closed;
    private Map<String, String> customAttributes;

    /** */
    public DistServiceInfo(DistServiceType serviceType, String getServiceClass, String serviceGuid, LocalDateTime createdDateTime, boolean closed, Map<String, String> customAttributes) {
        this.serviceType = serviceType;
        this.getServiceClass = getServiceClass;
        this.serviceGuid = serviceGuid;
        this.createdDateTime = createdDateTime;
        this.closed = closed;
        this.customAttributes = customAttributes;
    }

    public DistServiceType getServiceType() {
        return serviceType;
    }

    public String getGetServiceClass() {
        return getServiceClass;
    }

    public String getServiceGuid() {
        return serviceGuid;
    }

    public String getCreatedDateTime() {
        return createdDateTime.toString();
    }

    public boolean isClosed() {
        return closed;
    }

    public Map<String, String> getCustomAttributes() {
        return customAttributes;
    }
}
