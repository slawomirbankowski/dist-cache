package com.cache.test;

import com.cache.DistFactory;
import com.cache.interfaces.Cache;
import com.cache.test.dao.DatabaseCacheDao;
import com.cache.test.dao.DatabaseDao;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheManagerFullModelTest {
    private static final Logger log = LoggerFactory.getLogger(CacheManagerFullModelTest.class);

//    @Test
    public void fullDaoModelTest() {
        log.info("START------");
        Cache cache = DistFactory.buildDefaultFactory()
                .withName("GlobalCacheTest")
                .withCacheApp("https://localhost:9999/")
                .withServers("localhost:9095")
                .withStorageHashMap()
                //.withStorageKafka("")
                .withMaxObjectAndItems(100, 20000)
                .createCacheInstance();

        log.info("Initialize DAO to keep objects");
        DatabaseDao dao = new DatabaseDao();
        log.info("Initialize items into DAO");
        dao.initializeItems( 10, 2000, 10,
                1000, 10, 10000,
                1000, 5000, 10000);
        log.info("-->users: " + dao.getUsers().initialTable.length);
        log.info("-->warehouses: " + dao.getWarehouses().initialTable.length);
        log.info("-->products: " + dao.getProducts().initialTable.length);
        log.info("-->stocks: " + dao.getWarehouseStocks().initialTable.length);
        log.info("-->orders: " + dao.getOrders().initialTable.length);
        log.info("-->orderItems: " + dao.getOrderItems().initialTable.length);
        log.info("-->preferences: " + dao.getUserPreferences().initialTable.length);

        for (int test=0; test<10; test++) {
            long startTime = System.currentTimeMillis();
            for (int i=0; i<10; i++) {
                dao.getOrders().getById(i);
                // TODO: add more tests
            }
            log.info("Test: " + test + ", time: " + (System.currentTimeMillis()-startTime));
        }

        log.info("Initialize DAO with cache");
        DatabaseCacheDao cacheDao = new DatabaseCacheDao(cache, dao);

        for (int test=0; test<10; test++) {
            long startTime = System.currentTimeMillis();
            for (int i=0; i<10; i++) {
                cacheDao.getOrders().getById(i);
                // TODO: add more tests for different objects
            }
            log.info("Test: " + test + ", time: " + (System.currentTimeMillis()-startTime));
        }

        log.info("Start testing DAO with cache");

        cache.close();
        log.info("END-----");
    }
}
