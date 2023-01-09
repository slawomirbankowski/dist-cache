package com.cache.test;

import com.cache.DistFactory;
import com.cache.api.DaoParams;
import com.cache.dao.DaoKafkaBase;
import com.cache.interfaces.Agent;
import com.cache.interfaces.IssueHandler;
import com.cache.utils.DistUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DaoKafkaTest {
    private static final Logger log = LoggerFactory.getLogger(DaoKafkaTest.class);

    @Test
    public void kafkaDaoTest() {
        log.info("START ------ Kafka DAO test");

        Agent agent = DistFactory.buildEmptyFactory()
                .createAgentInstance();

        DaoKafkaBase dao = new DaoKafkaBase(DaoParams.kafkaParams("localhost:9092", 1, (short) 0x1, "agent-1", "agent-1"), agent);
        String testTopicName = "dist-agent-register-tmp";
        var topics = dao.getTopics();
        log.info("Got topics: " + topics.size());
        topics.stream().forEach(t -> log.info("---> TOPIC:" + t));

        boolean created = dao.createTopic(testTopicName);
        log.info("Created topic: " + created);

        var topicsAfter = dao.getTopics();
        log.info("Got topics after: " + topicsAfter.size());
        topicsAfter.stream().forEach(t -> log.info("---> TOPIC:" + t));

        dao.createKafkaConsumer(testTopicName, msg -> {
            log.info("=======> RECEIVE MESSAGE: " + msg.key() + ", topic: " + msg.topic() + ", value: " + msg.value() + ", offset: " + msg.offset());
            return true;
        });

        for (int i=0; i<50; i++) {
            String key = "agent" + i;
            log.info("=======> SEND MESSAGE: " + key);
            dao.send(testTopicName, key, "agent" + i + "value");
            DistUtils.sleep(10);
        }
        DistUtils.sleep(1000);

        dao.close();

        agent.close();

        log.info("END-----");
    }
}
