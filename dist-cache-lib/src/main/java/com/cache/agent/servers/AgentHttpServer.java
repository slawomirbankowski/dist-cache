package com.cache.agent.servers;

import com.cache.interfaces.Agent;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

/** */
public class AgentHttpServer {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(AgentServerSocket.class);
    private com.sun.net.httpserver.HttpServer httpServer;
    private AgentHttpHandler httpHandler;
    private Agent parentAgent;

    public AgentHttpServer(Agent parentAgent) {
        try {
            int httpPort = parentAgent.getConfig().getPropertyAsInt("AGENT_HTTP_PORT", 9941);
            this.parentAgent = parentAgent;
            log.info("Starting new HTTP server at port:" + httpPort + ", agent: " + parentAgent.getAgentGuid());
            httpServer = HttpServer.create(new InetSocketAddress(httpPort), 0);
            httpHandler = new AgentHttpHandler(parentAgent);
            httpServer.createContext("/", httpHandler);
            httpServer.setExecutor(null);
            httpServer.start();
            log.info("Started HTTP server!!!");
        } catch (Exception ex) {
            log.info("Cannot start HTTP server, reason: " + ex.getMessage());
        }
    }

    static class AgentHttpHandler implements HttpHandler {
        private Agent parentAgent;
        public AgentHttpHandler(Agent parentAgent) {
            this.parentAgent = parentAgent;
        }
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "This is the response";
            t.getRequestBody();
            t.getRequestHeaders();
            t.getProtocol();
            t.getRequestMethod();
            // TODO: get request, parse it and send to agent
            parentAgent.getAgentGuid();
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }


}
