package com.cache.agent.impl;

import com.cache.agent.AgentInstance;
import com.cache.api.AgentTimerInfo;
import com.cache.api.AgentTimerTaskInfo;
import com.cache.interfaces.Agent;
import com.cache.interfaces.AgentTimers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

/** implementation of timer manager with scheduled tasks */
public class AgentTimersImpl extends Agentable implements AgentTimers {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(AgentTimersImpl.class);
    /** timer to schedule important check methods */
    private final Timer timer = new Timer();
    /** sequence of run for all timer tasks */
    private final AtomicLong timerRunSeq = new AtomicLong();
    /** all registered tasks for timer */
    private final java.util.concurrent.ConcurrentLinkedQueue<AgentTimerTask> timerTasks = new java.util.concurrent.ConcurrentLinkedQueue<>();

    /** creates service manager for agent with parent agent assigned */
    public AgentTimersImpl(AgentInstance parentAgent) {
        super(parentAgent);
    }

    /** get number of timer tasks */
    public int getTimerTasksCount() {
        return timerTasks.size();
    }
    /** get timer associated with this timer manager */
    public Timer getTimer() {
        return timer;
    }

    /** schedule timer */
    public void setUpTimer(String timerName, String delayConfigName, long defaultTimerValue, Function<String, Boolean> onTask) {
        long timerPeriod = getParentAgent().getConfig().getPropertyAsLong(delayConfigName, defaultTimerValue);
        setUpTimer(timerName, timerPeriod, timerPeriod, onTask);
    }
    /** set-up timer for given method */
    public void setUpTimer(String timerName, long delayMs, long periodMs, Function<String, Boolean> onTask) {
        log.info("Scheduling timer task for agent: " + parentAgent.getAgentGuid() + ", name: " + timerName + ", current tasks count: " + timerTasks.size() + ", delay: " + delayMs + ", period: " + periodMs);
        AgentTimerTask agentTask = new AgentTimerTask(timerName, delayMs, periodMs, onTask);
        TimerTask taskToBeScheduled = new TimerTask() {
            @Override
            public void run() {
                try {
                    long startTime = System.currentTimeMillis();
                    timerRunSeq.incrementAndGet();
                    boolean taskRet = onTask.apply("");
                    if (taskRet) {
                        agentTask.onSuccess(System.currentTimeMillis()- startTime);
                    } else {
                        agentTask.onError();
                    }
                } catch (Exception ex) {
                    // TODO: mark exception
                    agentTask.onException();
                    parentAgent.getAgentIssues().addIssue("AgentTimersImpl.setUpTimer", ex);
                    log.warn("Exception while executing task, reason: " + ex.getMessage());
                }
            }
        };
        agentTask.setTimerTask(taskToBeScheduled);
        timerTasks.add(agentTask);
        timer.scheduleAtFixedRate(taskToBeScheduled, delayMs, periodMs);
    }
    /** get list of infos for timer tasks */
    public List<AgentTimerTaskInfo> getInfoTasks() {
        return timerTasks.stream().map(t -> t.toInfo()).collect(Collectors.toList());
    }
    /** get information about timer and timer tasks */
    public AgentTimerInfo getInfo() {
        // String timerClassName, long timerRunSeq, int timerTasksCount
        return new AgentTimerInfo(timer.getClass().getName(), timerRunSeq.get(), timerTasks.size(), getInfoTasks());
    }

    /** close all tasks and timer */
    public void close() {
        log.info("Closing all tasks for agent: " + getParentAgentGuid() + ", tasks: " + timerTasks.size());
        timerTasks.stream().forEach(tt -> tt.close());
        log.info("Closing timer");
        timer.cancel();
    }

}

/** enriched class keeping timer task with name and statistics */
class AgentTimerTask {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(AgentTimerTask.class);
    private LocalDateTime createdDate = LocalDateTime.now();
    private String name;
    private TimerTask task;
    private long delayMs;
    private long periodMs;
    private final AtomicLong timerRunSeq = new AtomicLong();
    private final AtomicLong errorsCount = new AtomicLong();
    private final AtomicLong exceptionsCount = new AtomicLong();
    private final AtomicLong totalRunTimeMs = new AtomicLong();

    /** method to be executed on time */
    private Function<String, Boolean> onTask;

    public AgentTimerTask(String name, long delayMs, long periodMs, Function<String, Boolean> onTask) {
        this.name = name;
        this.delayMs = delayMs;
        this.periodMs = periodMs;
        this.onTask = onTask;
    }
    public void setTimerTask(TimerTask task) {
        this.task = task;
    }
    public String getCreatedDate() {
        return createdDate.toString();
    }
    public String getName() {
        return name;
    }
    public TimerTask getTask() {
        return task;
    }
    public long getDelayMs() {
        return delayMs;
    }
    public long getPeriodMs() {
        return periodMs;
    }
    public long getTimerRunSeq() {
        return timerRunSeq.get();
    }
    public long getErrorsCount() {
        return errorsCount.get();
    }
    public long getAverageRunTimeMs() {
        if (timerRunSeq.get() > 0) {
            return totalRunTimeMs.get() / timerRunSeq.get();
        } else {
            return 0;
        }
    }
    public void onSuccess(long runTimeMs) {
        timerRunSeq.incrementAndGet();
        totalRunTimeMs.addAndGet(runTimeMs);
    }
    public void onError() {
        timerRunSeq.incrementAndGet();
        errorsCount.incrementAndGet();
    }
    public void onException() {
        timerRunSeq.incrementAndGet();
        exceptionsCount.incrementAndGet();
    }
    public AgentTimerTaskInfo toInfo() {
        return new AgentTimerTaskInfo(createdDate, name, delayMs, periodMs, timerRunSeq.get(), errorsCount.get(), exceptionsCount.get());
    }
    public void close() {
        try {
            task.cancel();
        } catch (Exception ex) {
            log.warn("Cannot cancel task for name: " + name + ", reason: " + ex.getMessage(), ex);
        }
    }
}
