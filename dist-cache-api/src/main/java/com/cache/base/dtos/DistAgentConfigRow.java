package com.cache.base.dtos;

import com.cache.utils.AdvancedMap;

import java.util.Map;

/** row for distagentconfig table
 * create table distagentconfig(agentguid varchar(300), configname varchar(300), configvalue varchar(300), createddate timestamp, lastupdateddate timestamp)
 *
 * */
public class DistAgentConfigRow {

    public final String agentguid;
    public final String configname;
    public String configvalue;
    public java.util.Date createddate;
    public java.util.Date lastupdateddate;

    public DistAgentConfigRow(String agentguid, String configname, String configvalue, java.util.Date createddate, java.util.Date lastupdateddate) {
        this.agentguid = agentguid;
        this.configname = configname;
        this.configvalue = configvalue;
        this.createddate = createddate;
        this.lastupdateddate = lastupdateddate;
    }

    public static DistAgentConfigRow fromMap(Map<String, Object> map) {
        AdvancedMap m = new AdvancedMap(map);
        return new DistAgentConfigRow(
                m.getStringOrEmpty("agentguid").toString(),
                m.getStringOrEmpty("configname").toString(),
                m.getStringOrEmpty("configvalue").toString(),
                m.getDateOrNow("createddate"),
                m.getDateOrNow("lastupdateddate")
        );
    }
}
