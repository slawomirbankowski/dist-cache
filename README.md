# dist-cache
Distributed cache library for JVM applications or through socket/http connectors.

Each cache is creating agent that can communicate to other configured agents to provide fast read and distributed write.

Cache can be fully configured how to keep objects: 
- time-based - it means that object is released/disposed after some time
- priority-based - it means that each object is disposed based on priority, top priority would be disposed last
- out-of-memory - large object would be disposed first, small would be disposed the last
- LRU-based - last recently used would not be disposed
- usage-factor-based - usage factor over time is calculated for each object
- keep with replace - 

Cache is distrubited and can be deployed as many instances with additional configuration.
- there is agent-based system to keep cache-instances connected


Cache can be connected to different storages to keep cache items and communicate between cache-agents:
- Redis
- Elasticseach
- Kafka
- JDBC-compliant database(s) (with DDL option)
- custom HTTP storage
