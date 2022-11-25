package com.cache.test;

import com.cache.DistCacheFactory;
import com.cache.api.CacheConfig;
import com.cache.api.CacheMode;
import com.cache.api.CacheObject;
import com.cache.api.StorageInitializeParameter;
import com.cache.storage.InternalWithTtlAndPriority;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Properties;

public class TestInternalTtlPriority {
  @Test
  public void test() {
    var props = new Properties();
    props.put(CacheConfig.CACHE_MAX_LOCAL_OBJECTS, "3");
    props.put(CacheConfig.CACHE_MAX_LOCAL_ITEMS, "5");
    var cc = new CacheConfig(props);
    var cache = DistCacheFactory.createInstance(props);
    var sip = new StorageInitializeParameter(cc, cache);
    var storage = new InternalWithTtlAndPriority(sip);
    System.out.println(storage);


    var single1 = new CacheObject("single1", "single1", 123, CacheMode.modePriority(1));
    var single2 = new CacheObject("single2", "single2", 123, CacheMode.modePriority(1));
    var single3 = new CacheObject("single3", "single3", 123, CacheMode.modePriority(1));
    var single4 = new CacheObject("single4", "single4", 123, CacheMode.modePriority(1));
    var multi1 = new CacheObject("multi1", List.of("m1", "m2", "m3"), 123, CacheMode.modePriority(2));
    var multi2 = new CacheObject("multi2", List.of("m1", "m2", "m3"), 123, CacheMode.modePriority(2));

    storage.setObject(single1);
    storage.setObject(single2);
    storage.setObject(single3);
    storage.setObject(single4);
    storage.setObject(multi1);
    storage.setObject(multi2);
  }
}
