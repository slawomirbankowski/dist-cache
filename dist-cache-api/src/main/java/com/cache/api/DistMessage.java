package com.cache.api;

import com.cache.interfaces.DistService;
import com.cache.utils.CacheUtils;

import java.io.Serializable;
import java.time.LocalDateTime;

/** interface for message sent by dist service to another service between agent
 * message should be serialized and sent using known servers-clients like Socket, Datagram, Kafka, HTTP, ...
 *  */
public class DistMessage implements Serializable {

    /** unique ID of this message */
    private String messageUid = CacheUtils.generateMessageGuid();
    /** date and time of creation of this message */
    private LocalDateTime createdDate = LocalDateTime.now();
    /** type of message - this could be welcome, system, request, response, noOperation, ... */
    private DistMessageType messageType;
    /** source agent for this message - this is UID of source agent */
    private String fromAgent;
    /** source service for this message */
    private DistServiceType fromService;
    /** UID of destination agent OR any mass-pointer for agent like broadcast, random, round-robin, first, last-connected */
    private String toAgent;
    /** destination service to receive this message */
    private DistServiceType toService;
    /** method to execute at given service */
    private String requestMethod;
    /** */
    private String responseMethod;
    /** serializable object of this message */
    private Object message;
    /** status */
    private DistMessageStatus status;
    /** tags to help catalog this message, tags are comma-separated string table */
    private String tags;
    /** end of validity for this message, after that time timeout would be called from callbacks and message would be deleted */
    private LocalDateTime validTill;

    /** creates new message */
    DistMessage(String messageUid, LocalDateTime createdDate, DistMessageType messageType, String fromAgent, DistServiceType fromService, String toAgent, DistServiceType toService, String requestMethod, String responseMethod, Object message, String tags, LocalDateTime validTill, DistMessageStatus status) {
        this.messageUid = messageUid;
        this.createdDate = createdDate;
        this.messageType = messageType;
        this.fromAgent = fromAgent;
        this.fromService = fromService;
        this.toAgent = toAgent;
        this.toService = toService;
        this.requestMethod = requestMethod;
        this.responseMethod = responseMethod;
        this.message = message;
        this.status = status;
        this.tags = tags;
        this.validTill = validTill;
    }
    /** creates new message */
    private DistMessage(DistMessageType messageType, String fromAgent, DistServiceType fromService, String toAgent, DistServiceType toService, String requestMethod, String responseMethod, Object message, String tags, LocalDateTime validTill, DistMessageStatus status) {
        this.messageType = messageType;
        this.fromAgent = fromAgent;
        this.fromService = fromService;
        this.toAgent = toAgent;
        this.toService = toService;
        this.requestMethod = requestMethod;
        this.responseMethod = responseMethod;
        this.message = message;
        this.status = status;
        this.tags = tags;
        this.validTill = validTill;
    }

    /** get UID of message */
    public String getMessageUid() {
        return messageUid;
    }
    /** get created date */
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    public DistMessageType getMessageType() {
        return messageType;
    }
    public String getFromAgent() {
        return fromAgent;
    }
    public DistServiceType getFromService() {
        return fromService;
    }
    public String getToAgent() {
        return toAgent;
    }
    public String getRequestMethod() {
        return requestMethod;
    }
    /** response method - to be executed when response is back */
    public String getResponseMethod() {
        return responseMethod;
    }
    public String getMethod() {
        if (isTypeResponse())
            return responseMethod;
        return requestMethod;
    }

    /** returns true if this message has type request
     * so this is request to be sent to another agent/service/method */
    public boolean isTypeRequest() {
        return getMessageType().equals(DistMessageType.request);
    }
    /** returns true if this message has type response
     * so this is response from another agent/service/method */
    public boolean isTypeResponse() {
        return getMessageType().equals(DistMessageType.response);
    }

    /** returns true if this is broadcast message to be sent to all known agents */
    public boolean isSentToBroadcast() {
        return toAgent.equals(sendToBroadcast);
    }
    /** returns true when this message should be sent to random agent */
    public boolean isSentToRandom() {
        return toAgent.equals(sendToRandom);
    }

    /** returns true if this is message to be sent to all agents with given tags */
    public boolean isSentToTag() {
        return toAgent.equals(sendToTags);
    }
    public boolean isSentRoundRobin() {
        return toAgent.equals(sendToRoundRobin);
    }
    public boolean isSentToMultiple() {
        return toAgent.startsWith(sendToMultiple);
    }
    public boolean isSentToGroup() {
        return toAgent.startsWith(sendToGroup);
    }


    /** returns true when this is welcome message to initiate transfers, set agentGuid, validity, support items */
    public boolean isWelcome() {
        return messageType.equals(DistMessageType.welcome);
    }
    public DistServiceType getToService() {
        return toService;
    }

    public Object getMessage() {
        return message;
    }
    public String getTags() {
        return tags;
    }
    public LocalDateTime getValidTill() {
        return validTill;
    }

