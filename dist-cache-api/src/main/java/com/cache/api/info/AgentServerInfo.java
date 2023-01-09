package com.cache.api.info;


import com.cache.api.enums.DistClientType;

/** information class about Server manager in Agent. */
public class AgentServerInfo {
    private DistClientType serverType;
    private String serverGuid;
    private String url;
    private int port;
    private boolean closed;
    private long receivedMessages;

    public AgentServerInfo(DistClientType serverType, String serverGuid, String url, int port, boolean closed, long receivedMessages) {
        this.serverType = serverType;
        this.serverGuid = serverGuid;
        this.url = url;
        this.port = port;
        this.closed = closed;
        this.receivedMessages = receivedMessages;
    }

    public DistClientType getServerType() {
        return serverType;
    }

    public String getServerGuid() {
        return serverGuid;
    }

    public String getUrl() {
        return url;
    }

    public int getPort() {
        return port;
    }

    public boolean isClosed() {
        return closed;
    }

    public long getReceivedMessages() {
        return receivedMessages;
    }
}
