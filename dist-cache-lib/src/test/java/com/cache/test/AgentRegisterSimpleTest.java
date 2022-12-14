package com.cache.test;

import com.cache.DistFactory;
import com.cache.api.CacheMode;
import com.cache.interfaces.Agent;
import com.cache.utils.DistUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

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
                .withRegisterCleanAfter(CacheMode.TIME_FIVE_MINUTES, CacheMode.TIME_ONE_DAY)
                .withTimerRegistrationPeriod(1000)
                .withTimerServerPeriod(1000)
                .createAgentInstance();

        assertNotNull(agent1, "Created agent should not be null");

        log.info("======-----> Agents [1]: " + agent1.getAgentRegistrations().getAgents().size() + ", servers: " + agent1.getAgentServices().getServices().size());
        assertEquals(1, agent1.getAgentServices().getServices().size(), "There should be 1 server");
        assertEquals(0, agent1.getAgentRegistrations().getAgents().size(), "There should be 0 agents");
        assertEquals(1, agent1.getAgentRegistrations().getRegistrationsCount(), "There should be 1 registration service");

        DistUtils.sleep(3000);
        log.info("======-----> Agents [2]: " + agent1.getAgentRegistrations().getAgents().size() + ", servers: " + agent1.getAgentServices().getServices().size());
        assertEquals(1, agent1.getAgentServices().getServices().size(), "There should be 1 server");
        assertEquals(1, agent1.getAgentRegistrations().getAgents().size(), "There should be 1 agent");
        assertEquals(1, agent1.getAgentRegistrations().getRegistrationsCount(), "There should be 1 registration service");

        agent1.close();
        assertTrue(agent1.isClosed(), "agent1 should be closed");

        log.info("END-----");
    }
}
