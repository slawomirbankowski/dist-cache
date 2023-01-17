package com.cache.api.enums;

/** all services that can be connected to Dist System, each service can have some features and methods to be used
 * There might be different distributed services like:
 * agent - communication between distributed processes
 * cache - keep objects in memory or other fast key-read storage to have faster access to these objects
 * space - shared object in global space of distributed agents
 * measure - global counters
 * flow - distributed flow
 * report - distributed shared reports to be executed
 * config - distributed configuration with access to many storages and versioned values over time
 * schedule - distributed orchestration like CRON in the Cloud
 * custom - any custom, unknown service, external from DistSystem point of view
 * */
public enum DistServiceType {
    agent, // agent service to bind all other services, providing communication, thread management, issues and events management
    receiver,
    cache, // distributed cache
    measure, // measures in distributed environment
    space, // shared spaces with objects with owners but allowed to be modified by anyone
    flow, //
    report, // executing reports based on storages
    storage, // storages that can be defined anywhere and are available everywhere
    config,
    schedule,
    security, // distributed security: authentication AND authorization
    remote, // remote execution of methods on registred objects
    custom
}
