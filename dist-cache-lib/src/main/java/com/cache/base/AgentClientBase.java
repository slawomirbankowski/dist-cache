package com.cache.base;

import com.cache.interfaces.Agent;
import com.cache.utils.CacheUtils;

import java.util.Arrays;
import java.util.Set;

/** base class for any client for connectors to other agents */
public abstract class AgentClientBase {

    /** parent agent for this client */
    protected Agent parentAgent;
    /** */
    protected String connectedAgentGuid = "";
    /** unique ID of this client */
    protected final String clientGuid = CacheUtils.generateClientGuid(this.getClass().getSimpleName());
    /** true is this client is still working */
    protected boolean working = true;
    /** tags for this client */
    protected Set<String> tags = Set.of();

    /** creates new client */
    public AgentClientBase(Agent parentAgent) {
        this.parentAgent = parentAgent;
    }
    /** get GUID for this client */
    public String getClientGuid() {
        return clientGuid;
    }
    /** true if client is still working */
    public boolean isWorking() {
        return working;
    }
    /** check if this client has given tag */
    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }
    /** check if this client has any of tags given */
    public boolean hasTags(String[] tgs) {
        return Arrays.stream(tgs).anyMatch(tag -> tags.contains(tag));
    }

}
