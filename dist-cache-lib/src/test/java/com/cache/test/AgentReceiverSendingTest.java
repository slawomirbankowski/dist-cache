package com.cache.test;

import com.cache.DistFactory;
import com.cache.api.DistCallbacks;
import com.cache.api.enums.DistCallbackType;
import com.cache.api.enums.DistServiceType;
import com.cache.interfaces.Agent;
import com.cache.interfaces.Receiver;
import com.cache.utils.DistUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

public class AgentReceiverSendingTest {
    private static final Logger log = LoggerFactory.getLogger(AgentReceiverSendingTest.class);

    @Test
    public void agentReceiverSendingTest() {
        log.info("START ------ agent receiver sending test");

        Agent agent1 = DistFactory.buildEmptyFactory()
                .withName("GlobalAgent")
                .withWebApiPort(9999)
                .withRegistrationJdbc("jdbc:postgresql://localhost:5432/cache01", "org.postgresql.Driver",
                        "cache_user", "${JDBC_PASS}")
                .withServerSocketPort(9901)
                .withTimerStorageClean(1000)
                .withTimerRegistrationPeriod(1000)
                .withTimerServerPeriod(1000)
                .createAgentInstance();

        Agent agent2 = DistFactory.buildEmptyFactory()
                .withName("GlobalAgent")
                .withWebApiPort(9998)
                .withRegistrationJdbc("jdbc:postgresql://localhost:5432/cache01", "org.postgresql.Driver",
                        "cache_user", "${JDBC_PASS}")
                .withServerSocketPort(9902)
                .withTimerStorageClean(1000)
                .withTimerRegistrationPeriod(1000)
                .withTimerServerPeriod(1000)
                .createAgentInstance();

        Receiver receiver = agent1.getAgentServices().getReceiver();
        receiver.registerReceiverMethod("", null);

        int maxTime = 3;
        for (int t=0; t<maxTime; t++) {
            log.info("TIME IS RUNNING................................ minutes: " + t + " of " + maxTime);
            DistUtils.sleep(60000);
        }

        log.info("==================================================================================================//////////////////////////////////////////////////////////////////////////////////////////////////////////////========================");
        log.info("========--------> CLOSING TEST");
        agent1.close();
        agent2.close();
        assertTrue(agent1.isClosed(), "agent1 should be closed");
        assertTrue(agent2.isClosed(), "agent2 should be closed");
        log.info("END-----");
    }
}