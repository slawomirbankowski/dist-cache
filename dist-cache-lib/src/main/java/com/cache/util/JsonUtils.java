package com.cache.util;

import com.cache.agent.AgentInstance;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** JSON utils */
public class JsonUtils {

    /** local logger for this class*/
    protected static final Logger log = LoggerFactory.getLogger(AgentInstance.class);
    /** serialize */
    public static String serialize(Object obj) {
        try {
            ObjectMapper mapper = JsonMapper.builder()
                    .findAndAddModules()
                    .build();
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException ex) {
            log.warn("CANNOT SERIALIZE OBJECT: " + obj.getClass().getName() + ", reason: " + ex.getMessage(), ex);
            return null;
        }
    }

}
