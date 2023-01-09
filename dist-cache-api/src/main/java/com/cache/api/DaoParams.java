package com.cache.api;

import com.cache.api.enums.DistDaoType;
import com.cache.utils.AdvancedMap;
import com.cache.utils.DistUtils;

import java.util.Map;

/** parameters for any DAO */
public class DaoParams {

    /** type of DAO */
    private final DistDaoType daoType;
    /** unique KEY of this DAO */
    private final String key;
    /** full map of parameters */
    private final AdvancedMap params;

    /** creates new parameters for DAO type, key to identify and parameters */
    private DaoParams(DistDaoType daoType, String key, Map<String, Object> params) {
        this.daoType = daoType;
        this.key = key;
        this.params = new AdvancedMap(params);
    }
    /** get unique key of this DAO */
    public String getKey() {
        return key;
    }
    /** get type of DAO for this parameters */
    public DistDaoType getDaoType() {
        return daoType;
    }

    public String getUrl() {
        return params.getString(URL, "");
    }
    public String getUser() {
        return params.getString(USER, "");
    }
    public String getPass() {
        return params.getString(PASS, "");
    }
    public String getDriver() {
        return params.getString(DRIVER, "");
    }
    public int getInitConnections() {
        return params.getInt(INIT_CONN, 1);
    }
    public int getMaxActiveConnections() {
        return params.getInt(MAX_CONN, 5);
    }
    public String getBrokers() {
        return params.getString(BROKERS, "");
    }
    public int getNumPartitions() {
        return params.getInt(NUM_PARTITIONS, 1);
    }
    public short getReplicationFactor() {
        return (short)params.getInt(REPL_FACTOR, 1);
    }
    public String getClientId() {
        return params.getString(CLIENT_ID, "");
    }
    public String getGroupId() {
        return params.getString(GROUP_ID, "");
    }
    /** create DAO parameters for Kafka */
    public static DaoParams kafkaParams(String brokers, int numPartitions, short replicationFactor, String clientId, String groupId) {
        String key = DistDaoType.kafka.name() + ":" + DistUtils.fingerprint( brokers + ":" + clientId);
        return new DaoParams(DistDaoType.kafka, key, Map.of(
                BROKERS, brokers,
                NUM_PARTITIONS, numPartitions,
                REPL_FACTOR, replicationFactor,
                CLIENT_ID, clientId,
                GROUP_ID, groupId
        ));
    }

    /** create DAO parameters for JDBC connections */
    public static DaoParams jdbcParams(String jdbcUrl, String jdbcDriver, String jdbcUser, String jdbcPass, int initConnections, int maxActiveConnections) {
        String key = DistDaoType.jdbc.name() + ":" + DistUtils.fingerprint( jdbcUrl + ":" + jdbcUser);
        return new DaoParams(DistDaoType.jdbc, key, Map.of(
                URL, jdbcUrl,
                DRIVER, jdbcDriver,
                USER, jdbcUser,
                PASS, jdbcPass,
                INIT_CONN, initConnections,
                MAX_CONN, maxActiveConnections
        ));
    }
    /** create DAO parameters for JDBC connections */
    public static DaoParams jdbcParams(String jdbcUrl, String jdbcDriver, String jdbcUser, String jdbcPass) {
        return jdbcParams(jdbcUrl, jdbcDriver, jdbcUser, jdbcPass, 1, 5);
    }
    /** create DAO parameters for Elasticsearch */
    public static DaoParams elasticsearchParams(String elasticUrl, String elasticUser, String elasticPass) {
        String key = DistDaoType.jdbc.name() + "" + DistUtils.fingerprint( elasticUrl + ":" + elasticUser);
        return new DaoParams(DistDaoType.elasticsearch, key, Map.of(
                URL, elasticUrl,
                USER, elasticUser,
                PASS, elasticPass
        ));
    }
    /** create DAO parameters for Elasticsearch */
    public static DaoParams elasticsearchParams(String elasticUrl) {
        return new DaoParams(DistDaoType.elasticsearch, elasticUrl, Map.of(
                URL, elasticUrl
        ));
    }

    /** create DAO parameters for Redis */
    public static DaoParams redisParams(String host, String port) {
        String key = DistDaoType.redis.name() + "" + DistUtils.fingerprint( host + ":" + port);
        return new DaoParams(DistDaoType.redis, key, Map.of(
                HOST, host,
                PORT, port
        ));
    }

    public static final String URL = "URL";
    public static final String USER = "USER";
    public static final String PASS = "PASS";
    public static final String INIT_CONN = "INIT_CONN";
    public static final String MAX_CONN = "MAX_CONN";
    public static final String DRIVER = "DRIVER";
    public static final String BROKERS = "BROKERS";
    public static final String NUM_PARTITIONS = "NUM_PARTITIONS";
    public static final String REPL_FACTOR = "REPL_FACTOR";
    public static final String CLIENT_ID = "CLIENT_ID";
    public static final String GROUP_ID = "GROUP_ID";
    public static final String HOST = "HOST";
    public static final String PORT = "PORT";

}
