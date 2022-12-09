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

        Agent agent2 = DistFactory.buildEmptyFactory()
                .withName("GlobalAgent")
                .withRegistrationJdbc("jdbc:postgresql://localhost:5432/cache01", "org.postgresql.Driver",
                        "cache_user", "cache_password123")
                .withServerSocketPort(9902)
                .createAgentInstance();

        assertNotNull(agent1, "Created agent1 should not be null");
        assertNotNull(agent1, "Created agent2 should not be null");
        for (int i=0; i<3; i++) {
            log.info("SLEEPING");
            CacheUtils.sleep(60000);
            System.out.println("-----> Agents1: " + agent1.getAgents().size() + ", servers: " + agent1.getServices().size());
            System.out.println("-----> Agents2: " + agent2.getAgents().size() + ", servers: " + agent2.getServices().size());
        }
        agent1.close();
        agent2.close();

        assertTrue(agent1.isClosed(), "agent1 should be closed");
        assertTrue(agent2.isClosed(), "agent2 should be closed");
        log.info("END-----");
    }
}