    /** create response message from current message by setting message type to response, object, status and switch from and to parameters
     * this is not changing: uid, createDate, tags, validTill */
    public DistMessage response(Object obj, DistMessageStatus st) {
        return new DistMessage(messageUid, createdDate, DistMessageType.response, toAgent, toService, fromAgent, fromService, requestMethod, responseMethod, obj, tags, validTill, DistMessageStatus.ok);
    }
    /** create message that has status notSupported - it means that method is not supporting given object type or this type of request */
    public DistMessage notSupported() {
        return new DistMessage(messageUid, createdDate, DistMessageType.response, toAgent, toService, fromAgent, fromService, requestMethod, responseMethod, "NOT_SUPPORTED", tags, validTill, DistMessageStatus.notSupported);
    }
    /** response message from current message - service not found */
    public DistMessage serviceNotFound() {
        return new DistMessage(messageUid, createdDate, DistMessageType.response, toAgent, toService, fromAgent, fromService, requestMethod, responseMethod, "SERVICE_NOT_FOUND", tags, validTill, DistMessageStatus.serviceNotFound);
    }
    /** response message from current message - exception while executing this message */
    public DistMessage exception(Exception ex) {
        return new DistMessage(messageUid, createdDate, DistMessageType.response, toAgent, toService, fromAgent, fromService, requestMethod, responseMethod, "EXCEPTION", "", validTill, DistMessageStatus.exception);
    }
    /** response - pong from ping */
    public DistMessage pong(String agentGuid) {
        return new DistMessage(messageUid, createdDate, DistMessageType.response, agentGuid, toService, fromAgent, fromService, requestMethod, responseMethod, "PONG", "", validTill, DistMessageStatus.ok);
    }

    /** String from this method */
    public String toString() {
        return "FROM: " + fromAgent + "/" + fromService.name() + "/" + responseMethod + ", TO: " + toAgent + "/" + toService.name() + "/" + requestMethod + ", type: " + messageType.name() + ", status: " + status.name() + ", UID: " + messageUid;
    }

    /** returns message with callbacks */
    public DistMessageFull withCallbacks(DistCallbacks callbacks) {
        return new DistMessageFull(this, callbacks);
    }
    /** returns message without callbacks */
    public DistMessageFull withNoCallbacks() {
        return new DistMessageFull(this, DistCallbacks.defaultCallbacks);
    }


    public static LocalDateTime getValidTillDefault() {
        return LocalDateTime.now().plusHours(24);
    }

    /** creates new message */
    public static DistMessage createMessage(DistMessageType messageType, String fromAgent, DistServiceType fromService, String toAgent, DistServiceType toService, String method, Object message,  String tags, LocalDateTime validTill) {
        return new DistMessage(messageType, fromAgent, fromService, toAgent, toService,  method, "", message, tags, validTill, DistMessageStatus.init);
    }

    /** creates new message */
    public static DistMessage createMessage(DistMessageType messageType, String fromAgent, DistServiceType fromService, String toAgent, DistServiceType toService, String method, String responseMethod, Object message, String tags, LocalDateTime validTill) {
        return new DistMessage(messageType, fromAgent, fromService, toAgent, toService,  method, responseMethod, message, tags, validTill, DistMessageStatus.init);
    }
    /** creates new message */
    public static DistMessage createMessage(DistMessageType messageType, String fromAgent, DistServiceType fromService, String toAgent, DistServiceType toService, String method, Object message, String tags) {
        return new DistMessage(messageType, fromAgent, fromService, toAgent, toService,  method, "", message, tags, getValidTillDefault(), DistMessageStatus.init);
    }
    /** */
    public static DistMessage createMessage(DistMessageType messageType, String fromAgent, DistServiceType fromService, String toAgent, DistServiceType toService, String method, Object message) {
        return new DistMessage(messageType, fromAgent, fromService, toAgent, toService,  method, "", message, "", getValidTillDefault(), DistMessageStatus.init);
    }
    /** create message */
    public static DistMessage createMessage(DistMessageType messageType, String fromAgent, DistServiceType fromService, String toAgent, DistServiceType toService, String method, String responseMethod, Object message) {
        return new DistMessage(messageType, fromAgent, fromService, toAgent, toService,  method, responseMethod, message, "", getValidTillDefault(), DistMessageStatus.init);
    }
    /** create new request message */
    public static DistMessage createMessage(DistService fromService, String toAgent, DistServiceType toService, String method, Object message) {
        return new DistMessage(DistMessageType.request, fromService.getAgent().getAgentGuid(), fromService.getServiceType(), toAgent, toService, method, "", message, "",  getValidTillDefault(), DistMessageStatus.init);
    }
    /** ping */
    public static DistMessage ping(DistService fromService, String toAgent) {
        return new DistMessage(DistMessageType.request, fromService.getAgent().getAgentGuid(), fromService.getServiceType(), toAgent, DistServiceType.agent, "ping", "pong", "ping", "", getValidTillDefault(), DistMessageStatus.init);
    }

    public static String sendToBroadcast = "*";
    public static String sendToRandom = "?";
    public static String sendToTags = "#";
    public static String sendToRoundRobin = "O";
    public static String sendToGroup = "@";
    public static String sendToMultiple = "+";
    /** send to self */
    public static String sendToSelf = "&";
    /** send to first connected client */
    public static String sendToFirst = "[";
    /** send to last connected client */
    public static String sendToLast = "]";

}
