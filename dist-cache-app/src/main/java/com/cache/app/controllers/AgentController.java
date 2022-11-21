package com.cache.app.controllers;

import com.cache.api.*;
import com.cache.app.services.AgentService;
import com.cache.app.services.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/** Controller to have endpoints to manipulate and */
@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class AgentController {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(AgentController.class);
    @Autowired
    protected AgentService agentService;

    /** get list of agents - only simplified information about basic things */
    @GetMapping(value = "/agents", consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AgentSimplified> getAgents() {
        return  agentService.getAgents();
    }

    /** get agent by id */
    @GetMapping(value = "/agent/{id}", consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AgentSimplified> getAgentById(@PathVariable("id") final String id) {
        return agentService.getAgentById(id);
    }

    /** register new agent */
    @PutMapping(value = "/agent", consumes = MediaType.APPLICATION_JSON_VALUE)
    public AgentConfirmation registerAgent(
            @RequestBody AgentRegister register) {
        log.info("Register agent: " + register.agentGuid + " from host: " + register.hostName + "/" + register.hostIp + ":" + register.port);
        AgentConfirmation confirmation = agentService.registerAgent(register);
        return confirmation;
    }

    /** ping from agent */
    @PostMapping(value = "/agent", consumes = MediaType.APPLICATION_JSON_VALUE)
    public AgentPingResponse pingAgent(@RequestBody AgentPing pingObject) {
        return agentService.pingAgent(pingObject);
    }

    /** delete agent by id */
    @DeleteMapping("/agent/{id}")
    public AgentConfirmation deleteAgent(@PathVariable("id") final String id) {
        return agentService.unregisterAgent(id);
    }

}
