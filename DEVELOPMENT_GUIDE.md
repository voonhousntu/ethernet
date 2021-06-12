# Development Guide 

## Makefile dependencies
The Makefile is created to ease the steps of deployment. To use it, the following dependencies are required:
- build-essential

Execute the following command to install the required dependencies:
```shell
sudo apt install build-essential -y
```

### Makefile functions
From the Makefile, the following commands are available:

| Commands                  | Description                                                                             |
|---------------------------|-----------------------------------------------------------------------------------------|
| protos                    | Generate required proto dependencies for Python and Java                                |
| build                     | Build the protos and compile Ethernet-Core (backend)                                    |
| build-java                | Build the ethernet-core                                                                 |
| compile-java-python       | Compile Java protos                                                                     |
| compile-protos-python     | Compile Python protos and reference errors                                              |
| create-ethernet-asset-dir | Initialise the required EtherNet asset and working directories                          |
| create-schema-n-tables    | Create the required Hive schema and tables                                              |
| deploy-docker-deps        | Deploy all docker images that are required for the EtherNet application to run properly |
| deploy-hive-hadoop        | Deploy the Hive and Hadoop docker images                                                |
| deploy-neo4j              | Deploy the latest Neo4j docker image                                                    |
| init-hive                 | Initialise Hive with the required schema and tables                                     |
| start-rpyc-server         | Start the RPyC server responsible for handling ethernet-core to Neo4j commands          |


## Running the Docker Images
The Hive-Hadoop docker-compose is adapted from:
[big-data-europe/docker-hive](https://github.com/big-data-europe/docker-hive)

### Hive-Hadoop
Execute the following command To run the hive-hadoop docker-compose.

The credentials for the Hive instance are: `unused:unused`

```shell
make deploy-hive-hadoop
```

### Neo4j
The credentials for the Neo4j instance are: `neo4j:test`
```shell
make deploy-neo4j
```

## Using Hostnames

As docker will create its own set of private internet protocol (IP) address for the images that are being executed, hostnames are required to be able to connect to these images.

As such, users are required to modify their `/etc/hosts` file. (A super user account is required for this to be done)

The hostnames that are used to connect to the hadoop **namenode** and **datanode** are specified in the [docker-compose.yml](infra/docker-hive/docker-compose.yml) file and they are listed below:

| hostname  | description                                                                    |
|-----------|--------------------------------------------------------------------------------|
| namenode  | Hostname to connect to the Hadoop namenode                                     |
| ddatanode | Hostname to connect to the Hadoop datanode (Please note that there are 2 `d`s) |


### How to Modify etc/hosts
1. To modify the `etc/hosts` file, please execute the following commands:

```shell
sudo nano /etc/hosts
```

2. Insert the following lines at the end of the `/etc/hosts` file.

Note that you should switch out the addresses used below for your servers' private addresses.

Note that you should also modify the `/etc/hosts` on your development machine to point to the same 
ip address(es) should you want to access the Hadoop NameNode Web UI. 

(This might be useful if you would like to traverse the hdfs via the Web UI without 
using `hdfs dfs` via console)

```text
172.21.148.207 namenode
172.21.148.207 ddatanode
```

Note that hostnames referenced in the `docker-compose.yml` configuration as referenced above corresponds to the hostname that are being added and mapped in the `/etc/hosts` file.

3. Save the file.

### Ports used
| Port  | Description                                         |
|-------|-----------------------------------------------------|
| 5432  | PostgreSQL listening port                           |
| 7473  | Neo4j online Web UI (https) [Not Enabled]            |
| 7474  | Neo4j online Web UI (http)                           |
| 7687  | Neo4j bolt listening port                           |
| 9083  | Hive metastore thrift listening port                |
| 10000 | Hive server2 thrift listening port                  |
| 50070 | Hadoop NameNode Web UI listening port                |
| 8020  | Hadoop NameNode metadata service IPC listening port |
| 50075 | DataNode WebUI to access the status, logs, etc      |
| 50010 | Custom DataNode HDFS protocol for data transfer     |