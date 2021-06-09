PROJECT_ROOT 	:= $(shell git rev-parse --show-toplevel)

# General

protos: compile-protos-java compile-protos-python

build:  protos build-java

create-ethernet-asset-dir:
	mkdir $$HOME/ethernet_assets && \
	mkdir $$HOME/ethernet_work_dir && \
	cp -R headers $$HOME/ethernet_assets/. && \
	cd $$HOME/ethernet_work_dir && \
	touch blocks.cache && \
	touch contracts.cache && \
	touch token_transfers.cache && \
	touch traces.cache && \
	touch transactions.cache

# Java

build-java:
	mvn clean verify -Dmaven.test.skip=true

compile-protos-java:
	mvn protobuf:compile; mvn protobuf:compile-custom

# Python

compile-protos-python:
	cd ./core/src/main/proto; \
	../../../../serving/venv/bin/python3 -m grpc_tools.protoc -I . \
			--python_out=../../../../sdk/python/ethernet/core \
			*.proto; \
	../../../../serving/venv/bin/python3 -m grpc_tools.protoc -I . \
			--grpc_python_out=../../../../sdk/python/ethernet/core \
			*Service.proto; \
	cd ../../../../; \
	serving/venv/bin/python3 ./infra/protoc_utils/fix_pb2.py "./sdk/python/ethernet/core/*.py"

install-serving-dep:
	cd serving; \
	virtualenv -p /usr/bin/python3 venv && \
	venv/bin/pip3 install -r requirements.txt && \
	cd ..

start-rypc-server:
	screen -S rpyc_server -d -m serving/venv/bin/python3 serving/venv/bin/rpyc_classic.py --host 0.0.0.0 -p 18812

# Docker

deploy-docker-deps: deploy-hive-hadoop deploy-neo4j

deploy-hive-hadoop:
	cd ./infra/docker-hive; docker-compose up -d; cd ../..

deploy-neo4j:
	docker run \
			--name docker-neo4j \
			-p7474:7474 -p7687:7687 \
			-d \
			-v $$HOME/neo4j/data:/data \
			-v $$HOME/neo4j/logs:/logs \
			-v $$HOME/neo4j/import:/var/lib/neo4j/import \
			-v $$HOME/neo4j/plugins:/plugins \
			-v $$HOME/ethernet_assets:/ethernet_assets \
			-v $$HOME/ethernet_work_dir:/ethernet_work_dir \
			--env NEO4J_AUTH=neo4j/test \
			neo4j:latest
