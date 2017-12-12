# Kafka KSQL Docker


## Introduction
This repository is a copy of the [KSQL quickstart](https://github.com/confluentinc/ksql/tree/0.1.x/docs/quickstart) where the [docker-compose.yml](./docker-compose.yml) was modified to add the `default` hosts. This modification was needed to be able to run it against the Docker Toolbox on Windows 7 because `default` is the name of the docker host machine and not `moby`.

The quickstart documentation home can be followed on this [page](./quickstart-home.md).

