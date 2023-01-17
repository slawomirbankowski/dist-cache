package com.cache.agent.impl;

import com.cache.api.DistConfig;
import com.cache.api.DistIssue;
import com.cache.api.enums.DistComponentType;
import com.cache.interfaces.Agent;
import com.cache.interfaces.AgentComponent;
import com.cache.interfaces.AgentIssues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Queue;

/** Implementation of Issues Manager.
 * Issues could be added in case of incorrect data, Exception, any error or unsupported thing in service
 * Issues could be stored and analyzed.
 * */
public class AgentIssuesImpl extends Agentable implements AgentIssues, AgentComponent {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(AgentIssuesImpl.class);
    /** queue of issues reported when using cache */
    protected final Queue<DistIssue> issues = new LinkedList<>();
    /** */
    public AgentIssuesImpl(Agent parentAgent) {
        super(parentAgent);
        parentAgent.addComponent(this);
    }

    /** get type of this component */
    public DistComponentType getComponentType() {
        return DistComponentType.api;
    }
    /** add issue to be revoked by parent
     * issue could be Exception, Error, problem with connecting to storage,
     * internal error, not consistent state that is unknown and could be used by parent manager */
    public void addIssue(DistIssue issue) {
        synchronized (issues) {
            issues.add(issue);
            // add issue for registration services
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
        synchronized (issues) {
            log.info("Closing Issues, clearing all, count: " + issues.size());
            issues.clear();
        }
    }

    @Override
    public String getGuid() {
        return getParentAgentGuid();
    }
}
