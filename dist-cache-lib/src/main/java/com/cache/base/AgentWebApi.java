package com.cache.base;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AgentWebApi {
    /** sequence for requests - GLOBAL ONE !!!, this is counting all API requests for WebSimpleApi */
    public static final AtomicLong requestSeq = new AtomicLong();

    /** get type of this API */
    public abstract String getApiType();
    /** start this API */
    public abstract void startApi();
    /** close this API */
    public abstract void close();
}
