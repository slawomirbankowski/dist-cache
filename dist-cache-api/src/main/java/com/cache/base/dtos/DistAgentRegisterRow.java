package com.cache.base.dtos;

import com.cache.api.AgentSimplified;
import com.cache.utils.AdvancedMap;
import com.cache.utils.CacheUtils;

import java.util.Map;

/** row for JDBC table distagentregister */
public class DistAgentRegisterRow {

    public String agentguid;
    public String hostname;
    public String hostip;
    public int portnumber;
    public java.util.Date lastpingdate;
    public int isactive;

    public DistAgentRegisterRow() {
    }
    public DistAgentRegisterRow(String agentguid, String hostname, String hostip, int portnumber, java.util.Date lastpingdate, int isactive) {
        this.agentguid = agentguid;
        this.hostname = hostname;
        this.hostip = hostip;
        this.portnumber = portnumber;
        this.lastpingdate = lastpingdate;
        this.isactive = isactive;
    }
    /** */
    public AgentSimplified toSimplified() {
        return new AgentSimplified(agentguid, hostname, hostip, portnumber, CacheUtils.dateToLocalDateTime(lastpingdate));
    }
    public static DistAgentRegisterRow fromMap(Map<String, Object> map) {
        AdvancedMap m = new AdvancedMap(map);
        return new DistAgentRegisterRow(
                m.getString("agentguid", ""),
                m.getString("hostname", ""),
                m.getString("hostip", ""),
                m.getInt("portnumber", 8085),
                m.getDateOrNow("lastpingdate"),
                m.getInt("isactive", 0)
        );
    }
}
