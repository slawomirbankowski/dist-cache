package com.cache.base;

import com.cache.api.*;
import com.cache.interfaces.HttpCallable;
import com.cache.utils.AdvancedMap;
import com.cache.utils.DistUtils;
import com.cache.utils.HttpConnectionHelper;
import com.cache.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/** base class for any JDBC based DAO */
public class DaoElasticsearchBase {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(DaoElasticsearchBase.class);

    private final String elasticUrl;
    private final String elasticUser;
    private final String elasticPass;

    /** HTTP client to Elasticsearch */
    private HttpCallable conn;

    private Map<String, String> defaultHeaders;

    /** creates new DAO to JDBC database */
    public DaoElasticsearchBase(String elasticUrl, String elasticUser, String elasticPass) {
        this.elasticUrl = elasticUrl;
        this.elasticUser = elasticUser;
        this.elasticPass = elasticPass;
        onInitialize();
    }

    public String getElasticUrl() {
        return elasticUrl;
    }

    public String getElasticUser() {
        return elasticUser;
    }

    public String getElasticPass() {
        return elasticPass;
    }

    public void onInitialize() {
        try {
            log.info("Connecting to Elasticsearch, URL=" + elasticUrl + ", user: " + elasticUser);
            defaultHeaders = Map.of("Content-Type", "application/json", "Authorization", DistUtils.getBasicAuthValue(elasticUser, elasticPass));
            conn = HttpConnectionHelper.createHttpClient(elasticUrl);
            List<ElasticClusterInfo> cluInfo = getClusterInfo();
            log.info("Connected to Elasticsearch, cluster nodes: " + cluInfo.size() + ", cluster info: " + cluInfo);
        } catch (Exception ex) {
            log.info("Cannot connect to Elasticsearch at URL:" + elasticUrl + ", reason: " + ex.getMessage(), ex);
        }
    }

    public List<ElasticClusterInfo> getClusterInfo() {
        return conn.callGet("/_cat/master?v=true&format=json", defaultHeaders).parseOutputTo(new TypeReference<List<ElasticClusterInfo>>() {}).orElseGet(() -> List.of());
    }
    public Optional<ElasticIndexCreateInfo> createIndex(String indexName) {
        String createBody = JsonUtils.serialize(Map.of());
        return conn.callPut("/" + indexName , defaultHeaders, createBody).parseOutputTo(new TypeReference<ElasticIndexCreateInfo>() {});
    }
    public List<ElasticIndexInfo> getIndices() {
        Optional<List<ElasticIndexInfo>> indices = conn.callGet("/_cat/indices?format=json", defaultHeaders).parseOutputTo(new TypeReference<List<ElasticIndexInfo>>() {});
        return indices.orElseGet(() -> List.of());
    }
    /** get names of all indices in the set */
    public Set<String> getIndicesNames() {
        return getIndices().stream().map(x -> x.getIndex()).collect(Collectors.toSet());
    }
    public Optional<ElasticDocumentInfo> getDocument(String indexName, String key) {
        return conn.callGet("/" + indexName + "/_doc/" + key, defaultHeaders).parseOutputTo(new TypeReference<ElasticDocumentInfo>() {});
    }
    public Optional<ElasticInsertInfo> addOrUpdateDocument(String indexName, String key, Map<String, String> doc) {
        String insertBody = JsonUtils.serialize(doc);
        return conn.callPut("/" + indexName + "/_doc/" + key, defaultHeaders, insertBody).parseOutputTo(new TypeReference<ElasticInsertInfo>() {});
    }
    public Optional<ElasticInsertInfo> deleteDocument(String indexName, String key) {
        return conn.callDelete("/" + indexName + "/_doc/" + key, defaultHeaders).parseOutputTo(new TypeReference<ElasticInsertInfo>() {});
    }
    public Optional<ElasticSearchInfo> searchSimple(String indexName, String query) {
        return conn.callGet("/" + indexName + "/_search?q=" + query, defaultHeaders).parseOutputTo(new TypeReference<ElasticSearchInfo>() {});
    }
    public Optional<ElasticSearchInfo> searchComplex(String indexName, String key, String value) {
        return searchComplex(indexName, "match", key, value);
    }
    public Optional<ElasticSearchInfo> searchComplex(String indexName, String matchType, String key, String value) {
        var searchStructure = Map.of("query", Map.of("bool", Map.of("match", Map.of(key, value))));
        String searchBody = JsonUtils.serialize(searchStructure);
        return conn.callPost("/" + indexName + "/_search", defaultHeaders, searchBody).parseOutputTo(new TypeReference<ElasticSearchInfo>() {});
    }

    /** close current Elasticsearch DAO */
    public boolean close() {
        return true;
    }


}
