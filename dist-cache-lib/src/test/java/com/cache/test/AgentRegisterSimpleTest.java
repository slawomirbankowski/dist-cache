package com.cache.test;

import com.cache.DistFactory;
import com.cache.interfaces.Agent;
import com.cache.utils.CacheUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AgentRegisterSimpleTest {
    private static final Logger log = LoggerFactory.getLogger(AgentRegisterSimpleTest.class);

    @Test
    public void agentRegisterSimpleTest() {
        log.info("START ------ agent register test test");
        Agent agent1 = DistFactory.buildEmptyFactory()
                .withName("GlobalAgent")
                .withRegistrationJdbc("jdbc:postgresql://localhost:5432/cache01", "org.postgresql.Driver",
                        "cache_user", "cache_password123")
                .withServerSocketPort(9901)
                .createAgentInstance();
       // agent1.getConfig().saveToJson();
        agent1.getAgentRegistrations().getAgents();
        assertNotNull(agent1, "Created agent should not be null");

        for (int i=0; i<2; i++) {
            log.info("SLEEPING");
            CacheUtils.sleep(60000);
            log.info("-----> Agents1: " + agent1.getAgentRegistrations().getAgents().size() + ", servers: " + agent1.getAgentServices().getServices().size());
        }
        agent1.close();

        assertTrue(agent1.isClosed(), "agent1 should be closed");
        log.info("END-----");
    }
}
