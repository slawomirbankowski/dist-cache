package com.cache.agent.apis;

import com.cache.api.AgentWebApiRequest;
import com.cache.api.AgentWebApiResponse;
import com.cache.api.DistConfig;
import com.cache.base.AgentWebApi;
import com.cache.interfaces.AgentApi;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.Executors;

/** simple implementation of web api for agent
 * Web API is simple REST api to access direct agent services with methods
 * It is creating HTTP server that is redirecting request to proper service based on URI
 * Agent Web API URI must be like:
 *  http(s)://host[:port]/SERVICE/METHOD
 * Where:
 * SERVICE = agent | cache | report |
 * METHOD = method in service
 * */
public class WebSimpleApi extends AgentWebApi {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(WebSimpleApi.class);
    /** parent API class to execute request methods */
    private final AgentApi parentApi;
    /** HTTP server for Agent Web API */
    private com.sun.net.httpserver.HttpServer httpServer;
    /** handler to handle HTTP requests */
    private WebSimpleApi.WebSimpleApiHandler httpHandler;
    /** port of this Web API server */
    private final int webApiPort;

    /** creates new Web API and initialize it */
    public WebSimpleApi(AgentApi api) {
        parentApi = api;
        webApiPort = parentApi.getAgent().getConfig().getPropertyAsInt(DistConfig.AGENT_API_PORT, 9900);
        startApi();
    }
    /** start this Agent Web API */
    public void startApi() {
        try {
            log.info("Starting new Web API for agent as HTTP server at port:" + webApiPort + ", agent: " + parentApi.getAgent().getAgentGuid());
            httpServer = HttpServer.create(new InetSocketAddress(webApiPort), 0);
            httpHandler = new WebSimpleApi.WebSimpleApiHandler(parentApi);
            httpServer.createContext("/", httpHandler);
            httpServer.setExecutor(Executors.newFixedThreadPool(10)); // set default thread as Executor
            httpServer.start();
            log.info("Started HTTP server as Agent Web API on port: " + webApiPort +", agent: " + parentApi.getAgent().getAgentGuid());
        } catch (Exception ex) {
            log.warn("Cannot start HTTP server as Agent Web API, reason: " + ex.getMessage());
            parentApi.getAgent().getAgentIssues().addIssue("WebSimpleApi.startApi", ex);
        }
    }
    /** get type of this Agent Web API */
    public String getApiType() {
        return "simple";
    }
    /** close this Agent Web API */
    public void close() {
        try {
            log.info("Try to close Agent Web API");
            httpServer.stop(3);
        } catch (Exception ex) {
            parentApi.getAgent().getAgentIssues().addIssue("WebSimpleApi.close", ex);
        }
    }
    /** handler of HTTP requests */
    static class WebSimpleApiHandler implements HttpHandler {
        /** local logger for this class*/
        protected static final Logger log = LoggerFactory.getLogger(WebSimpleApiHandler.class);
        /** parent API for this web server */
        private AgentApi parentApi;
        public WebSimpleApiHandler(AgentApi parentApi) {
            this.parentApi = parentApi;
        }
        /** handle HTTP request */
        @Override
        public void handle(HttpExchange t) throws IOException {
            long reqSeq = AgentWebApi.requestSeq.incrementAndGet();
            long startTime = System.currentTimeMillis();
            try {
                AgentWebApiRequest req = new AgentWebApiRequest(reqSeq, startTime, t.getProtocol(), t.getRequestMethod(), t.getRequestURI(), t.getRequestHeaders(), t.getRequestBody().readAllBytes());
                log.info(">>>>> HANDLE REQUEST [" + reqSeq + "], protocol: " + t.getProtocol() + ", method: " + t.getRequestMethod() + ", HEADERS.size: " + t.getRequestHeaders().size() + ", URI: " + t.getRequestURI().toString() + ", service: " + req.getServiceName());
                AgentWebApiResponse response = parentApi.getAgent().getAgentServices().handleRequest(req);
                var respBytes = response.getResponseContent();
                t.getResponseHeaders().putAll(response.getHeaders());
                long totalTime = System.currentTimeMillis() - startTime;
                t.getResponseHeaders().put("Request-Time", List.of(""+totalTime));
                t.getResponseHeaders().put("Request-Seq", List.of(""+reqSeq));
                t.getResponseHeaders().put("Request-User", List.of("ANONYMOUS")); // TODO: add Authorization to request-response
                t.getResponseHeaders().put("Agent-Guid", List.of(parentApi.getAgent().getAgentGuid()));
                t.sendResponseHeaders(response.getResponseCode(), respBytes.length);
                OutputStream os = t.getResponseBody();
                os.write(respBytes);
                os.flush();
                log.info(">>>>> END OF HANDLE REQUEST[" + reqSeq + "], totalTime: " + totalTime + ", code: " + response.getResponseCode() + " content.len: " + response.getResponseContent().length + ", headers: " + t.getResponseHeaders().size());
                os.close();
            } catch (Exception ex) {
                log.warn("Web API Exception: " + ex.getMessage());
                parentApi.getAgent().getAgentIssues().addIssue("WebSimpleApiHandler.handle", ex);
                String errorResponse = ">>>>> ERROR DURING REQUEST [" + reqSeq +  "] +, reason: " + ex.getMessage();
                t.sendResponseHeaders(501, errorResponse.length());
                OutputStream os = t.getResponseBody();
                os.write(errorResponse.getBytes());
                os.flush();
            }
        }
    }
}
