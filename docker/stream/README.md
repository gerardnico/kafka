# Kafka Stream


## Introduction
This repository contains a docker compose file that was used to follow the [Stream Quickstart](http://kafka.apache.org/10/documentation/streams/quickstart)



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


### Healthy Check
  * See the status
```bash
docker-compose ps
```
  * Zookeeper log
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
  * From your machine, you can call the services if the hostname are mapped to the [DOCKER_IP](https://gerardnico.com/wiki/docker/host#ip) in your host file network.
```txt
192.168.99.100   broker
192.168.99.100   zookeeper
```
then
```bash
kafka-topics --list --zookeeper zookeeper:32181
```

## Reference / Documentation

  * (Stream Quickstart)[http://kafka.apache.org/10/documentation/streams/quickstart]