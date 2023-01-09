package com.cache.api.enums;

/** types of DAO classes */
public enum DistDaoType {
    jdbc("com.cache.dao.DaoJdbcBase"),
    kafka("com.cache.dao.DaoKafkaBase"),
    elasticsearch("com.cache.dao.DaoElasticsearchBase"),
    redis("com.cache.dao."),
    mongodb("com.cache.dao.");

    /** */
    private String className;

    /** */
    public String getClassName() {
        return className;
    }
    DistDaoType(String className) {
        this.className = className;
    }
}
