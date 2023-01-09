package com.cache.api;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/** representing management object for agent
 * It is representing remove Agent with simplified info, updates, registration keys.
 * */
public class AgentObject {

    /** create date of this object representing agent */
    private final LocalDateTime createDate = LocalDateTime.now();
    /** registering object for agent */
    private AgentRegister register;
    private AgentSimplified simplified;
    /** last updated date and time of this Agent */
    private LocalDateTime lastUpdated = LocalDateTime.now();
    /** number of updates of this Agent */
    private final AtomicLong updatesCount = new AtomicLong();
    /** set of registration keys as sources for this Agent */
    private final Set<String> registrationKeys = new HashSet<>();

    // TODO: add more useful info like last ping time, updated storages, connection network from this agent

    public AgentObject(AgentRegister register)
    {
        this.register = register;
        this.simplified = register.toSimplified();
    }
    public AgentObject(AgentSimplified simplified)
    {
        this.simplified = simplified;
    }
    /** get GUID for agent */
    public String getAgentGuid() {
        return register.agentGuid;
    }
    /** unregister this agent */
    public void unregister() {
        // TODO: unregister this agent
    }
    /** update existing agent with new information */
    public void update(AgentSimplified updateInfo, String registrationKey) {
        // TODO: update existing agent
        lastUpdated = LocalDateTime.now();
        registrationKeys.add(registrationKey);
        updatesCount.incrementAndGet();
    }


    /** */
    public AgentSimplified getSimplified() {
        return simplified;
    }
    /** get simplified version of agent in cache */
    public AgentSimplified toSimplified() {
        // TODO: get simplified object of agent with only the most important items - add current information of agent
        return register.toSimplified();
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
}
