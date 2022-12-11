package com.cache.interfaces;


/** interface for threads manager in agent
 * object to manage threads created in agent - to be sure that all threads are properly maintained and stopped when not needed */
public interface AgentThreads {

    /** register thread to be maintained */
    void registerThread(Thread thread);
    /** get number of threads */
    int getThreadsCount();
    /** close  */
    void close();
}
