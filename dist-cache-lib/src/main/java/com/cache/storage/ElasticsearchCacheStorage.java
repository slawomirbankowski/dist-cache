package com.cache.storage;

import com.cache.api.*;
import com.cache.base.CacheStorageBase;
import com.cache.util.JsonUtils;
import com.cache.utils.DistUtils;
import com.cache.utils.HttpConnectionHelper;

import java.util.*;

/** cache with Elasticsearch index - need to connect to Elasticsearch,
 * create index and read/write items from/to cache
 *
 * TODO: Implement storage saving cache objects in Elasticsearch
 * */
public class ElasticsearchCacheStorage extends CacheStorageBase {

    /** URL to connect to Elasticsearch */
    private String elasticsearchUrl;
    private String elasticUser;
    private String elasticPass;
    private String elasticIndex;

    /** HTTP client to Elasticsearch */
    private HttpConnectionHelper conn;
    /** default header for Elasticsearch requests */
    private Map<String, String> header;

    /** TODO: init Elasticsearch storage */
    public ElasticsearchCacheStorage(StorageInitializeParameter p) {
        super(p);
        elasticsearchUrl = p.cache.getConfig().getProperty(DistConfig.CACHE_STORAGE_ELASTICSEARCH_URL, "http://localhost:9200");
        elasticUser = p.cache.getConfig().getProperty(DistConfig.CACHE_STORAGE_ELASTICSEARCH_USER, "");
        elasticPass = p.cache.getConfig().getProperty(DistConfig.CACHE_STORAGE_ELASTICSEARCH_PASS, "");
        elasticIndex = "cache-items"; // TODO: change this default index name by parameters
        if (!elasticUser.isEmpty()) {
            header = Map.of("Content-Type", "application/json", "Authorization", DistUtils.getBasicAuthValue(elasticUser, elasticPass));
        } else {
            header = Map.of("Content-Type", "application/json");
        }
        log.debug("Connection to Elasticsearch storage URL: " + elasticsearchUrl + ", index with cache objects: " + elasticIndex);
        conn = new HttpConnectionHelper(elasticsearchUrl);
    }
    /** Elasticsearch is external storage */
    public  boolean isInternal() { return false; }
    /** get type of this storage */
    public CacheStorageType getStorageType() {
        return CacheStorageType.elasticsearch;
    }
    /** check if object has given key, optional with specific type */
    public boolean contains(String key) {
        // TODO: search for the key


        return false;
    }
    /** TODO: get item from Elasticsearch */
    public Optional<CacheObject> getObject(String key) {
        var res = conn.callHttpGet("/" + elasticIndex + "/_doc/" + encodeKey(key), header);
        if (res.isOk()) {

            return Optional.empty();
        } else {
            return Optional.empty();
        }
    }
    public Optional<CacheObject> setObject(CacheObject o) {
        var prevObj = getObject(encodeKey(o.getKey()));
        conn.callHttpPut("/" + elasticIndex+ "/_doc/" + encodeKey(o.getKey()), header, JsonUtils.serialize(o.serializedFullCacheObject(distSerializer)));
        //CacheUtils.baseToString()
        return prevObj;
    }
    /** remove objects in cache storage by keys */
    public void removeObjectsByKeys(Collection<String> keys) {

    }
    /** remove object in cache storage by key */
    public void removeObjectByKey(String key) {
        conn.callHttpDelete("/" + elasticIndex + "/_doc/" + encodeKey(key), header);
    }
    /** get number of items in cache */
    public int getItemsCount() {

        return 0;
    }
    /** get number of objects in this cache */
    public int getObjectsCount() { return 0; }

    /** get keys for all cache items */
    public Set<String> getKeys(String containsStr) {
        return new HashSet<String>();
    }
    /** get info values */
    public List<CacheObjectInfo> getInfos(String containsStr) {

        return new LinkedList<CacheObjectInfo>();
    }
    /** get values of cache objects that contains given String in key */
    public List<CacheObject> getValues(String containsStr) {
        return new LinkedList<CacheObject>();
    }
    /** clear caches with given clear cache */
    public int clearCacheForGroup(String groupName) {
        // TODO: implements
        return 1;
    }
    /** clear cache by given mode
     * returns estimated of elements cleared */
    public int clearCache(CacheClearMode clearMode) {
        return -1;
    }
    /** clear cache contains given partial key */
    public int clearCacheContains(String str) {
        return 1;
    }
    /** check cache every X seconds to clear TTL caches */
    public void onTimeClean(long checkSeq) {

    }
}

class ElasticsearchIndex {

}