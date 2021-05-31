# Deployment Guide

The step-by-step guide to deploy the EtherNet application is documented below. 

## 1. Installing docker
Please follow the guides [here](https://docs.docker.com/engine/install/ubuntu/) to install `docker`.

## 2. Installing docker-compose
Please follow the guides [here](https://linuxize.com/post/how-to-install-and-use-docker-compose-on-ubuntu-18-04/) to install `docker-compose`.

## 3. Running the docker images
Execute the following commands to run the docker images.
```shell
make deploy-docker-deps
```

## 4. Add the required hostname mappings into the host-file
Please follow the guide which can be found [here](DEVELOPMENT_GUIDE.md#how-to-modify-etchosts).

## 5. Build EtherNet-core (backend) and Python protos
Execute the following commands to build the `ethernet-core` backend.

```shell
make build
```

## 6. Deploy EtherNet-core (backend)

### 6.1. Create GCP private key
This section contains the steps required to enable the BigQuery API and creating a private key for a service account to be used by Ethernet-core.

1. Enable the BigQuery API using the link [here](https://console.cloud.google.com/bigquery?project=).
2. Navigate to the mange BigQuery credentials page using the link [here](https://console.cloud.google.com/apis/api/bigquery.googleapis.com/credentials?project=).
3. Create a new credential for a service account; this step can be skipped if you would like to use an existing account.
4. Click `edit` on the account of choice.
5. Navigate to the `keys` tab.
6. Click on the `ADD KEY` button.
7. Select the `JSON` key type.

### 6.2. Deploy EtherNet-core
Using the private key that you have created in `6.1. Create GCP private key` with the path of `./path/to/google_private_key.json`
deploy EtherNet-core with the following command.

```shell
PATH=./path/to/google_private_key.json java -jar ./path/to/ethernet-core.jar
```