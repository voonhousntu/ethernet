# Deployment Guide

## Step-by-step guide
The step-by-step guide to deploy the Ethernet application is documented below. 

### 1. Installing docker
Please follow the guides [here](https://docs.docker.com/engine/install/ubuntu/) to install `docker`.

### 2. Installing docker-compose
Please follow the guides [here](https://linuxize.com/post/how-to-install-and-use-docker-compose-on-ubuntu-18-04/) to install `docker-compose`.

### 3. Running the docker images
Execute the following commands to run the docker images.
```shell
make deploy-docker-deps
```

### 4. Add the required hostname mappings into the host-file
Please follow the guide which can be found [here](DEVELOPMENT_GUIDE.md#how-to-modify-etchosts).

### 5. Build Ethernet-core (backend) and Python protos
Execute the following commands to build the `Ethernet-core` backend.

```shell
make build
```