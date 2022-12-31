package com.cache.agent.impl;

import com.cache.agent.AgentInstance;
import com.cache.api.DistConfig;
import com.cache.api.DistIssue;
import com.cache.interfaces.AgentIssues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Queue;

/** Implementation of Issues Manager.
 * Issues could be added in case of incorrect data, Exception, any error or unsupported thing in service
 * Issues could be stored and analyzed.
 * */
public class AgentIssuesImpl extends Agentable implements AgentIssues {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(AgentIssuesImpl.class);
    /** queue of issues reported when using cache */
    protected final Queue<DistIssue> issues = new LinkedList<>();

    /** */
    public AgentIssuesImpl(AgentInstance parentAgent) {
        super(parentAgent);
    }

    /** add issue to cache manager to be revoked by parent
     * issue could be Exception, Error, problem with connecting to storage,
     * internal error, not consistent state that is unknown and could be used by parent manager */
    public void addIssue(DistIssue issue) {
        synchronized (issues) {
            issues.add(issue);
            // add issue for registration services
            parentAgent.getAgentServices();
            parentAgent.getAgentRegistrations().addIssue(issue);
            while (issues.size() > parentAgent.getConfig().getPropertyAsLong(DistConfig.CACHE_ISSUES_MAX_COUNT, DistConfig.CACHE_ISSUES_MAX_COUNT_VALUE)) {
                issues.poll();
            }
        }
    }

    /** add issue with method and exception */
    public void addIssue(String methodName, Exception ex) {
        addIssue(new DistIssue(this, methodName, ex));
    }

    /** get all recent issues with cache */
    public Queue<DistIssue> getIssues() {
        return issues;
    }

    /** close issues with clearing all */
    public void close() {
        issues.clear();
    }

}
