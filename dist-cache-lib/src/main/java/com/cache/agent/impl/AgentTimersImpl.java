package com.cache.agent.impl;

import com.cache.agent.AgentInstance;
import com.cache.api.AgentTimerInfo;
import com.cache.interfaces.AgentTimers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

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

    /** get number of timer tasks */
    public int getTimerTasksCount() {
        return timerTasks.size();
    }
    /** get timer associated with this timer manager */
    public Timer getTimer() {
        return timer;
    }

    /** set-up timer gor given method */
    public void setUpTimer(long delayMs, long periodMs, Function<String, Boolean> onTask) {
        // initialization for communicate
        log.info("Scheduling timer task for agent: " + parentAgent.getAgentGuid() + ", current tasks: " + timerTasks.size());
        timerRunSeq.incrementAndGet();
        TimerTask onTimeCommunicateTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    timerRunSeq.incrementAndGet();
                    onTask.apply("");
                } catch (Exception ex) {
                    // TODO: mark exception
                    parentAgent.getAgentIssues().addIssue("AgentTimersImpl.setUpTimer", ex);
                    log.warn("Exception while executing task, reason: " + ex.getMessage());
                }
            }
        };
        timerTasks.add(onTimeCommunicateTask);
        timer.scheduleAtFixedRate(onTimeCommunicateTask, delayMs, periodMs);
    }
    /** get information about timer and timer tasks */
    public AgentTimerInfo getInfo() {
        // String timerClassName, long timerRunSeq, int timerTasksCount
        return new AgentTimerInfo(timer.getClass().getName(), timerRunSeq.get(), timerTasks.size());
    }

    /** close */
    public void close() {
        log.info("Closing all tasks: " + timerTasks.size());

    }








}
