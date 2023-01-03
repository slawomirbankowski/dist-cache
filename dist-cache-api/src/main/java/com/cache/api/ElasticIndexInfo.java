package com.cache.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ElasticIndexInfo {
    private String health;
    private String status;
    private String index;
    private String uuid;

    public ElasticIndexInfo() {
    }
    public ElasticIndexInfo(String health, String status, String index, String uuid) {
        this.health = health;
        this.status = status;
        this.index = index;
        this.uuid = uuid;
    }

    public String getHealth() {
        return health;
    }

    public void setHealth(String health) {
        this.health = health;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
