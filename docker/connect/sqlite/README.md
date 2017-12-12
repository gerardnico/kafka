# Kafka Connect with SQLite preconfigured


## Introduction
This repository is a copy of the [Kafka cp-all-in-one](https://github.com/confluentinc/cp-docker-images/blob/3.3.x/examples/cp-all-in-one/docker-compose.yml) where the [docker-compose.yml](./docker-compose.yml) was modified to:
  * add the version to the image (no more `latest` as image tag)
  * add the `CONNECT_PLUGIN_PATH` environment variable to make the Jdbc connector plugin available.
  * use a build based on the [cp-kafka-connect image](https://github.com/confluentinc/cp-docker-images/blob/3.3.x/debian/kafka-connect/Dockerfile)  to be able to:
     * put the sqlite and postgress jar driver file in a directory of the classpath (ie `/etc/kafka-connect/jars/`, see [line 41 of the launch script](https://github.com/confluentinc/cp-docker-images/blob/3.3.x/debian/kafka-connect-base/include/etc/confluent/docker/launch#L41))
     * create a `source-quickstart-sqlite` connector configuration file in `json` format (because the connect rest api accepts only the Json format)

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

## Note

The schema registry service is mandatory when using the JDBC connector (for serialization and schema evolution)

### Utility

In the services container or in the host, you can use the following command line:
  * [kafka-topics](https://gerardnico.com/wiki/dit/kafka/topic#creation)
  * [kafka-console-producer](https://gerardnico.com/wiki/dit/kafka/producer)
  * [kafka-console-consumer](https://gerardnico.com/wiki/dit/kafka/consumer)

Example:
  * From a container
```bash
docker-compose exec connect bash
kafka-topics --list --zookeeper zookeeper:32181
```
  * From your machine with the [DOCKER_IP](https://gerardnico.com/wiki/docker/host#ip)
```bash
kafka-topics --list --zookeeper ${DOCKER_IP}:32181
```

## Note to dev 
To start the build again:
```bash
docker-compose up -d --build
```

The entrypoint follows the following scripts in order:
  * [/etc/confluent/docker/run](https://github.com/confluentinc/cp-docker-images/blob/3.3.x/debian/kafka-connect-base/include/etc/confluent/docker/run)
  * [/etc/confluent/docker/launch](https://github.com/confluentinc/cp-docker-images/blob/3.3.x/debian/kafka-connect-base/include/etc/confluent/docker/launch)
  * `/usr/bin/connect-distributed`

