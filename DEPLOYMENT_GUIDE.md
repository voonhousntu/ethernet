# Deployment Guide

The step-by-step guide to deploy the EtherNet application is documented below. 

## 1. Installing docker
Please follow the guides [here](https://docs.docker.com/engine/install/ubuntu/) 
to install `docker`.

## 2. Installing docker-compose
Please follow the guides [here](https://linuxize.com/post/how-to-install-and-use-docker-compose-on-ubuntu-18-04/) 
to install `docker-compose`.

## 3. Install Python dependencies
Install the python dependencies required to compile and produce the python protos and run RPyC.

```shell
make install-serving-dep
```

## 4. Running the docker images
Please make sure that the makefile is in the current directory that you are in.

Execute the following commands to run the docker images.

```shell
make deploy-docker-deps
```

## 5. Add the required hostname mappings into the host-file
Please follow the guide which can be 
found [here](DEVELOPMENT_GUIDE.md#how-to-modify-etchosts).

## 6. Build EtherNet-core (backend) and Python protos
Execute the following commands to build the `ethernet-core` backend.

```shell
make build
```

## 7. Deploy EtherNet-core (backend)

### 7.1. Create GCP private key
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

### 7.2. Deploy EtherNet-core
Using the private key that you have created in `6.1. Create GCP private key` 
with the path of `./path/to/google_private_key.json`
deploy EtherNet-core with the following command.

```shell
PATH=./path/to/google_private_key.json java -jar ./path/to/ethernet-core.jar
```

## 8. Run RPyC server
```shell
make start-rpyc-server
```

## 9. How files are distributed
The tree below shows how files are distributed in my deployment.
Do note that it is important that the `ethernet_assets` and `ethernet_work_dir` 
are in your `$HOME` directory.

Do note that some of the files have been shifted around and do not correspond 
to the file structure of the repository.

Executing the above steps will not produce the same file structure!

```text
/home/user
├── Desktop
│   ├── docker-hive
│   │   ├── Dockerfile
│   │   ├── Makefile
│   │   ├── README.md
│   │   ├── conf
│   │   ├── docker-compose.backup.yml
│   │   ├── docker-compose.yml
│   │   ├── entrypoint.sh
│   │   ├── hadoop-hive.env
│   │   └── startup.sh
│   ├── ethernet
│   │   ├── core-0.0.1-SNAPSHOT.jar
│   │   ├── serving
│   │   │   ├── requirements.txt
│   │   │   ├── run_neo4j_import.py
│   │   │   ├── switch_db.py
│   │   │   └── venv
│   │   └── gcp_private_key.json
│   └── jupyter
│       ├── demo_notebook.ipynb
│       ├── ethernet
│       └── venv
├── ethernet_assets
│   └── headers
│       ├── addresses.csv
│       ├── blocks.csv
│       ├── token_transfers.csv
│       ├── traces.csv
│       └── transactions.csv
├── ethernet_work_dir
│   ├── token_transfers.cache
│   ├── traces.cache
│   ├── transactions.cache
└── neo4j           # Automatically when by docker generated
    ├── data
    │   ├── databases
    │   ├── dbms
    │   └── transactions
    ├── import
    ├── logs
    │   ├── debug.log
    │   └── token-transfers-447500-607767-20210507T0752Z_import-report.txt
    └── plugins
```