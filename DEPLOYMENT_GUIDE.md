# Deployment Guide

The step-by-step guide to deploy the EtherNet application is documented below.

Java version `java --version`: 
```shell
openjdk 11.0.11 2021-04-20
OpenJDK Runtime Environment (build 11.0.11+9-Ubuntu-0ubuntu2)
OpenJDK 64-Bit Server VM (build 11.0.11+9-Ubuntu-0ubuntu2, mixed mode, sharing)
```

Python3 version `python3 --version`:
```shell
Python 3.9.5
```

## 1. Installing docker
Please follow the guides [here](https://docs.docker.com/engine/install/ubuntu/) 
to install `docker`.

## 2. Installing docker-compose
Please follow the guides [here](https://linuxize.com/post/how-to-install-and-use-docker-compose-on-ubuntu-18-04/) 
to install `docker-compose`.

## 3. Install Python dependencies
Install the python dependencies required to compile and produce the python protos and run RPyC.

### 3.1. Install Make
Please ensure that you have `make` installed.
```shell
sudo apt install make
```

### 3.2. Install virtualenv
Install `virtualenv`.

```shell
sudo apt install -y python3-virtualenv
```

### 3.3. Install Serving dependencies
```shell
make install-serving-dep
```

## 4. Create the EtherNet asset and working directories
```shell
make create-ethernet-asset-dir
```

## 5. Running the docker images
Please make sure that the makefile is in the current directory that you are in.

### 5.1. Deploy Docker containers
Execute the command below to deploy the docker containers.

```shell
make deploy-docker-deps
```

### 5.2. Listing Docker containers
Execute the command below to list the docker containers that are deployed and running. 
```shell
docker ps -a
```

The above command should yield the following output in your console:
```shell
CONTAINER ID   IMAGE                                             COMMAND                  CREATED          STATUS                    PORTS                                                                                            NAMES
82156f941ce0   neo4j:latest                                      "/sbin/tini -g -- /d…"   5 seconds ago    Up 4 seconds              0.0.0.0:7474->7474/tcp, :::7474->7474/tcp, 7473/tcp, 0.0.0.0:7687->7687/tcp, :::7687->7687/tcp   docker-neo4j
ff8d5ec288b2   bde2020/hive:2.3.2-postgresql-metastore           "entrypoint.sh /bin/…"   34 seconds ago   Up 31 seconds             0.0.0.0:10000->10000/tcp, :::10000->10000/tcp, 10002/tcp                                         docker-hive_hive-server_1
b2f7da34b265   bde2020/hive:2.3.2-postgresql-metastore           "entrypoint.sh /opt/…"   34 seconds ago   Up 31 seconds             10000/tcp, 0.0.0.0:9083->9083/tcp, :::9083->9083/tcp, 10002/tcp                                  docker-hive_hive-metastore_1
fded5a95efa9   bde2020/hadoop-namenode:2.0.0-hadoop2.7.4-java8   "/entrypoint.sh /run…"   34 seconds ago   Up 31 seconds (healthy)   0.0.0.0:8020->8020/tcp, :::8020->8020/tcp, 0.0.0.0:50070->50070/tcp, :::50070->50070/tcp         docker-hive_namenode_1
6beedc201153   bde2020/hadoop-datanode:2.0.0-hadoop2.7.4-java8   "/entrypoint.sh /run…"   34 seconds ago   Up 31 seconds (healthy)   0.0.0.0:50010->50010/tcp, :::50010->50010/tcp, 0.0.0.0:50075->50075/tcp, :::50075->50075/tcp     docker-hive_datanode_1
94ab94cb5efe   bde2020/hive-metastore-postgresql:2.3.0           "/docker-entrypoint.…"   35 seconds ago   Up 31 seconds             0.0.0.0:5432->5432/tcp, :::5432->5432/tcp                                                        docker-hive_hive-metastore-postgresql_
```

### 5.3. Creating Hive schema and tables
Execute the command below to initialise Hive with the required schema and tables.

```shell
make init-hive
```


## 6. Add the required hostname mappings into the host-file
For the following step, this should be done on the server hosting your project and the client 
machine that is going to consume the EtherNet services.

In short, modification of the `/etc/hosts` file needs to be done on two places.

Please follow the guide which can be found 
[here](DEVELOPMENT_GUIDE.md#how-to-modify-etchosts).


## 7. Build EtherNet-core (backend) and Python protos

## 7.1. Modify the EtherNet-core configs
Before compiling please ensure that the EtherNet-core configurations are configured correctly.
This can be done by modifying the `core/src/main/resources/appliaction.yml` file.

The key things to change are:
1. spring.datasource.hivedb.url
2. spring.neo4j.uri
3. spring.data.neo4j (Leave it unchanged if you are following this deployment guide)
4. ethernet.work-dir (Leave it unchanged if you are following this deployment guide)
5. ethernet.rpyc-host
6. ethernet.rpyc-port (Leave it unchanged if you are following this deployment guide)

```shell
nano core/src/main/resources/application.yml
```


## 7.2. Build the EtherNet-core jar
Execute the following commands to build the `ethernet-core` backend.

```shell
make build
```

## 8. Deploy EtherNet-core (backend)

### 8.1. Create GCP private key
This section contains the steps required to enable the BigQuery API and 
creating a private key for a service account to be used by Ethernet-core.

1. Enable the BigQuery API using the 
   link [here](https://console.cloud.google.com/bigquery?project=).
2. Navigate to the mange BigQuery credentials page using the 
   link [here](https://console.cloud.google.com/apis/api/bigquery.googleapis.com/credentials?project=).
3. Create a new credential for a service account; 
   this step can be skipped if you would like to use an existing account.
4. Click `edit` on the account of choice.
5. Navigate to the `keys` tab.
6. Click on the `ADD KEY` button.
7. Select the `JSON` key type.

### 8.2. Deploy EtherNet-Core
As the EtherNet-Core jar accesses the `serving` assets using a relative path 
(e.g. `serving/run_neo4j_import.py`), the EtherNet Core jar needs to be in the same directory 
as `serving` folder.

To enforce this, move the EtherNet-Core jar out from its target folder by executing the command 
below:
```shell
mv core/target/core-0.0.1-SNAPSHOT.jar .
```

Using the private key that you have created in `6.1. Create GCP private key` 
with the path of `./path/to/google_private_key.json`
deploy EtherNet-core with the following command.

```shell
GOOGLE_APPLICATION_CREDENTIALS=./path/to/google_private_key.json java -jar core-0.0.1-SNAPSHOT.jar
```

To start the EtherNet-core in a screen, use the command below:
```shell
screen -s ethernet_core -dm GOOGLE_APPLICATION_CREDENTIALS=./path/to/google_private_key.json java -jar core-0.0.1-SNAPSHOT.jar
```

## 9. Run RPyC server

### 9.1. Install screen
```shell
sudo apt install -y screen
```

### 9.2. Start the RPyC Server
The command below will start a screen with the name `rpyc_server`, run the RPyC server and detach 
from it.

```shell
make start-rpyc-server
```

## 10. How files are distributed
By following the steps above (1 to 9), you should obtain a directory tree as shown below.

Do note that it is important that the `ethernet_assets` and `ethernet_work_dir` 
are in your `$HOME` directory.

```text
/home/user
├── ethernet_assets
│   └── headers
│       ├── addresses.csv
│       ├── blocks.csv
│       ├── token_transfers.csv
│       ├── traces.csv
│       └── transactions.csv
├── ethernet_work_dir
│   ├── blocks.cache
│   ├── contracts.cache
│   ├── token_transfers.cache
│   ├── traces.cache
│   └── transactions.cache
├── neo4j    # Automatically when by docker generated
│   ├── data
│   │   ├── databases
│   │   ├── dbms
│   │   └── transactions
│   ├── import
│   ├── logs
│   │   └── debug.log
│   └── plugins
└── repository
    ├── ethernet
    │   ├── DEPLOYMENT_GUIDE.md
    │   ├── DEVELOPMENT_GUIDE.md
    │   ├── Makefile
    │   ├── README.md
    │   ├── core
    │   ├── core-0.0.1-SNAPSHOT.jar
    │   ├── ethernet_work_dir
    │   ├── headers
    │   ├── infra
    │   ├── pom.xml
    │   ├── sdk
    │   └── serving
    └── gcp_private_key.json
```