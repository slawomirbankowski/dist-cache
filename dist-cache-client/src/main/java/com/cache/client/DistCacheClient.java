package com.cache.client;

import com.cache.utils.HttpConnectionHelper;

/** client to connect to standalone Cache Application and provide cache through REST endpoints OR direct Socket connections */
public class DistCacheClient {

    private String baseUrl;
    private HttpConnectionHelper connectionHelper;

    public DistCacheClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.connectionHelper = new HttpConnectionHelper(baseUrl);
    }

    /** check connection */
    public boolean isConnected() {
        return "pong".equals(ping());
    }
    /** ping application, should returns: pong */
    public String ping() {
        return connectionHelper.callHttpGet("/api/v1/ping").getOutObject().toString();
    }

}
