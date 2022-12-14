package com.cache.agent.clients;

import com.cache.api.*;
import com.cache.base.AgentClientBase;
import com.cache.base.dtos.DistAgentServerRow;
import com.cache.interfaces.Agent;
import com.cache.interfaces.AgentClient;
import com.cache.interfaces.HttpCallable;
import com.cache.utils.HttpConnectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** HTTP client with client communications */
public class HttpClient extends AgentClientBase implements AgentClient {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(HttpClient.class);
    private String url;
    private HttpCallable httpConnectionHelper;

    /** creates new HTTP client  */
    public HttpClient(Agent parentAgent, DistAgentServerRow srv) {
        super(parentAgent);
        this.url = srv.serverurl;
        connectedAgentGuid = srv.agentguid;
        httpConnectionHelper = HttpConnectionHelper.createHttpClient(url);
        log.info("Creates new HTTP client for server: " + srv.servertype + ", url: " + srv.serverurl);
        initialize();
    }
    /** get type of client - socket, http, datagram, ... */
    public DistClientType getClientType() {
        return DistClientType.http;
    }
    /** get unified URL of this client */
    public String getUrl() {
        return url;
    }
    /** initialize client - connecting or reconnecting */
    public boolean initialize() {
        try {
            log.info("Initializing HTTP client for agent: " + parentAgent.getAgentGuid() + ", URL: " + url + ", client UID: " + clientGuid);
            AgentWelcomeMessage welcome = new AgentWelcomeMessage(parentAgent.getAgentInfo(), getClientInfo());
            DistMessage welcomeMsg = DistMessage.createMessage(DistMessageType.system, parentAgent.getAgentGuid(), DistServiceType.agent, connectedAgentGuid, DistServiceType.agent, "welcome",  welcome);
            send(welcomeMsg);
            return true;
        } catch (Exception ex) {
            log.warn("Cannot initialize client " + clientGuid + ", agent: " + this.parentAgent.getAgentGuid() + ", Exception at Start: "+ex.getMessage(), ex);
            return false;
        }
    }

    /** send message to this client */
    public boolean send(DistMessage msg) {
        try {
            String line = parentAgent.getSerializer().serializeToString(msg);
            log.trace("Writing line to be sent using HTTP client: " + clientGuid + ", LINE=" + line + ", serializer: " + parentAgent.getSerializer().getClass().getName() + ", message: " + msg.toString());
            var res = httpConnectionHelper.callPostText("", line);
            return res.isOk();
        } catch (Exception ex) {
            log.warn("ERROR WHILE SENDING DATA FOR CLIENT: " + clientGuid + ", reason: " + ex.getMessage(), ex);
            parentAgent.getAgentIssues().addIssue("HttpClient.send", ex);
            return false;
        }
    }

    /** close this client */
    public void close() {
        log.info("Closing HTTP client for GUID: " + clientGuid);
        try {
            AgentWelcomeMessage welcome = new AgentWelcomeMessage(parentAgent.getAgentInfo(), getClientInfo());
            DistMessage closeMsg = DistMessage.createMessage(DistMessageType.system, parentAgent.getAgentGuid(), DistServiceType.agent, connectedAgentGuid, DistServiceType.agent, "close",  welcome);
            send(closeMsg);
            working = false;
        } catch (Exception ex) {
            log.info(" Error while closing HTTP client connection, reason: "+ex.getMessage());

        }
    }

}