package com.cache.agent.servers;

import com.cache.api.enums.DistClientType;
import com.cache.api.DistConfig;
import com.cache.api.DistMessage;
import com.cache.base.ServerBase;
import com.cache.interfaces.Agent;
import com.cache.interfaces.AgentServer;
import com.cache.utils.DistUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.*;

/**
 * Server for communication between Agents based on UDP packets - datagrams. */
public class AgentDatagramServer extends ServerBase implements AgentServer, Runnable {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(AgentServerSocket.class);

    /** Datagram server - UDP to receive packets/ datagram */
    private DatagramSocket datagramSocket;
    private int workingPort;
    private String url;
    private Thread mainThread;

    public AgentDatagramServer(Agent parentAgent) {
        super(parentAgent);
        try {
            workingPort = parentAgent.getConfig().getPropertyAsInt(DistConfig.AGENT_SERVER_DATAGRAM_PORT, DistConfig.AGENT_SERVER_DATAGRAM_PORT_DEFAULT_VALUE);
            int soTimeout = parentAgent.getConfig().getPropertyAsInt(DistConfig.AGENT_SERVER_DATAGRAM_TIMEOUT,1000);
            datagramSocket = new DatagramSocket(workingPort);
            datagramSocket.setSoTimeout(soTimeout);
            url = "udp://" + DistUtils.getCurrentHostName() + ":" + workingPort + "/";
            mainThread = new Thread(this);
            mainThread.setDaemon(true);
            mainThread.start();
            parentAgent.getAgentThreads().registerThread(this, mainThread, "datagram-server-" + workingPort);
            log.info("Started new DATAGRAM server at port:" + workingPort + ", timeout: " + soTimeout + ", agent: " + parentAgent.getAgentGuid());
        } catch (Exception ex) {
            log.info("Cannot start DATAGRAM server, reason: " + ex.getMessage());
            parentAgent.getAgentIssues().addIssue("AgentDatagramServer", ex);
        }
    }
    /** get type of clients to be connected to this server */
    public DistClientType getClientType() {
        return DistClientType.datagram;
    }
    /** get port of this server */
    public int getPort() {
        return workingPort;
    }
    /** get URL of this server */
    public String getUrl() {
        return url;
    }

    /** run in separated thread to get datagram packets */
    public void run() {
        log.info("......... Accepting UDP connections on port " + workingPort);
        while (!closed) {
            try {
                byte[] receiveBuff = new byte[4096];
                DatagramPacket packet = new DatagramPacket(receiveBuff, receiveBuff.length);
                datagramSocket.receive(packet);
                byte[] received = packet.getData();
                if (received != null && received.length > 0) {
                    DistMessage receivedMsg = (DistMessage)parentAgent.getSerializer().deserialize(DistMessage.class.getName(), received);
                    log.info("......... Got message from from UDP:  " + receivedMsg.toString());
                }
            } catch (IOException ex) {

            } catch (Exception ex) {
                log.error("!!!!! Unknown exception on Datagram server working on port " + workingPort);
            }
            DistUtils.sleep(2000);
        }
    }

    @Override
    public void close() {
        try {
            log.info("Try to close DATAGRAM server for Agent: " + parentAgent.getAgentGuid());
            datagramSocket.close();
            mainThread.join(2000);
        } catch (Exception ex) {
            log.warn("");
            parentAgent.getAgentIssues().addIssue("AgentDatagramServer.close", ex);

        }
    }


}
