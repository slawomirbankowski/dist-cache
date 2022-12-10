package com.cache.interfaces;

import java.util.function.Function;

/** interface for timers manager in agent
 * object to manager timers with scheduled tasks */
public interface AgentTimers {

    /** start timer */
    void setUpTimer(long delayMs, long periodMs, Function<String, Boolean> onTask);
    /** close  */
    void close();

}
