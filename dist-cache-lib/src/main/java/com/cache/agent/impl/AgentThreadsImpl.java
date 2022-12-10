package com.cache.agent.impl;

import com.cache.agent.AgentInstance;
import com.cache.dtos.DistAgentServerRow;
import com.cache.interfaces.AgentThreads;
import com.cache.interfaces.AgentTimers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;

public class AgentThreadsImpl implements AgentThreads {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(AgentThreadsImpl.class);
    /** parent agent for this services manager */
    private AgentInstance parentAgent;

    /** all rows of registered servers */
    private final java.util.concurrent.ConcurrentLinkedQueue<DistAgentServerRow> registeredThreads = new java.util.concurrent.ConcurrentLinkedQueue<>();

    /** creates service manager for agent with parent agent assigned */
    public AgentThreadsImpl(AgentInstance parentAgent) {
        this.parentAgent = parentAgent;
    }

    /** close */
    public void close() {
        log.info("Closing all threads: " + registeredThreads.size());
        // TODO: closing all threads

    }

}
