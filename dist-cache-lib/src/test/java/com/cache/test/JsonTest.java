package com.cache.test;

import com.cache.api.CacheObjectRequest;
import com.cache.util.JsonUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonTest {
    private static final Logger log = LoggerFactory.getLogger(JsonTest.class);

    @Test
    public void jsonTest() {
        log.info("START ------ agent Web API test");
        CacheObjectRequest cor1 = new CacheObjectRequest("key1", "value1", "priority=3,ttl=100000", Set.of("group1", "group2"));
        log.info("COR1=" + cor1);
        String json = JsonUtils.serialize(cor1);
        log.info("COR_JSON=" + json);
        CacheObjectRequest cor2 = JsonUtils.deserialize(json, CacheObjectRequest.class);
        log.info("COR2=" + cor2);

        log.info("END-- ---");
    }
}
