package com.cache.report;

import com.cache.api.*;
import com.cache.base.ServiceBase;
import com.cache.interfaces.Agent;
import com.cache.interfaces.Reports;
import com.cache.utils.DistUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Service to keep  execute, delete, schedule, edit reports.
 * Each report is connected to Storage, has query to be executed, list of columns, filters, formatters, readers.
 * Report can be executed with parameters for filters, column subset, maximum rows, maximum execution/delivery time,
 * selected format, */
public class ReportsImpl extends ServiceBase implements Reports {

    /** local logger for this clas s*/
    protected static final Logger log = LoggerFactory.getLogger(ReportsImpl.class);


    public ReportsImpl(Agent agent) {
        super(agent);
    }

    /** get type of service: cache, measure, report, flow, space, ... */
    public DistServiceType getServiceType() {
        return DistServiceType.report;
    }
    /** create new service UID for this service */
    protected String createServiceUid() {
        return DistUtils.generateCacheGuid();
    }

    /** process message, returns message with status */
    public DistMessage processMessage(DistMessage msg) {
        // TODO: implement processing message
        return msg;
    }
    /** handle API request in this Web API for this service */
    public AgentWebApiResponse handleRequest(AgentWebApiRequest request) {
        // TODO: implement handling Web Api request by reports
        return request.responseNotImplemented();
    }
    /** close and deinitialize service */
    public void onClose() {
        // TODO: close this reports service
    }

}
