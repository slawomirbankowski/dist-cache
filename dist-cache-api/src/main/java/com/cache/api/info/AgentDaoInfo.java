package com.cache.api.info;

import com.cache.api.enums.DistDaoType;

import java.time.LocalDateTime;

/** Information class about remote agent maintained by other system */
public class AgentDaoInfo {

    private LocalDateTime createDate;
    private String key;
    private DistDaoType daoType;

    public AgentDaoInfo(LocalDateTime createDate, String key, DistDaoType daoType) {
        this.createDate = createDate;
        this.key = key;
        this.daoType = daoType;
    }
    public LocalDateTime getCreateDate() {
        return createDate;
    }
    public String getKey() {
        return key;
    }
    public DistDaoType getDaoType() {
        return daoType;
    }
}
