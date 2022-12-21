package com.cache.api;

import com.cache.utils.CacheUtils;

import java.time.LocalDateTime;

/** Object to encapsulate Thread managed by Dist Agent */
public class AgentThreadObject {

    /** date and time of creation */
    private final LocalDateTime createdDate = LocalDateTime.now();
    /** global unique ID */
    private final String threadGuid = CacheUtils.generateConnectorGuid(this.getClass().getSimpleName());
    /** Thread created in Dist Agent system or Dist Service */
    private Thread thread;

    public AgentThreadObject(Thread thread) {
        this.thread = thread;
    }
    /** extract information about this Thread */
    public AgentThreadInfo getInfo() {
        // LocalDateTime createdDate, String threadGuid, String threadName, String threadState, int threadPriority, long threadId
        return new AgentThreadInfo(createdDate, threadGuid, thread.getName(), thread.getState().name(), thread.getPriority(), thread.getId());
    }

}
