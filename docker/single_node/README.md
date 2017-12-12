# Kafka Single Node


## Introduction
This repository is a copy of the [Kafka Single node](https://github.com/confluentinc/cp-docker-images/blob/3.3.x/examples/kafka-single-node/docker-compose.yml) where the [docker-compose.yml](./docker-compose.yml) was modified to add the `default` hosts. This modification was needed to be able to run it against the Docker Toolbox on Windows 7 because `default` is the name of the docker host machine and not `moby`.



## Usage

### Services

```bash
# Start the services
docker-compose up -d # detached mode (daemon)
# See the status
docker-compose ps
# Shut them down
docker-compose down
```

Healthy Check
  * Zookeeper
```bash
docker-compose logs zookeeper | grep -i binding
```
```text
zookeeper_1  | [2016-07-25 03:26:04,018] INFO binding to port 0.0.0.0/0.0.0.0:32181 (org.apache.zookeeper.server.NIOServerCnxnFactory)
```
  * Kafka
```
docker-compose logs kafka | grep -i started
```
```text
kafka_1      | [2017-08-31 00:31:40,244] INFO [Socket Server on Broker 1], Started 1 acceptor threads (kafka.network.SocketServer)
kafka_1      | [2017-08-31 00:31:40,426] INFO [Replica state machine on controller 1]: Started replica state machine with initial state -> Map() (kafka.controller.ReplicaStateMachine)
kafka_1      | [2017-08-31 00:31:40,436] INFO [Partition state machine on Controller 1]: Started partition state machine with initial state -> Map() (kafka.controller.PartitionStateMachine)
kafka_1      | [2017-08-31 00:31:40,540] INFO [Kafka Server 1], started (kafka.server.KafkaServer)
```
### Utility

In the services container or in the host, you can use the following command line:
  * [kafka-topics](https://gerardnico.com/wiki/dit/kafka/topic#creation)
  * [kafka-console-producer](https://gerardnico.com/wiki/dit/kafka/producer)
  * [kafka-console-consumer](https://gerardnico.com/wiki/dit/kafka/consumer)

Example:
  * From a container
```bash
docker-compose exec kafka bash
kafka-topics --list --zookeeper localhost:32181
```
  * From your machine with the [DOCKER_IP](https://gerardnico.com/wiki/docker/host#ip)
```bash
kafka-topics --list --zookeeper ${DOCKER_IP}:32181
```
## Note
The [docker-compose](./docker-compose.yml) file use the network mode `host` which means that the port are available directly on the `host`. You can then access them directly from your machine via the [DOCKER_IP](https://gerardnico.com/wiki/docker/host#ip)

## Reference / Documentation

  * [Quickstart](https://docs.confluent.io/current/installation/docker/docs/quickstart.html)