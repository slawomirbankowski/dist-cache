package com.cache.utils;

import com.cache.api.DistMessageStatus;
import com.cache.interfaces.DistMessage;

import java.util.HashMap;
import java.util.function.BiFunction;

/** processor is helper class for message processing with selection of method to be executed
 * this could be used in service */
public class DistMessageProcessor {

    /** key -> method name
     *
     * value - method to process this message */
    private HashMap<String, BiFunction<String, DistMessage, DistMessageStatus>> methodProcessors = new HashMap<>();
    /** default processor for messages  */
    private BiFunction<String, DistMessage, DistMessageStatus> defaultProcessor = this::defaultMethodReturnsNotSupported;

    /** */
    public DistMessageProcessor() {
    }
    public DistMessageStatus defaultMethodReturnsNotSupported(String methodName, DistMessage msg) {
        //
        return new DistMessageStatus();
    }
    /** add method to processor */
    public DistMessageProcessor addMethod(String methodName, BiFunction<String, DistMessage, DistMessageStatus> method) {
        methodProcessors.put(methodName, method);
        return this;
    }
    /** */
    public DistMessageProcessor setDefaultMethod(BiFunction<String, DistMessage, DistMessageStatus> method) {
        defaultProcessor = method;
        return this;
    }
    /** process message - find method and execute it */
    public DistMessageStatus process(String methodName, DistMessage msg) {
        BiFunction<String, DistMessage, DistMessageStatus> method = methodProcessors.get(methodName);
        try {
            if (method != null) {
                return method.apply(methodName, msg);
            } else {
                return defaultProcessor.apply(methodName, msg);
            }
        } catch (Exception ex) {
            // TODO: status is Exception

            return new DistMessageStatus();
        }
    }

}
