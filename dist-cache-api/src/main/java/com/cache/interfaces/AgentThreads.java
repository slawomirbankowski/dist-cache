package com.cache.interfaces;


/** interface for threads manager in agent
 * object to manage threads created in agent - to be sure that all threads are properly maintained and stopped when not needed */
public interface AgentThreads {

    /** close  */
    void close();
}
