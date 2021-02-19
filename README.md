# EtherNet
This repository includes the source code of the backend, Python SDK and docker images required to deploy the EtherNet application. 

## Overview
EtherNet (Ethereum Network) is an operational data system to facilitate the management and transformation of Ethereum blockchain data.


## Dependencies
To run the applications in Docker, the following dependencies are required:
- [docker](https://docs.docker.com/engine/install/ubuntu/)
- [docker-compose](https://linuxize.com/post/how-to-install-and-use-docker-compose-on-ubuntu-18-04/)


## Development

### Makefile dependencies
The Makefile is created to ease the steps of deployment. To use it, the following dependencies are required:
- Build-essential 
  
Execute the following command to install the required dependencies:
```shell
sudo apt install build-essential -y
```

### Makefile functions
From the Makefile, the following commands are available:

| Commands              | Description                                              |
|-----------------------|----------------------------------------------------------|
| protos                | Generate required proto dependencies for Python and Java |
| build                 | Build the protos and compile ethernet-core (backend)     |
| build-java            | Build the ethernet-core                                  |
| compile-java-python   | Compile Java protos                                      |
| compile-protos-python | Compile Python protos and reference errors               |


## Running the docker images
The Hive-Hadoop docker-compose is adapted from:
[big-data-europe/docker-hive](https://github.com/big-data-europe/docker-hive)

### Hive-Hadoop
Execute the following command To run the hive-hadoop docker-compose.

The credentials for the Hive instance are: `unused:unused`

```shell
cd ./infra/docker-hive && docker-compose up -d
```

### Neo4j
The credentials for the Neo4j instance are: `neo4j:test`
```shell
docker run \
    --name docker-neo4j \
    -p7474:7474 -p7687:7687 \
    -d \
    -v $HOME/neo4j/data:/data \
    -v $HOME/neo4j/logs:/logs \
    -v $HOME/neo4j/import:/var/lib/neo4j/import \
    -v $HOME/neo4j/plugins:/plugins \
    --env NEO4J_AUTH=neo4j/test \
    neo4j:latest
```

## Note

## TODOs:
- Add deploy into make file

