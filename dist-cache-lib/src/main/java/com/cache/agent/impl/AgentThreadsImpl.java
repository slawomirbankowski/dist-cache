package com.cache.agent.impl;

import com.cache.api.AgentThreadObject;
import com.cache.api.enums.DistComponentType;
import com.cache.api.info.DistThreadsInfo;
import com.cache.interfaces.Agent;
import com.cache.interfaces.AgentComponent;
import com.cache.interfaces.AgentThreads;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

public class AgentThreadsImpl extends Agentable implements AgentThreads, AgentComponent {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(AgentThreadsImpl.class);
    /** all registered threads */
    private final java.util.concurrent.ConcurrentLinkedQueue<AgentThreadObject> registeredThreads = new java.util.concurrent.ConcurrentLinkedQueue<>();

    /** creates service manager for agent with parent agent assigned */
    public AgentThreadsImpl(Agent parentAgent) {
        super(parentAgent);
        parentAgent.addComponent(this);
    }

    /** get type of this component */
    public DistComponentType getComponentType() {
        return DistComponentType.threads;
    }
    @Override
    public String getGuid() {
        return getParentAgentGuid();
    }
    /** get number of threads */
    public int getThreadsCount() {
        return registeredThreads.size();
    }
    /** register thread to be maintained by Agent */
    public void registerThread(AgentComponent parent, Thread thread, String threadFriendlyName) {
        AgentThreadObject thObj = new AgentThreadObject(parent, thread, threadFriendlyName);
        registeredThreads.add(thObj);
    }
    /** get information about managed threads */
    public DistThreadsInfo getThreadsInfo() {
        var infos = registeredThreads.stream().map(x -> x.getInfo()).collect(Collectors.toList());
        return new DistThreadsInfo(infos.size(), infos);
    }
    /** close - make sure all threads would be closed */
    public void close() {
        log.info("Closing all threads: " + registeredThreads.size());
        // TODO: closing all threads

    }

}
