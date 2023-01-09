package com.cache.base.dtos;

import com.cache.utils.AdvancedMap;

import java.util.Map;

/** row for JDBC table distagentregister
 * create table distagentserver(agentguid text, servertype text, serverhost text, serverip text, serverport int, serverurl text, createddate timestamp, isactive int, lastpingdate timestamp)
 * */
public class DistAgentServerRow {

    public String agentguid;
    public String serverguid;
    public String servertype;
    public String serverhost;
    public String serverip;
    public int serverport;
    public String serverurl;
    public java.util.Date createddate;
    public int isactive;
    public java.util.Date lastpingdate;
    public String serverparams;

    /** */
    public DistAgentServerRow() {
    }
    public DistAgentServerRow(String agentguid, String serverguid, String servertype, String serverhost, String serverip, int serverport, String serverurl, java.util.Date createddate, int isactive, java.util.Date lastpingdate, String serverparams) {
        this.agentguid = agentguid;
        this.serverguid = serverguid;
        this.servertype = servertype;
        this.serverhost = serverhost;
        this.serverip = serverip;
        this.serverport = serverport;
        this.serverurl = serverurl;
        this.createddate = createddate;
        this.isactive = isactive;
        this.lastpingdate = lastpingdate;
        this.serverparams = serverparams;
    }
    public DistAgentServerRow copyNoPassword() {
        return new DistAgentServerRow(agentguid, serverguid, servertype, serverhost, serverip, serverport, serverurl, createddate, isactive, lastpingdate, serverparams);
    }
    public Map<String, String> toMap() {
        return Map.of("type", "server",
                "agentguid", agentguid,
                "serverguid", serverguid,
                "servertype", servertype,
                "serverhost", serverhost,
                "serverip", serverip,
                "serverport", "" + serverport,
                "serverurl", serverurl,
               // "serverparams", serverparams,
                "createddate", createddate.toString(),
                "lastpingdate", lastpingdate.toString());
    }
    public static DistAgentServerRow fromMap(Map<String, Object> map) {
        AdvancedMap m = new AdvancedMap(map);
        return new DistAgentServerRow(
                m.getString("agentguid", ""),
                m.getString("serverguid", ""),
                m.getString("servertype", ""),
                m.getString("serverhost", ""),
                m.getString("serverip", ""),
                m.getInt("serverport", 8085),
                m.getString("serverurl", ""),
                m.getDateOrNow("lastpingdate"),
                m.getInt("isactive", 0),
                m.getDateOrNow("lastpingdate"),
                m.getString("serverparams", "")
        );
    }
}
