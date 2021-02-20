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

### Using hostnames

#### Introduction
As docker will create its own set of private internet protocol (IP) address for the images that are being executed, hostnames are required to be able to connect to these images.

As such, users are required to modify their `/etc/hosts` file. (A super user account is required for this to be done)

The hostnames that are used to connect to the hadoop **namenode** and **datanode** are specified in the [docker-compose.yml](infra/docker-hive/docker-compose.yml) file and they are listed below:

| hostname  | description                                                                     |
|-----------|---------------------------------------------------------------------------------|
| namenode  | Hostname to connect to the Hadoop namenode.                                     |
| ddatanode | Hostname to connect to the Hadoop datanode. Please note that there are 2 `d`s. |


#### How to modify etc/hosts file
1. To modify the `etc/hosts` file, please execute the following commands:

```shell
sudo nano /etc/hosts
```

2. Insert the following lines at the end of the `/etc/hosts` file.

```text
172.21.148.207 namenode
172.21.148.207 ddatanode
```

Note that hostnames referenced in the `docker-compose.yml` configuration as referenced above corresponds to the hostname that are being added and mapped in the `/etc/hosts` file. 

3. Save the file.


## TODOs:
- Add deploy into make file

