package com.cache.interfaces;

import com.cache.api.DistIssue;

import java.util.Queue;

public interface AgentIssues {

    /** add issue to cache manager to be revoked by parent
     * issue could be Exception, Error, problem with connecting to storage,
     * internal error, not consistent state that is unknown and could be used by parent manager */
    void addIssue(DistIssue issue);
    /** add issue with method and exception */
    public void addIssue(String methodName, Exception ex);
    /** get all recent issues with cache */
    Queue<DistIssue> getIssues();
    /** close this manager for issues */
    void close();
}
