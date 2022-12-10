package com.cache.agent.clients;


import com.cache.dtos.DistAgentServerRow;
import com.cache.interfaces.Agent;
import com.cache.interfaces.AgentClient;
import com.cache.utils.CacheUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

/** socket client with client communications */
public class SocketServerClient implements AgentClient, Runnable {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(SocketServerClient.class);
    /** unique ID of this client */
    private final String clientGuid = CacheUtils.generateClientGuid("SocketServerClient");
    protected Socket socket;
    protected boolean isServer;
    /** name of connected host */
    protected String host = "";
    /** port number */
    protected int port = 0;
    protected BufferedReader inSocket;
    protected PrintWriter outSocket;
    protected Thread receivingThread;
    private boolean working = true;
    protected int sleepThreadTime = 1000; // TODO: change this to be configured

    /** creates new socket client */
    public SocketServerClient(Agent parentAgent, Socket socket) {
        this.socket = socket;
        isServer = true;
        initialize();
    }
    public SocketServerClient(Agent parentAgent, DistAgentServerRow srv) {
        try {
            log.info("Creates new socket client for server: " + srv.servertype + ", host: " + srv.serverhost + ", port: " + srv.serverport);
            isServer = false;
            this.socket = new Socket(srv.serverhost, srv.serverport);
        } catch (Exception ex) {
            log.warn("Cannot initialize socket to host: " + srv.serverhost);
        }
        initialize();
    }
    /** initialize client - connecting or reconnecting */
    public boolean initialize() {
        try {
            socket.setSoTimeout(1000);
            host = socket.getInetAddress().getHostAddress();
            port = socket.getPort();
            log.info("Initializing socket client, server: " + isServer + ", host: " + host + ", port: " + port + ", GUID: " + clientGuid);
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

    /** get GUID for this client */
    public String getClientGuid() {
        return clientGuid;
    }

    /** true if client is still working */
    public boolean isWorking() {
        return working;
    }

    public void threadWork() {
        try {
            if (!working) {
                log.warn("Socket thread is not working, trying to reconnect");
                //reconnect();
                return;
            }
            if (inSocket == null) {
                log.warn("inSocket is null, trying to reconnect");
                working = false;
                //reconnect();
                return;
            }
            String readLine = inSocket.readLine();
            log.info("Socket read line: " +readLine);
        }  catch (java.net.SocketTimeoutException ex) {
        }  catch (IOException ex) {
            working = false;
            log.info(" Exception while reading from socket: "+ex.getMessage());
        } catch (Exception ex) {
            working = false;
            log.info(" Exception in Socket Client: "+ex.getMessage()+"; "+ex.getLocalizedMessage());
        }

    }

    /** close this client */
    public void close() {
        try {
            working = false;
            log.info("Closing socket for GUID: " + clientGuid);
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
        log.info("Starting thread for socket client on port: " + port + ", GUID: " + clientGuid);
        try {
            Thread.sleep(200);
        } catch (InterruptedException ex) {
            log.warn(" Plugin thread stopped before started for client: " + clientGuid);
        }
        while (working) {
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