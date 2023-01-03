package com.cache.test;

import com.cache.base.DaoElasticsearchBase;
import com.cache.base.DaoKafkaBase;
import com.cache.interfaces.IssueHandler;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

public class DaoKafkaTest {
    private static final Logger log = LoggerFactory.getLogger(DaoKafkaTest.class);

    @Test
    public void kafkaDaoTest() {
        log.info("START ------ Kafka DAO test");
        IssueHandler issueHandler = new IssueHandler() {
            @Override
            public void addIssue(String methodName, Exception ex) {
                log.warn("ISSUE in method: " + methodName + ", reason: " + ex.getMessage(), ex);
            }
        };
        DaoKafkaBase dao = new DaoKafkaBase("localhost:9092", 1, (short) 0x1, "agent-1", "agent-1", issueHandler);

        var topics = dao.getTopics();
        log.info("Got topics: " + topics.size());
        topics.stream().forEach(t -> log.info("---> TOPIC:" + t));

        boolean created = dao.createTopic("dist-agent-register");
        log.info("Created topic: " + created);

        var topicsAfter = dao.getTopics();
        log.info("Got topics after: " + topicsAfter.size());
        topicsAfter.stream().forEach(t -> log.info("---> TOPIC:" + t));

        dao.createKafkaConsumer("dist-agent-register", x -> "");

        dao.close();

        log.info("END-----");
    }
}
