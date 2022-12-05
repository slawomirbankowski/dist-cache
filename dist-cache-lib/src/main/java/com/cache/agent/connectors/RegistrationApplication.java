package com.cache.agent.connectors;

import com.cache.agent.AgentInstance;
import com.cache.api.*;
import com.cache.base.RegistrationBase;
import com.cache.utils.HttpConnectionHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

/** connector to global dist-cache application - central point with registering/unregistering agents  */
public class RegistrationApplication extends RegistrationBase {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(RegistrationApplication.class);

    private String urlString;
    /** HTTP connection helper */
    private HttpConnectionHelper applicationConn = null;

    public RegistrationApplication(AgentInstance parentAgent) {
        super(parentAgent);

    }
    /** run for initialization in classes */
    @Override
    public void onInitialize() {
        urlString = parentAgent.getConfig().getProperty(DistConfig.CACHE_APPLICATION_URL);
        try {
            log.info("Connecting to dist-cache application, URL: " + urlString);
            applicationConn = new HttpConnectionHelper(urlString);
        } catch (Exception ex) {
            log.warn("Cannot connect to dist-cache application, reason: " + ex.getMessage(), ex);
        }
    }
    @Override
    protected boolean onIsConnected() {
        return false;
    }
    @Override
    protected AgentConfirmation onAgentRegister(AgentRegister register) {
        try {
            log.info("Try to register agent as dist-cache application");
            ObjectMapper mapper = JsonMapper.builder()
                    .findAndAddModules()
                    .build();
            String registerBody = mapper.writeValueAsString(register);
            applicationConn = new HttpConnectionHelper(urlString);
            log.info("Try to register agent with endpoint /agent and body: " + registerBody);
            var response = applicationConn.callHttpPut("/v1/agent", registerBody);

            // TODO: save response from application
            log.info("Got response from APP: " + response.getInfo());
            return null;
        } catch (Exception ex) {
            log.warn("Cannot connect to dist-cache application, reason: " + ex.getMessage(), ex);
            return null;
        }
    }
    protected AgentConfirmation onAgentUnregister(String agentGuid) {
        return new AgentConfirmation(agentGuid, true, false, 0, List.of());
    }
    @Override
    protected AgentPingResponse onAgentPing(AgentPing ping) {
        // TODO: implement ping to connector from this agent

        return null;
    }
    /** get list of agents from connector */
    @Override
    protected List<AgentSimplified> onGetAgents() {
        applicationConn.callHttpGet("");

        return null;
    }
    /** get list of active agents */
    public List<AgentSimplified> getAgentsActive() {
        return null;
    }

    /** close current connector */
    @Override
    protected void onClose() {
        // TODO: implement closing this connector
    }

}
