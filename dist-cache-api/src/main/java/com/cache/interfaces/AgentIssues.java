package com.cache.interfaces;

import com.cache.api.DistIssue;

import java.util.Queue;

/** Interface for Issue manager to have possibilities to add and list issues added by services connected to Agent.
 * Issues are waiting in a queue with limited size, it means that the oldest issues would be removed forever.
 * */
public interface AgentIssues {

    /** add issue to cache manager to be revoked by parent
     * issue could be Exception, Error, problem with connecting to storage,
     * internal error, not consistent state that is unknown and could be used by parent manager */
    void addIssue(DistIssue issue);
    /** add issue with method and exception - issue can be sent to logger or get by parent applications to check what is going on */
    public void addIssue(String methodName, Exception ex);
    /** get all recent issues with cache */
    Queue<DistIssue> getIssues();
    /** close this manager for issues */
    void close();
}
