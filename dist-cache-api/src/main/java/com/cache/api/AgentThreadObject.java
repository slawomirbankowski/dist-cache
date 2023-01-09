package com.cache.api;

import com.cache.api.info.AgentThreadInfo;
import com.cache.interfaces.AgentComponent;
import com.cache.utils.DistUtils;

import java.time.LocalDateTime;

/** Object to encapsulate Thread managed by Dist Agent */
public class AgentThreadObject {

    /** date and time of creation */
    private final LocalDateTime createdDate = LocalDateTime.now();
    /** global unique ID */
    private final String threadGuid = DistUtils.generateConnectorGuid(this.getClass().getSimpleName());
    private String threadFriendlyName;
    /** parent component of Agent system */
    private final AgentComponent parent;
    /** Thread created in Dist Agent system or Dist Service */
    private Thread thread;

    public AgentThreadObject(AgentComponent parent, Thread thread, String threadFriendlyName) {
        this.parent = parent;
        this.thread = thread;
        this.threadFriendlyName = threadFriendlyName;
    }
    /** extract information about this Thread */
    public AgentThreadInfo getInfo() {
        // LocalDateTime createdDate, String threadGuid, String threadName, String threadState, int threadPriority, long threadId
        return new AgentThreadInfo(createdDate, threadGuid, threadFriendlyName, thread.getName(), thread.getState().name(), thread.getPriority(), thread.getId());
    }

}
