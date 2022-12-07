package com.cache.agent.servers;

import com.cache.api.DistConfig;
import com.cache.interfaces.Agent;
import com.cache.interfaces.AgentServer;
import com.cache.utils.CacheUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;

public class AgentServerSocket implements AgentServer, Runnable {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(AgentServerSocket.class);
    /** date ant time of creation */
    private final LocalDateTime createDate = LocalDateTime.now();
    /** GUID of server */
    private final String serverGuid = CacheUtils.generateServerGuid("Socket");
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
            System.out.println("Starting socket server on port: " + workingPort);
            Thread mainThread = new Thread(this);
            mainThread.setDaemon(true);
            mainThread.start();
            threads.add(mainThread);
        } catch (Exception ex) {
            System.out.println("Cannot run socket server on port: " + workingPort + ", reason: " + ex.getMessage());
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
                System.out.println("!!!!! Interrupted accepting sockets on port " + workingPort);
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
class SocketServerClient implements Runnable {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(SocketServerClient.class);
    protected AgentServerSocket parentServer;
    protected java.net.ServerSocket serverSocket;
    protected Socket socket;
    /** name of connected host */
    protected String host = "";
    /** port number */
    protected int port = 0;
    protected BufferedReader inSocket;
    protected PrintWriter outSocket;
    protected Thread receivingThread;
    private boolean working = true;
    protected int sleepThreadTime = 1000;

    /** */
    public SocketServerClient(AgentServerSocket parentServer, java.net.ServerSocket serverSocket, Socket socket) {
        this.parentServer = parentServer;
        this.serverSocket = serverSocket;
        //socket = new Socket("", 9998);
        this.socket = socket;
        initialize();
    }
    public boolean initialize() {
        try {
            System.out.println("Initializing socket for server");
            socket.setSoTimeout(1000);
            host = socket.getInetAddress().getHostAddress();
            port = socket.getPort();
            inSocket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outSocket = new PrintWriter(socket.getOutputStream(),true);
            working = true;
            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
            }
            receivingThread = new Thread(this);
            receivingThread.start();
            return true;
        } catch (SocketException ex) {
            log.info(" SockedException at Start("+socket.getPort()+","+socket.getInetAddress().getHostAddress()+"): "+ex.getMessage());
            return false;
        } catch (IOException ex) {
            log.info(" IOException at Start("+socket.getPort()+","+socket.getInetAddress().getHostAddress()+"): "+ex.getMessage());
            return false;
        }
    }

    public void threadWork() {
        try {
            if (!working) {
                log.info("Socket thread is not working, trying to reconnect");
                //reconnect();
                return;
            }
            if (inSocket == null) {
                log.info("inSocket is null, trying to reconnect");
                working = false;
                //reconnect();
                return;
            }
            String readLine = inSocket.readLine();
            System.out.println("Socket read line: " +readLine);
        }  catch (java.net.SocketTimeoutException ex) {
        }  catch (IOException ex) {
            working = false;
            log.info(" Exception while reading from socket: "+ex.getMessage());
        } catch (Exception ex) {
            working = false;
            log.info(" Exception in Socket Client: "+ex.getMessage()+"; "+ex.getLocalizedMessage());
        }

    }

    public void close() {
        try {
            working = false;
            log.info("Closing socket for server: " + parentServer.getServerGuid());
            if (inSocket != null)
                inSocket.close();
            if (outSocket != null)
                outSocket.close();
            if (socket != null)
                socket.close();
        }
        catch (IOException ex) {
            log.info(" Error while closing Socket connection, reason: "+ex.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException ex) {
            log.info(" Plugin thread stopped before started ");
        }
        log.info(" Plugin thread begin ");
        while (!working) {
            threadWork();
            try {
                Thread.sleep(sleepThreadTime);
            } catch (InterruptedException ex) {
            }
            // TODO: check if socket is still valid OR there is a need to reconnect
        }
        log.info(" Plugin thread stopped (end) ");
    }

}