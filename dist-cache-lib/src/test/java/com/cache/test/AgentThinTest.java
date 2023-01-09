package com.cache.test;

import com.cache.DistFactory;
import com.cache.api.DistCallbacks;
import com.cache.api.enums.DistCallbackType;
import com.cache.api.enums.DistServiceType;
import com.cache.interfaces.Agent;
import com.cache.utils.DistUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

public class AgentThinTest {
    private static final Logger log = LoggerFactory.getLogger(AgentThinTest.class);

    /** simple test for very thin Agent that is not holding anything, not doing anything */
    @Test
    public void agentRegisterTest() {
        log.info("START ------ agent thin test");
        Agent agent = DistFactory.buildEmptyFactory()
                .createAgentInstance();
        assertNotNull(agent, "Created agent should not be null");
        assertEquals(0, agent.getAgentRegistrations().getAgents().size(), "There should be 0 agents known by agent");
        assertEquals(0, agent.getAgentServices().getServicesCount(), "There should be 0 services");
        assertEquals(0, agent.getAgentConnectors().getClientsCount(), "There should be 0 clients");
        assertEquals(0, agent.getAgentIssues().getIssues().size(), "There should be 0 issues");
        assertEquals(0, agent.getAgentThreads().getThreadsCount(), "There should be 0 threads");
        assertEquals(0, agent.getAgentRegistrations().getRegistrationsCount(), "There should be 0 registrations");
        agent.close();
        assertTrue(agent.isClosed(), "agent should be closed");
        log.info("END-----");
    }
}
