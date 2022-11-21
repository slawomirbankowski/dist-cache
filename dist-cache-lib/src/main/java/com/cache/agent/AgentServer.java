package com.cache.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AgentServer {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(AgentServer.class);

    /** socket server for connections from other agents */
    private java.net.ServerSocket socket = null;

    public AgentServer() {
    }
    public void initializeServer(int workingPort) {
        try {
            // open socket port
            log.info("Starting socket for incoming connections from other clients on port " + workingPort);
            // TODO: create SocketServer and thread for accepting sockets
            //java.net.ServerSocket socket = new java.net.ServerSocket(workingPort);
            //socket.accept();
            //socket.getLocalPort();
            //socket.connect();
            //parentCache.addIssue();
        } catch (Exception ex) {
        }
    }
}
