package com.cache.test;

import com.cache.base.DaoElasticsearchBase;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DaoElasticsearchTest {
    private static final Logger log = LoggerFactory.getLogger(DaoElasticsearchTest.class);

    @Test
    public void elasticsearchDaoTest() {
        log.info("START ------ Elasticsearch DAO test");
        DaoElasticsearchBase dao = new DaoElasticsearchBase("https://localhost:9200", "elastic", "elastic");
        var ci = dao.getClusterInfo();
        log.info("Cluster info: " + ci);
        var indx = dao.getIndices();
        log.info("Indexes: " + indx.size());
        indx.stream().forEach(idx -> log.info("Index: " + idx.getIndex()));
        var doc1 = dao.getDocument("cache-items", "key1");
        log.info("Document1: " + doc1.isPresent());
        var doc2 = dao.getDocument("cache-items", "key2");
        log.info("Document2: " + doc2.isPresent());
        dao.addOrUpdateDocument("cache-items", "key11", Map.of("key", "key11", "value", "value11"));
        var doc11 = dao.getDocument("cache-items", "key11");
        log.info("Document11: " + doc2);
        var simpleRes = dao.searchSimple("cache-items", "cache");
        if (simpleRes.isPresent()) {
            log.info("Search simple size: " + simpleRes.get().getHits().getHits().size());
        } else {
            log.info("Documents not found in simple search");
        }
        var complexRes = dao.searchComplex("cache-items", "value", "value4");
        if (complexRes.isPresent()) {
            log.info("Search complex size: " + complexRes.get().getHits().getHits().size());
        } else {
            log.info("Documents not found in complex search");
        }
        log.info("END-----");
    }
}
