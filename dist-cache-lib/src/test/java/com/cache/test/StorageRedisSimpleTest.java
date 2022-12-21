package com.cache.test;

import com.cache.DistFactory;
import com.cache.interfaces.Agent;
import com.cache.utils.CacheUtils;
import net.bytebuddy.implementation.bytecode.constant.MethodConstant;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import redis.clients.jedis.Jedis;
//import redis.clients.jedis.params.SetParams;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StorageRedisSimpleTest {
    private static final Logger log = LoggerFactory.getLogger(StorageRedisSimpleTest.class);

    @Test
    public void redisSimpleTest() {
        log.info("START ------ ");
        String host = "localhost";
        /*
        int port = 6379;
        Jedis jedis = new Jedis(host, port);
        log.info("Redis client ID: " + jedis.clientId());
        log.info("Redis client info: " + jedis.clientInfo());
        log.info("Redis client name: " + jedis.clientGetname());
        //log.info("Redis who-am-i: " + jedis.aclWhoAmI());
        //log.info("Redis ping: " + jedis.ping());
        //log.info("Redis info: " + jedis.info());
        //log.info("Redis randomKey: " + jedis.randomKey());

        log.info("Redis set1: " + jedis.set("key1", "value1", SetParams.setParams().ex(10)));
        log.info("Redis get1: " + jedis.get("key1"));

        log.info("Redis set2: " + jedis.set("key2", "value2", SetParams.setParams().ex(10)));
        log.info("Redis get2: " + jedis.get("key2"));

        log.info("Redis set3: " + jedis.set("key3", "value3", SetParams.setParams().ex(10)));
        log.info("Redis get3: " + jedis.get("key3"));

        log.info("Redis KEYS: " + jedis.keys("key*"));

        CacheUtils.sleep(11000);

        log.info("Redis get2: " + jedis.get("key1"));
        jedis.close();

         */
        log.info("END-----");
    }
}
