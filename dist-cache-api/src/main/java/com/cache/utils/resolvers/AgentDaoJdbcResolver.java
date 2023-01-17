package com.cache.utils.resolvers;

import com.cache.interfaces.Agent;
import com.cache.interfaces.Resolver;

import java.util.Optional;

public class AgentDaoJdbcResolver implements Resolver {
    /** get single value for a key */
    public Optional<String> getValue(String key) {
        return Optional.empty();
    }

    /** connect */
    public void connectAgent(Agent agent) {
    }
}
