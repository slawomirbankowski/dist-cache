package com.cache.utils;

import com.cache.api.DistMessage;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.function.BiFunction;

/** processor is helper class for message processing with selection of method to be executed
 * this could be used in service */
public class DistMessageProcessor {

    /** key -> method name
     * value - method to process this message */
    private HashMap<String, BiFunction<String, DistMessage, DistMessage>> methodProcessors = new HashMap<>();
    /** default processor for messages  */
    private BiFunction<String, DistMessage, DistMessage> defaultProcessor = this::defaultMethodReturnsNotSupported;

    /** creates new empty processor for incomming messages */
    public DistMessageProcessor() {
    }
    public DistMessage defaultMethodReturnsNotSupported(String methodName, DistMessage msg) {
        return msg.notSupported();
    }
    /** add many methods */
    public DistMessageProcessor addMethods(Object service) {
        for (Method method : service.getClass().getMethods()) {
            // TODO: check if method is taking String and DistMessage and returns DistMessageStatus - then add to methods

        }
        return this;
    }
    /** add method to processor for given method, each service might have many methods to be called */
    public DistMessageProcessor addMethod(String methodName, BiFunction<String, DistMessage, DistMessage> method) {
        methodProcessors.put(methodName, method);
        return this;
    }
    /** */
    public DistMessageProcessor setDefaultMethod(BiFunction<String, DistMessage, DistMessage> method) {
        defaultProcessor = method;
        return this;
    }
    /** process message - find method and execute it */
    public DistMessage process(String methodName, DistMessage msg) {
        BiFunction<String, DistMessage, DistMessage> method = methodProcessors.get(methodName);
        try {
            if (method != null) {
                return method.apply(methodName, msg);
            } else {
                return defaultProcessor.apply(methodName, msg);
            }
        } catch (Exception ex) {
            // TODO: status is Exception

            return msg.exception(ex);
        }
    }

}
