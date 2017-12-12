# Kafka Docker Machine


## List

  * [Ksql](./ksql): a docker-compose to get an environment to be able to play with KSQL
  * [Single Node](./single_node): An environment with just `zookeeper` and `kafka`.


## Images

  * List: [Doc](https://docs.confluent.io/current/installation/docker/docs/configuration.html)
  * Code: [Github](https://github.com/confluentinc/cp-docker-images/debian)

### Zookeeper

[confluentinc/cp-zookeeper/](https://hub.docker.com/r/confluentinc/cp-zookeeper/)

Env variable from [the configuration page](https://docs.confluent.io/current/installation/docker/docs/configuration.html#zookeeper):
  * `ZOOKEEPER_CLIENT_PORT`: Always required - Port
  * `ZOOKEEPER_TICK_TIME` : tick time (Example=2000)
  * `ZOOKEEPER_SYNC_LIMIT`: sync limit (Example=2)
  * `ZOOKEEPER_SERVER_ID`: unique server id in the cluster if any (Example between "1" and "255")

### Kafka

  * [confluentinc/cp-kafka](https://hub.docker.com/r/confluentinc/cp-kafka/) -- Official Confluent Docker Image for Kafka (OSS Version)
  * [confluentinc/cp-enterprise-kafka](https://hub.docker.com/r/confluentinc/cp-enterprise-kafka/) -- Kafka packaged with the Enterprise Confluent distribution. 


Env Variable. See [Doc configuration page](https://docs.confluent.io/current/installation/docker/docs/configuration.html#confluent-kafka-cp-kafka) and [Configure Script](https://github.com/confluentinc/cp-docker-images/blob/3.3.x/debian/kafka/include/etc/confluent/docker/configure)

  * `KAFKA_ZOOKEEPER_CONNECT` : Example=localhost:32181
  * `KAFKA_ADVERTISED_LISTENERS` : Example=PLAINTEXT://localhost:29092 - make Kafka accessible to the host at the port 29092. Port exposed by default inside the container is 9092.
  * `KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR` must be set to "1" in single-node cluster
  * `KAFKA_BROKER_ID` unique server id in the cluster if any (Example between "1" and "255")
  * `CONFLUENT_SUPPORT_CUSTOMER_ID` customer id only for the enterprise version

### Connect

  * [confluentinc/cp-kafka-connect](https://hub.docker.com/r/confluentinc/cp-kafka-connect/)

Healty ?
```bash
docker logs kafka-connect | grep started
```
```txt
[2016-08-25 18:25:19,665] INFO Herder started (org.apache.kafka.connect.runtime.distributed.DistributedHerder)
[2016-08-25 18:25:19,676] INFO Kafka Connect started (org.apache.kafka.connect.runtime.Connect)
```

From [doc](https://docs.confluent.io/current/installation/docker/docs/configuration.html#kafka-connect)

  * `CONNECT_BOOTSTRAP_SERVERS`=localhost:29092 
  * `CONNECT_REST_PORT`=28082 
  * `CONNECT_GROUP_ID`="quickstart" - A unique string that identifies the Connect cluster group this worker belongs to.
  * `CONNECT_REST_ADVERTISED_HOST_NAME`="localhost" - host name that can be reached by the client.


The [metadata topics](https://gerardnico.com/wiki/dit/kafka/distributed#metadata_internal_topics):
  * `CONNECT_CONFIG_STORAGE_TOPIC`="quickstart-config" -  must be the same for all workers with the same group.id
  * `CONNECT_OFFSET_STORAGE_TOPIC`="quickstart-offsets" -  must be the same for all workers with the same group.id
  * `CONNECT_STATUS_STORAGE_TOPIC`="quickstart-status"  -  must be the same for all workers with the same group.id
  * `CONNECT_CONFIG_STORAGE_REPLICATION_FACTOR`=1 if in a single cluster node mode
  * `CONNECT_OFFSET_STORAGE_REPLICATION_FACTOR`=1 if in a single cluster node mode
  * `CONNECT_STATUS_STORAGE_REPLICATION_FACTOR`=1 if in a single cluster node mode

The [Converters](https://gerardnico.com/wiki/dit/kafka/converter) controls the format of the data (key and value) that will be written to Kafka for source connectors or read from Kafka for sink connectors.
  * `CONNECT_KEY_CONVERTER`="org.apache.kafka.connect.json.JsonConverter" 
  * `CONNECT_VALUE_CONVERTER`="org.apache.kafka.connect.json.JsonConverter" 
  * `CONNECT_INTERNAL_KEY_CONVERTER`="org.apache.kafka.connect.json.JsonConverter" - internal
  * `CONNECT_INTERNAL_VALUE_CONVERTER`="org.apache.kafka.connect.json.JsonConverter"  - internal

Logger:
  * `CONNECT_LOG4J_ROOT_LOGLEVEL`=DEBUG
  * `CONNECT_LOG4J_LOGGERS`=org.reflections=ERROR 

Plugins Location:
  * `CONNECT_PLUGIN_PATH`=/usr/share/java \

[Interceptor Classes](https://docs.confluent.io/4.0.0/control-center/docs/clients.html):
  * `CONNECT_PRODUCER_INTERCEPTOR_CLASSES`=io.confluent.monitoring.clients.interceptor.MonitoringProducerInterceptor
  * `CONNECT_CONSUMER_INTERCEPTOR_CLASSES`=io.confluent.monitoring.clients.interceptor.MonitoringConsumerInterceptor

### Rest Proxy (Consume and Produce Topic)

  * [confluentinc/cp-kafka-rest](https://hub.docker.com/r/confluentinc/cp-kafka-rest/) -- Rest Proxy

Env from [doc](https://docs.confluent.io/current/installation/docker/docs/configuration.html#kafka-rest-proxy):
  * `KAFKA_REST_ZOOKEEPER_CONNECT` - Zookeeper location (Example=localhost:32181 or hostname1:port1,hostname2:port2,hostname3:port3)
  * `KAFKA_REST_LISTENERS`. Example=http://localhost:8082 - make the rest proxy accessible to the host at the port 8082.
  * `KAFKA_REST_SCHEMA_REGISTRY_URL` - Schema Registry location (Example=http://localhost:8081)
  * `KAFKA_REST_HOST_NAME` - Required. Example=localhost - The host name used to generate absolute URLs in responses. 

### Schema Registry

  * [confluentinc/cp-schema-registry](https://hub.docker.com/r/confluentinc/cp-schema-registry/)

Environment Variable: [Doc](https://docs.confluent.io/current/installation/docker/docs/configuration.html#schema-registry)
  
  * `SCHEMA_REGISTRY_KAFKASTORE_CONNECTION_URL` (localhost:32181)
  * `SCHEMA_REGISTRY_HOST_NAME` (localhost) - The host name advertised in ZooKeeper. 
  * `SCHEMA_REGISTRY_LISTENERS` (http://localhost:8081)
  * `SCHEMA_REGISTRY_DEBUG` (true/false)

```bash
docker logs schema-registry
```

### Control Center

  * [confluentinc/cp-enterprise-control-center/](https://hub.docker.com/r/confluentinc/cp-enterprise-control-center/)
  * [confluentinc/cp-control-center/](https://hub.docker.com/r/confluentinc/cp-control-center/)

See:
https://docs.confluent.io/current/installation/docker/docs/quickstart.html#confluent-control-center

### Demo

#### Streams
  
  * [confluentinc/kafka-streams-examples](https://hub.docker.com/r/confluentinc/kafka-streams-examples/)
  * [confluentinc/cp-kafka-streams-examples](https://hub.docker.com/r/confluentinc/cp-kafka-streams-examples/)
  * [confluentinc/ksql-clickstream-demo](https://hub.docker.com/r/confluentinc/ksql-clickstream-demo/)

#### Ksql

  * [confluentinc/ksql-cli](https://hub.docker.com/r/confluentinc/ksql-cli/)
  * [confluentinc/ksql-examples](https://hub.docker.com/r/confluentinc/ksql-examples/)