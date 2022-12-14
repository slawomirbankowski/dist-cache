package com.cache.api;

/** class representing single method in service can can be called */
public class ServiceMethod {
    private DistServiceType service;
    private String methodName;
    private Class messageObject;
    private Class responseObject;

    public ServiceMethod(DistServiceType service, String methodName, Class messageObject, Class responseObject) {
        this.service = service;
        this.methodName = methodName;
        this.messageObject = messageObject;
        this.responseObject = responseObject;
    }
    public DistServiceType getService() {
        return service;
    }
    public String getMethodName() {
        return methodName;
    }
    public Class getMessageObject() {
        return messageObject;
    }
    public Class getResponseObject() {
        return responseObject;
    }
}
