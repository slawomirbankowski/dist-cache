package com.cache.utils;

import com.cache.api.AgentWebApiRequest;
import com.cache.api.AgentWebApiResponse;

import java.util.HashMap;
import java.util.function.BiFunction;

/** Processor for Agent Web API to handle requests and produce responses
 * it is keeping methods
 * */
public class DistWebApiProcessor {

    /** key -> method name
     * value - method to process this Web Api Request */
    private HashMap<String, BiFunction<String, AgentWebApiRequest, AgentWebApiResponse>> requestMethodHandlers = new HashMap<>();

    /** add new handler */
    public DistWebApiProcessor addHandler(String methodName, BiFunction<String, AgentWebApiRequest, AgentWebApiResponse> method) {
        requestMethodHandlers.put(methodName, method);
        return this;
    }
    /** handle request - find method and execute it to get response for Web API */
    public AgentWebApiResponse handleRequest(AgentWebApiRequest req) {
        var requestHandler = requestMethodHandlers.get(req.getServiceMethod());
        if (requestHandler != null) {
            return requestHandler.apply(req.getServiceMethod(), req);
        }
        return req.responseNotFound();
    }

}
