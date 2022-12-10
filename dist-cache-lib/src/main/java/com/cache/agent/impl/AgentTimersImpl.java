package com.cache.agent.impl;

import com.cache.agent.AgentInstance;
import com.cache.api.DistConfig;
import com.cache.api.DistMessageStatus;
import com.cache.dtos.DistAgentServerRow;
import com.cache.interfaces.AgentServices;
import com.cache.interfaces.AgentTimers;
import com.cache.interfaces.DistMessage;
import com.cache.interfaces.DistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

/** implementation of timer manager with scheduled tasks */
public class AgentTimersImpl implements AgentTimers {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(AgentTimersImpl.class);
    /** parent agent for this services manager */
    private AgentInstance parentAgent;
    /** timer to schedule important check methods */
    private final Timer timer = new Timer();
    /** sequence of run for all timer tasks */
    private final AtomicLong timerRunSeq = new AtomicLong();
    /** all registered tasks for timer */
    private final java.util.concurrent.ConcurrentLinkedQueue<TimerTask> timerTasks = new java.util.concurrent.ConcurrentLinkedQueue<>();

    /** creates service manager for agent with parent agent assigned */
    public AgentTimersImpl(AgentInstance parentAgent) {
        this.parentAgent = parentAgent;
    }

    /** set-up timer gor given method */
    public void setUpTimer(long delayMs, long periodMs, Function<String, Boolean> onTask) {
        // initialization for communicate
        log.info("Scheduling timer task for agent: " + parentAgent.getAgentGuid());
        timerRunSeq.incrementAndGet();
        TimerTask onTimeCommunicateTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    //agentConnectors
                    onTask.apply("");
                } catch (Exception ex) {
                    // TODO: mark exception

                    log.warn("Exception while executing task, reason: " + ex.getMessage());
                }
            }
        };
        timerTasks.add(onTimeCommunicateTask);
        timer.scheduleAtFixedRate(onTimeCommunicateTask, delayMs, periodMs);
    }

    /** close */
    public void close() {
        log.info("Closing all tasks: " + timerTasks.size());

    }

}
