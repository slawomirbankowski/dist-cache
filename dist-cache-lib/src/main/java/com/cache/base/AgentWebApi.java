package com.cache.base;

import com.cache.api.info.AgentApiInfo;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/** interface for WebAPI to publish Web REST API to directly connect to an Agent and manage it.
 * Web API is fast direct connection to Agent with many methods to be called, endpoints are in format:
 *  METHOD /service/method/parameters
 * Example:
 *  GET /agent/ping
 *  GET /agent/info
 *  POST /cache/initialize-single-storage/com.cache.storages.JdbcStorage
 *  DELETE /cache/objects/key_to_be_cleared
 * */
public abstract class AgentWebApi {
    /** sequence for requests - GLOBAL ONE !!!, this is counting all API requests for WebSimpleApi */
    public static final AtomicLong requestSeq = new AtomicLong();

    /** get type of this API */
    public abstract String getApiType();
    /** get information about this simple API */
    public abstract AgentApiInfo getInfo();
    /** get port of this WebAPI */
    public abstract int getPort();
    /** start this API */
    public abstract void startApi();
    /** close this API */
    public abstract void close();

}
