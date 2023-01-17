package com.cache.interfaces;

import com.cache.api.enums.DistComponentType;

import java.time.LocalDateTime;

/** base interface for any component in Agent */
public interface AgentComponent {

    /** get Agent of this component */
    Agent getAgent();
    /** get type of this component */
    DistComponentType getComponentType();
    /** get date and time of creation of this component */
    LocalDateTime getCreateDate();
    /** get globally unique ID of this component - this could be serverGuid, clientGuid, managerGuid */
    String getGuid();
}
