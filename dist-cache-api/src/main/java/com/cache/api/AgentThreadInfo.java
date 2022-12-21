package com.cache.api;

import java.time.LocalDateTime;

/** Information about Dist Agent managed Thread */
public class AgentThreadInfo {

    /** date and time of creation */
    private LocalDateTime createdDate;
    private String threadGuid;
    private String threadName;
    private String threadState;
    private int threadPriority;
    private long threadId;

    public AgentThreadInfo(LocalDateTime createdDate, String threadGuid, String threadName, String threadState, int threadPriority, long threadId) {
        this.createdDate = createdDate;
        this.threadGuid = threadGuid;
        this.threadName = threadName;
        this.threadState = threadState;
        this.threadPriority = threadPriority;
        this.threadId = threadId;
    }

    public String getCreatedDate() {
        return createdDate.toString();
    }

    public String getThreadGuid() {
        return threadGuid;
    }

    public String getThreadName() {
        return threadName;
    }

    public String getThreadState() {
        return threadState;
    }

    public int getThreadPriority() {
        return threadPriority;
    }

    public long getThreadId() {
        return threadId;
    }
}
