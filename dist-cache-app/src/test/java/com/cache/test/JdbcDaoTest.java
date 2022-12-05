package com.cache.test;

import com.cache.dtos.CacheRowJdbc;
import com.cache.base.DaoBase;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JdbcDaoTest {
    private static final Logger log = LoggerFactory.getLogger(JdbcDaoTest.class);

    @Test
    public void cleanTest() {
        log.info("START ------ clean test");
        DaoBase dao = new DaoBase("jdbc:postgresql://localhost:5432/cache01",
                "org.postgresql.Driver",
                "cache_user",
                "cache_password123", 2, 5);

        // dist-cache
        // dist-agent
        // dist-config
        // dist-jdbc
        // dist-flow
        // dist-transform
        // dist-security
        // dist-ml

        //boolean isConn = dao.isConnected();
        //dao.getIdleConnections();
        //dao.getActiveConnections();
        //dao.executeAnyQuery("create table distcacheitem_tmp(cachekey varchar(4000), cachevalue text, inserteddate timestamp default (now()) )");
        //dao.executeAnyQuery("create index idx_distcacheitem_tmp_cachekey on cacheitems_tmp(cachekey)");
        //dao.executeInsert(new CacheRowJdbc("key222", "value222"), "cacheitems_tmp");

        var items = dao.executeSelectQuery("select * from cacheitems_tmp", x -> CacheRowJdbc.fromMap(x));
        dao.executeSelectQuery("", new Object[] {"", "", ""});
        for (CacheRowJdbc item: items) {
            log.debug("----> key=" + item.cachekey);
        }
        //dao.isConnected();
        //dao.executeUpdateQuery("insert into distcacheitems_tmp(cachekey,cachevalue,inserteddate) values (?,?,?)", new Object[] {"key1", "value111", new java.util.Date()});
        //dao.executeSelectQuery("select * from cacheitems");
        //dao.executeInsert()
        //log.debug("Connected:" + isConn);
        //assertTrue(, "Connected");
        dao.close();
        log.info("END-----");
    }

}
