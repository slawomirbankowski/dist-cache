package com.cache.test;

import com.cache.DistFactory;
import com.cache.api.CacheObject;
import com.cache.interfaces.Cache;
import com.cache.api.CacheMode;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CacheJdbcStorageTest {
    private static final Logger log = LoggerFactory.getLogger(CacheJdbcStorageTest.class);

    @Test
    public void simpleTest() {
        log.info("START------");
        Cache cache = DistFactory.buildDefaultFactory()
                .withName("GlobalCacheTest")
                .withStorageJdbc("jdbc:postgresql://localhost:5432/cache01", "org.postgresql.Driver",
                        "cache_user", "cache_password123")
                .withSerializerDefault()
                .withSerializer("java.lang.String=StringSerializer,default=ObjectStreamSerializer")
                .withRegistrationJdbc("jdbc:postgresql://localhost:5432/cache01", "org.postgresql.Driver",
                        "cache_user", "cache_password123")
                .withRegisterApplication("")
                .withRegisterApplicationDefaultUrl()
                .withMaxObjectAndItems(30, 100)
                .createCacheInstance();
        Object[] objs = new Object[] {
                "value5",
                Map.of("aaaa", "1111", "bbbb", "2222"),
                new SimpleObject("aaaa", 1111),
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                List.of("aaaaa", "bbbbbb", "cccccc", "dddddd"),
                new ComplexObject(10)
        };

        for (int i=0; i< objs.length; i++) {
            cache.setCacheObject("key" + i, objs[i], CacheMode.modeTtlOneHour);
        }
        for (int i=0; i< objs.length; i++) {
            var co = cache.getCacheObject("key" + i);
            if (co.isPresent()) {
                CacheObject o = co.get();
                System.out.println("-----------------------------------------------------" + i);
                System.out.println("OBJECT:" + i + ", type=" + objs[i].getClass().getName() + ", VALUE=" + objs[i]);
                System.out.println("CACHE =" + i + ", type=" + o.getClassName() + ", VALUE=" + o.getValue() + ", size=" + o.getSize() + ", priority=" + o.getPriority());
            } else {
                System.out.println("OBJECT[" + i + ", key: key" + i + ", original: " + objs[i].getClass().getName() + ", NO OBJ IN CACHE[" + i + "]");
            }
        }
        System.out.println("-----------------------------------------------------");
        log.info("Cache getItemsCount: " + cache.getItemsCount());
        cache.close();
        log.info("END-----");
    }
}
class SimpleObject implements Serializable {
    private String name;
    private int number;
    public SimpleObject(String name, int number) {
        this.name = name;
        this.number = number;
    }
    public String toString() {
        return "SimpleObject:name=" + name + ",number=" + number;
    }
}
class ComplexObject implements Serializable {

    private SimpleObject obj;
    protected List<SimpleObject> objs = new LinkedList<>();
    public ComplexObject(int n) {
        obj = new SimpleObject("item", n);
        for (int i=0; i<n; i++) {
            objs.add(new SimpleObject("item" + i, i));
        }
    }
    public String toString() {
        return "ComplexObject:objs=" + objs.size();
    }
    public int size() {
        return objs.size();
    }
}