package com.cache.api;

import java.time.LocalDateTime;
import java.util.List;

public class AgentInfo {

    private String agentGuid;
    private LocalDateTime createDate;
    private boolean closed;
    private int serversCount;

    private int servicesCount;
    private List<String> services;
    private List<String> servers;

    private int clientsCount;
    private List<String> clients;

    private int timerTasksCount;
    private int threadsCount;
    private int registrationsCount;
    private List<String> registrations;
    private int eventsCount;
    private int issuesCount;

    public AgentInfo(String agentGuid, LocalDateTime createDate, boolean closed,
                     int serversCount, List<String> servers,int clientsCount, List<String> clients,
                     int servicesCount, List<String> services,
                     int registrationsCount, List<String> registrations,
                     int timerTasksCount, int threadsCount, int eventsCount, int issuesCount) {
        this.agentGuid = agentGuid;
        this.createDate = createDate;
        this.closed = closed;

        this.serversCount = serversCount;
        this.servers = servers;

        this.clientsCount = clientsCount;
        this.clients = clients;

        this.servicesCount = servicesCount;
        this.services = services;

        this.registrationsCount = registrationsCount;
        this.registrations = registrations;

        this.timerTasksCount = timerTasksCount;
        this.threadsCount = threadsCount;

        this.eventsCount = eventsCount;
        this.issuesCount = issuesCount;
    }

    /** serialize this agent info */
    public String toString() {
        return "AGENT uid: " + agentGuid + ", created: " + createDate + ", closed: " + closed
                + ", serversCount: " + serversCount + ", servers: " + servers
                + ", clientsCount: " + clientsCount + ", clients: " + clients
                + ", registrationsCount: " + registrationsCount + ", registrations: " + registrations
                + ", timerTasksCount: " + timerTasksCount + ", threadsCount: " + threadsCount
                + ", eventsCount: " + eventsCount + ", issuesCount: " + issuesCount;
    }

    public String getAgentGuid() {
        return agentGuid;
    }

    public String getCreateDate() {
        return createDate.toString();
    }
    public boolean isClosed() {
        return closed;
    }
    public int getServicesCount() {
        return servicesCount;
    }
    public List<String> getServices() {
        return services;
    }
    public int getServersCount() {
        return serversCount;
    }
    public List<String> getServers() {
        return servers;
    }
    public int getClientsCount() {
        return clientsCount;
    }
    public List<String> getClients() {
        return clients;
    }
    public int getTimerTasksCount() {
        return timerTasksCount;
    }
    public int getThreadsCount() {
        return threadsCount;
    }
    public int getRegistrationsCount() {
        return registrationsCount;
    }
    public List<String> getRegistrations() {
        return registrations;
    }
    public int getEventsCount() {
        return eventsCount;
    }
    public int getIssuesCount() {
        return issuesCount;
    }
}
