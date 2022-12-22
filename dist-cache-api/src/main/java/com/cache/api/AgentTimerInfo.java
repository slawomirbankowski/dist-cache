package com.cache.api;

/** information class to keep timer attributes
 * Timers with tasks are defined in Dist services to perform repetitive work like cleaning cache
 * */
public class AgentTimerInfo {
    private String timerClassName;
    private long timerRunSeq;
    private int timerTasksCount;

    public AgentTimerInfo(String timerClassName, long timerRunSeq, int timerTasksCount) {
        this.timerClassName = timerClassName;
        this.timerRunSeq = timerRunSeq;
        this.timerTasksCount = timerTasksCount;
    }

    public String getTimerClassName() {
        return timerClassName;
    }
    public long getTimerRunSeq() {
        return timerRunSeq;
    }
    public int getTimerTasksCount() {
        return timerTasksCount;
    }
}
