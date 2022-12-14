package com.cache.api;

import com.cache.utils.CacheUtils;

import java.time.LocalDateTime;

public class AgentThreadObject {

    /** date and time of creation */
    private final LocalDateTime createdDate = LocalDateTime.now();
    /** global unique ID */
    private final String threadGuid = CacheUtils.generateConnectorGuid(this.getClass().getSimpleName());

    /** */
    private Thread thread;

    public AgentThreadObject(Thread thread) {
        this.thread = thread;

    }

}
