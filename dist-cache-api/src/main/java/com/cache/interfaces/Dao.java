package com.cache.interfaces;

import com.cache.api.DaoParams;
import com.cache.api.info.AgentDaoInfo;

/** basic interface for any DAO - Data Access Object.
 * */
public interface Dao {

    /** returns true if DAO is connected */
    boolean isConnected();
    /** get initialization parameters */
    DaoParams getParams();
    /** close current DAO */
    boolean close();
    /** get info about DAO */
    AgentDaoInfo getInfo();

}
