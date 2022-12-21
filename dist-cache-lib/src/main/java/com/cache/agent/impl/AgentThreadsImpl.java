package com.cache.agent.impl;

import com.cache.agent.AgentInstance;
import com.cache.api.AgentThreadObject;
import com.cache.api.DistThreadsInfo;
import com.cache.interfaces.AgentThreads;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

public class AgentThreadsImpl implements AgentThreads {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(AgentThreadsImpl.class);
    /** parent agent for this services manager */
    private AgentInstance parentAgent;

    /** all registered threads */
    private final java.util.concurrent.ConcurrentLinkedQueue<AgentThreadObject> registeredThreads = new java.util.concurrent.ConcurrentLinkedQueue<>();

    /** creates service manager for agent with parent agent assigned */
    public AgentThreadsImpl(AgentInstance parentAgent) {
        this.parentAgent = parentAgent;
    }

    /** get number of threads */
    public int getThreadsCount() {
        return registeredThreads.size();
    }
    /** register thread to be maintained */
    public void registerThread(Thread thread) {
        AgentThreadObject thObj = new AgentThreadObject(thread);
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
