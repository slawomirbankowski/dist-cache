package com.cache.test;

import com.cache.api.CacheMode;
import com.cache.api.CacheObject;
import com.cache.api.CachePolicy;
import com.cache.api.CachePolicyBuilder;
import com.cache.utils.CacheStats;
import com.cache.utils.CacheUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class CachePolicyBuildTest {
    private static final Logger log = LoggerFactory.getLogger(CachePolicyBuildTest.class);

    @Test
    public void cachePolicyBuilderSimpleTest() {
        log.info("START ------ agent register test test");

        String fullPolicy = "sizeMin=10,sizeMax=30,applyPrioritySet=5 ; ttlMin=10000,ttlMax=40000,applyMode=5 ; ttlMin=1000000,applyMode=KEEP";
        CachePolicy policy = CachePolicyBuilder.empty().fromString(fullPolicy).create();

        log.info("Policy items count: " + policy.getItemsCount());
        log.info("Policy items: " + policy.getItems());
        log.info("Policy: " + policy);

        long currTime = System.currentTimeMillis();
        Object obj = "objectInCache";
        String key = "key";
        int objSize = 10;
        long acquireTimeMs = 100;
        CacheObject co = new CacheObject(0, currTime, currTime, currTime, key,
                obj, x -> obj, objSize, acquireTimeMs, 0, 0, CacheMode.Mode.TTL, 5, 1000000, Set.of());

        CacheStats stats = new CacheStats();
        stats.refreshMemory();
        stats.keyRead(key);
        stats.keyMiss(key);
        stats.keyRead(key);

        policy.checkAndApply(co, stats);

        CachePolicy policy2 = CachePolicyBuilder
                .empty()
                .next().checkSizeMin(100).applyPriority(6)
                .next().checkThread("Main").checkMode(CacheMode.Mode.KEEP).applySizeMultiply(2)
                .next().checkAcquireTime(5000, 99999).applyPriorityIncrease(3)
                .next().checkPriority(0, 3).checkTtl(100000, 999999).applyMode(CacheMode.Mode.KEEP)
                .next().checkKeyContains("UserDto").applyPriorityIncrease(3)
                .next().checkMemoryFree(3000000).applyTtlDivide(10)
                .create();

        log.info("END-----");
    }
}
