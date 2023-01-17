package com.cache.utils.resolvers;

import com.cache.interfaces.Agent;
import com.cache.interfaces.Resolver;

/** resolver from Map from Environment variables */
public class EnvironmentResolver extends MapResolver implements Resolver {
    public EnvironmentResolver() {
        super(System.getenv());
    }

    /** connect */
    public void connectAgent(Agent agent) {
    }
}
