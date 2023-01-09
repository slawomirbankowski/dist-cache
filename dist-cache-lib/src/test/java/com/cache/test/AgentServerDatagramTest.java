package com.cache.test;

import com.cache.DistFactory;
import com.cache.api.CacheMode;
import com.cache.api.enums.DistCallbackType;
import com.cache.api.DistCallbacks;
import com.cache.api.enums.DistServiceType;
import com.cache.interfaces.Agent;
import com.cache.utils.DistUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

public class AgentServerDatagramTest {
    private static final Logger log = LoggerFactory.getLogger(AgentServerDatagramTest.class);

    @Test
    public void agentRegisterSimpleTest() {
        log.info("START ------ agent register test test");
        Agent agent1 = DistFactory.buildEmptyFactory()
                .withName("GlobalAgent")
                .withRegistrationJdbc("jdbc:postgresql://localhost:5432/cache01", "org.postgresql.Driver",
                        "cache_user", "${JDBC_PASS}")
                .withRegisterCleanAfter(CacheMode.TIME_FIVE_MINUTES, CacheMode.TIME_ONE_DAY)
                .withServerDatagramPort(9911)
                .withSerializer("java.lang.String=StringSerializer,default=ObjectStreamSerializer")
                .withTimerRegistrationPeriod(1000)
                .withTimerServerPeriod(1000)
                .createAgentInstance();

        Agent agent2 = DistFactory.buildEmptyFactory()
                .withName("GlobalAgent")
                .withRegistrationJdbc("jdbc:postgresql://localhost:5432/cache01", "org.postgresql.Driver",
                        "cache_user", "cache_password123")
                .withRegisterCleanAfter(CacheMode.TIME_FIVE_MINUTES, CacheMode.TIME_ONE_DAY)
                .withServerDatagramPort(9912)
                .withSerializer("java.lang.String=StringSerializer,default=ObjectStreamSerializer")
                .withTimerRegistrationPeriod(1000)
                .withTimerServerPeriod(1000)
                .createAgentInstance();

        assertNotNull(agent1, "Created agent should not be null");
        assertNotNull(agent2, "Created agent should not be null");

        log.info("======-----> Agent1 [1]: " + agent1.getAgentRegistrations().getAgents().size() + ", servers: " + agent1.getAgentServices().getServices().size());
        assertEquals(1, agent1.getAgentServices().getServices().size(), "There should be 1 server");
        assertEquals(0, agent1.getAgentRegistrations().getAgents().size(), "There should be 0 agents");
        assertEquals(1, agent1.getAgentRegistrations().getRegistrationsCount(), "There should be 1 registration service");
        log.info("======-----> Agent2 [1]: " + agent2.getAgentRegistrations().getAgents().size() + ", servers: " + agent2.getAgentServices().getServices().size());
        assertEquals(1, agent2.getAgentServices().getServices().size(), "There should be 1 server");
        assertEquals(0, agent2.getAgentRegistrations().getAgents().size(), "There should be 0 agents");
        assertEquals(1, agent2.getAgentRegistrations().getRegistrationsCount(), "There should be 1 registration service");

//com.cache.api.enums.DistServiceType fromService, DistServiceType toService, String method, Object message, DistCallbacks

        DistUtils.sleep(3000);
        log.info("======-----> Agent1 [2]: " + agent1.getAgentRegistrations().getAgents().size() + ", servers: " + agent1.getAgentServices().getServices().size());
        assertEquals(1, agent1.getAgentServices().getServices().size(), "There should be 1 server");
        assertEquals(2, agent1.getAgentRegistrations().getAgents().size(), "There should be 2 agents");
        assertEquals(1, agent1.getAgentRegistrations().getRegistrationsCount(), "There should be 1 registration service");
        log.info("======-----> Agent2 [2]: " + agent2.getAgentRegistrations().getAgents().size() + ", servers: " + agent2.getAgentServices().getServices().size());
        assertEquals(1, agent2.getAgentServices().getServices().size(), "There should be 1 server");
        assertEquals(2, agent2.getAgentRegistrations().getAgents().size(), "There should be 2 agents");
        assertEquals(1, agent2.getAgentRegistrations().getRegistrationsCount(), "There should be 1 registration service");

        DistUtils.sleep(3000);

        agent1.sendMessageBroadcast(DistServiceType.agent, DistServiceType.agent, "ping", "ping", DistCallbacks.createEmpty().addCallback(DistCallbackType.onResponse, x -> { System.out.println("GOT PONG"); return true; } ));

        agent1.close();
        assertTrue(agent1.isClosed(), "agent1 should be closed");

        log.info("END-----");
    }
}
