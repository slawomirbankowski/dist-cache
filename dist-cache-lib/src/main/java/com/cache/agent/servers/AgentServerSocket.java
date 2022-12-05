package com.cache.agent.servers;

import com.cache.api.DistConfig;
import com.cache.interfaces.Agent;
import com.cache.interfaces.AgentServer;
import com.cache.utils.CacheUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;

public class AgentServerSocket implements AgentServer, Runnable {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(AgentServerSocket.class);
    /** date ant time of creation */
    private final LocalDateTime createDate = LocalDateTime.now();
    /** GUID of agent */
    private final String serverGuid = CacheUtils.generateAgentGuid();
    /** if server has been closed */
    private boolean closed = false;
    private final Agent parentAgent;
    /** socket server for connections from other agents */
    private java.net.ServerSocket serverSocket = null;
    /** all threads initialized */
    private LinkedList<Thread> threads = new LinkedList<>();
    /** all threads initialized */
    private LinkedList<SocketServerClient> clients = new LinkedList<>();
    /** all clients but organized by agent GUID when agent is sending introduction message */
    private HashMap<String,SocketServerClient> clientsByAgentGuid = new HashMap<>();
    private int workingPort;

    /** */
    public AgentServerSocket(Agent parentAgent) {
        this.parentAgent = parentAgent;
    }
    public void initializeServer(int workingPort) {
        try {
            this.workingPort = workingPort;
            // open socket port
            log.info("Starting socket for incoming connections from other clients on port " + workingPort);
            // TODO: create SocketServer and thread for accepting sockets
            serverSocket = new java.net.ServerSocket(workingPort);
            serverSocket.setSoTimeout(2000);
            log.info("Starting socket server on port: " + workingPort);
            Thread mainThread = new Thread(this);
            mainThread.setDaemon(true);
            mainThread.start();
            threads.add(mainThread);
        } catch (Exception ex) {
            log.info("Cannot run socket server on port: " + workingPort + ", reason: " + ex.getMessage());
        }
    }
    /** run in separated thread to accept */
    public void run() {
        while (!closed) {
            try {
                System.out.print("......... Accepting connections on port " + workingPort);
                Socket socket = serverSocket.accept();
                if (socket != null) {
                    SocketServerClient client = new SocketServerClient(this, serverSocket, socket);
                    clients.add(client);
                }
            } catch (Exception ex) {
                log.info("!!!!! Interrupted accepting sockets on port " + workingPort);
            }
            CacheUtils.sleep(2000);
        }
    }
    @Override
    public String getServerGuid() {
        return serverGuid;
    }
    @Override
    public DistConfig getConfig() {
        return parentAgent.getConfig();
    }
    @Override
    public boolean isClosed() {
        return closed;
    }
    @Override
    public LocalDateTime getCreateDate() {
        return createDate;
    }

    @Override
    public void close() {
        // TODO: close socket server and all clients
        closed = true;
        threads.stream().forEach(th -> {
            try {
                log.info("Closing main thread of socket server");
                // TODO: proper thread close
                th.join(4000);
            } catch (Exception ex) {
                //
                log.info("Cannot close thread: " + th.getName() + ", reason: " + ex.getMessage());
            }
        });
        clients.stream().forEach(c -> {
            c.close();
        });
        try {
            log.info("Closing socket server at port " + workingPort);
            serverSocket.close();
        } catch (Exception ex) {
            log.warn("Cannot close socket server, reason: " + ex.getMessage());
        }
    }
}

/** socket client with client communications */
class SocketServerClient {

    /** */
    public SocketServerClient(AgentServerSocket parentServer, java.net.ServerSocket serverSocket, Socket socket) {

    }
    public void close() {

    }
}