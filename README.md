# dist-cache
Distributed cache library for JVM applications or through socket/http connectors.

Each cache is creating agent that can communicate to other configured agents to provide fast read and distributed write.

Cache can be fully configured how to keep objects: 
- time-based - it means that object is released/disposed after some time
- priority-based - it means that each object is disposed based on priority, top priority would be disposed last
- out-of-memory - large object would be disposed first, small would be disposed the last
- LRU-based - last recently used would not be disposed
- usage-factor-based - usage factor over time is calculated for each object
- keep with replace - replace will be only if new object is in cache, always there must be at least one object

Cache is distributed and can be deployed as many instances with additional configuration.
- there is agent-based system to keep cache-instances connected
- there are callback to be set when something important is happening
- last usage date is available
- get cache size per storage
- refresh all caches with refresh mode

Cache can be connected to different storages to keep cache items and communicate between cache-agents:
- Redis
- Elasticseach
- Kafka
- JDBC-compliant database(s) (with DDL option)
- custom HTTP storage

Cache could be used as:
- library to be linked and used inside application
- standalone application having public API to be used and REST API
- code to be included "as it is"

To be added:
- Kafka to exchange messages
- Registration with Elasticsearch
- Registration with Kafka
- Remove methods with registered objects
- Cache Mongodb storage
- hit ratio of cache usage - advanced possibilities to check what is percentage of hit/miss for cache object usage
- cache blacklist - initial list of keys that should NOT be stored in cache
- Integrate Swagger or any other REST Doc API into dist-cache-app
- Security to Spring application for REST endpoints
- Application version - currently there is no versioning of this app, there should be semantic versioning
- Gradle task to build docker image after build of app jar file
- cache object group and mode, acquire time, refresh time, methods to get next value for model

* How to use Agent Distribute System with services like Cache, Reports, Storages, Remote

To create new Agent:
* Agent agent = DistFactory.buildEmptyFactory()
-    .withName("GlobalAgent") // give it any name; OPTIONAL - without this it should be OK
-    .withRegistrationXXX(...) // set-up global registration for agents as central point to exchange list of available agents; MANDATORY - Agents needs at least one common registration service
-    .withTimerYYY(...) // set up some timers to perform system tasks, checks, clean-ups; OPTIONAL - by default it is 1 minute which should be OK for most of cases
-    .withServerZZZ(...) // set up servers; MANDATORY - there should be at least Server/Client to exchange information between Agents
-    .withWebApi(...) // set-up Web API as REST-full endpoints for this Agent; OPTIONAL - 
-    .withTags(...) // give some tags to easier group Agents; OPTIONAL - for small agent systems tags are not needed
-    .withResolver(...) // add resolvers for configuration options from Command-Line, Environment variables, Vault, Resource file, ...; OPTIONAL - by default it is ENV variables 
-    .withCacheStorageVVV(...) // add storage to cache; MANDATORY for cache usage, to use caching it must be at least one cache storage defined
-    .withCachePolicy(...) // create policy for cache objects; OPTIONAL for cache usage
-    .withMaxIssues(...) // set maximum number of issues kept in Agent; OPTIONAL - by default it is 1000
-    .withMaxEvents(...) // set maximum number of events kept in Agent system; OPTIONAL - by default it is 1000
-    .withCallback(...) // add callbacks in case of events of given types; OPTIONAL - callbacks helps to react on different events, exceptions, problems with connections, storage or memory issues
-    .createAgentInstance(); // finally create Agent with all these settings and parameters

Configuration of Agent could be loaded from different sources:
Agent agent = DistFactory.buildEmptyFactory()
-    .withEnvironmentVariables() // load properties from environment variables
-    .withCommandLineArguments(String[] args) // load from command line arguments in format of: --name1 value1 --name2 value2 --name3 value3
-    .withMap(Map<String, String> initialFactoryProperties) // load from Map
-    .withJson(String jsonDefinition) // load from Map in JSON given by String
-    .withPropertiesFile(String propertiesFile) // load from properties like with name given
-    .withPropertiesUrl(String urlWithProperties) // load from properties file on URL connection

Configuration from Agent could be saved:
-    agent.getConfig().saveToFile(String fileName)
-    agent.getConfig().saveToJson()
-    agent.getConfig().saveToMap()

All configuration values are resolved using Resolvers, so value could be like:
- CONFIG_VALUE = ${SOME_KEY}_${OTHER_KEY}:${ONE_MORE_KEY}
If values for these keys with Resolvers would be:
- SOME_KEY = aaa
- OTHER_KEY = bbb
- ONE_MORE_KEY = ccc
Final value would be:
- CONFIG_VALUE = aaa_bbb:ccc

From Agent - it is possible to get different services:
-   Cache cache = agent.getCache();
-   Reports reports = agent.getReports();
-   Storages storages = agent.getStorages();

These Agents could be created in many places, many applications and have different services turned on.
- At first - Agent is connecting to Registration Services (JDBC, Elasticsearch, Kafka, ...) and register itself, servers and services.
- Then, Agent is opening Servers for communication with other clients.
- Next, Agent is opening Web API for custom connections.
- The last - Agent is checking other Agent connected to Registration Services and 

To close Agent, unregister and free all resources, just call:
- agent.close();

Each Agent has:
- Registrations - agent1.getAgentRegistrations() - global repository of agents, servers, clients, issues, events, configurations
- Connectors - agent.getAgentConnectors() - clients and servers to connect to other agents
- Services - agent.getAgentServices() - all services like Cache, Reports, Storages, Spaces, ...
- Serializer - agent.getSerializer() - serializer to serialize and deserialize messages and other objects in Agent
- Configuration - agent1.getConfig() - properties to create Agent and services
- Issues - agent.getAgentIssues() - issues like errors and exceptions from Agent and dependent services
- Events - agent.getAgentEvents() - events 
- Threads - agent.getAgentThreads() - 
- Timers - agent1.getAgentTimers() - 
- Tags - agent1.getAgentTags() - set of custom String values to classify agent
