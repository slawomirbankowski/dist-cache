package com.cache.api;

import java.util.List;
import java.util.Map;

/** Response for Agent Web API */
public class AgentWebApiResponse {
    private final int code;
    private final Map<String, List<String>> headers;
    private final String content;

    public AgentWebApiResponse(int code, Map<String, List<String>> headers, String content) {
        this.code = code;
        this.headers = headers;
        this.content = content;
    }

    /** */
    public String getContent() {
        return content;
    }
    /** */
    public byte[] getResponseContent() {
        return content.getBytes();
    }
    public int getResponseCode() {
        return code;
    }
    public Map<String, List<String>> getHeaders() {
        return headers;
    }
}
