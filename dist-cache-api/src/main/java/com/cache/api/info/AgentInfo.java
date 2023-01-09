package com.cache.api.info;

import com.cache.api.DistServiceInfo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/** information DTO about Agent - current state */
public class AgentInfo implements Serializable {

    private String agentGuid;
    private LocalDateTime createDate;
    private boolean closed;
    private Set<String> tags;
    List<DistServiceInfo> services;

    private AgentConnectorsInfo connectors;

    private List<AgentRegistrationInfo> registrations;

    private AgentTimerInfo timers;
    private DistThreadsInfo threads;

    private int eventsCount;
    private int issuesCount;

    public AgentInfo(String agentGuid, LocalDateTime createDate, boolean closed,
                     Set<String> tags,
                     AgentConnectorsInfo connectors,
                     List<DistServiceInfo> services,
                     List<AgentRegistrationInfo> registrations,
                     AgentTimerInfo timers, DistThreadsInfo threads,
                     int eventsCount, int issuesCount) {
        this.agentGuid = agentGuid;
        this.createDate = createDate;
        this.closed = closed;
        this.tags = tags;
        this.connectors = connectors;
        this.services = services;
        this.registrations = registrations;
        this.timers = timers;
        this.threads = threads;
        this.eventsCount = eventsCount;
        this.issuesCount = issuesCount;
    }

    /** serialize this agent info */
    public String toString() {
        return "AGENT uid: " + agentGuid + ", created: " + createDate + ", closed: " + closed
                + ", registrationsCount: " + registrations.size()
                + ", timerTasksCount: " + timers.getTasks().size() + ", threadsCount: " + threads.getThreadsCount()
                + ", eventsCount: " + eventsCount + ", issuesCount: " + issuesCount;
    }

    public String getAgentGuid() {
        return agentGuid;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public boolean isClosed() {
        return closed;
    }

    public Set<String> getTags() {
        return tags;
    }

    public List<DistServiceInfo> getServices() {
        return services;
    }

    public AgentConnectorsInfo getConnectors() {
        return connectors;
    }

    public List<AgentRegistrationInfo> getRegistrations() {
        return registrations;
    }

    public AgentTimerInfo getTimers() {
        return timers;
    }

    public DistThreadsInfo getThreads() {
        return threads;
    }

    public int getEventsCount() {
        return eventsCount;
    }

    public int getIssuesCount() {
        return issuesCount;
    }
}
