package com.cache.utils;

import com.cache.api.AgentWebApiRequest;
import com.cache.api.AgentWebApiResponse;

import java.util.HashMap;
import java.util.function.BiFunction;

/** Processor for Agent Web API to handle requests and produce responses
 * it is keeping methods
 * */
public class DistWebApiProcessor {

    /** key -> method with name like GET:info
     * value - method to process this Web Api Request */
    private final HashMap<String, BiFunction<String, AgentWebApiRequest, AgentWebApiResponse>> requestMethodHandlers = new HashMap<>();

    /** add new handler */
    public DistWebApiProcessor addHandler(String requestMethod, String serviceMethod, BiFunction<String, AgentWebApiRequest, AgentWebApiResponse> method) {
        String fullKey = requestMethod + ":" + serviceMethod;
        requestMethodHandlers.put(fullKey, method);
        return this;
    }
    public DistWebApiProcessor addHandlerGet(String serviceMethod, BiFunction<String, AgentWebApiRequest, AgentWebApiResponse> method) {
        return addHandler("GET", serviceMethod, method);
    }
    public DistWebApiProcessor addHandlerPost(String serviceMethod, BiFunction<String, AgentWebApiRequest, AgentWebApiResponse> method) {
        return addHandler("POST", serviceMethod, method);
    }
    public DistWebApiProcessor addHandlerPut(String serviceMethod, BiFunction<String, AgentWebApiRequest, AgentWebApiResponse> method) {
        return addHandler("PUT", serviceMethod, method);
    }
    public DistWebApiProcessor addHandlerDelete(String serviceMethod, BiFunction<String, AgentWebApiRequest, AgentWebApiResponse> method) {
        return addHandler("DELETE", serviceMethod, method);
    }
    /** handle request - find method and execute it to get response for Web API */
    public AgentWebApiResponse handleRequest(AgentWebApiRequest req) {
        String fullMethodName = req.getMethod() + ":" + req.getServiceMethod();
        var requestHandler = requestMethodHandlers.get(fullMethodName);
        if (requestHandler != null) {
            return requestHandler.apply(req.getServiceMethod(), req);
        }
        return req.responseNotFound();
    }

}
