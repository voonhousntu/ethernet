PROJECT_ROOT 	:= $(shell git rev-parse --show-toplevel)

# General

protos:
	compile-protos-java compile-protos-python

build:
	protos build-java

create-ethernet-asset-dir:
	mkdir $$HOME/ethernet_assets; \
	mkdir $$HOME/ethernet_work_dir; \
	cp -R headers $$HOME/ethernet_assets/.

# Java

build-java:
	mvn clean verify

compile-protos-java:
	mvn protobuf:compile; mvn protobuf:compile-custom

# Python

compile-protos-python:
	cd ./core/src/main/proto; \
	python3 -m grpc_tools.protoc -I . \
			--python_out=../../../../sdk/python/ethernet/core \
			*.proto; \
		python3 -m grpc_tools.protoc -I . \
			--grpc_python_out=../../../../sdk/python/ethernet/core \
			*Service.proto; \
	cd ../../../../; \
	python3 ./infra/protoc_utils/fix_pb2.py "./sdk/python/ethernet/core/*.py"

# Docker

deploy-docker-deps:
	deploy-hive-hadoop deploy-neo4j

deploy-hive-hadoop:
	cd ./infra/docker-hive; docker-compose up -d

deploy-neo4j:
	docker run \
			--name docker-neo4j \
			-p7474:7474 -p7687:7687 \
			-d \
			-v $HOME/neo4j/data:/data \
			-v $HOME/neo4j/logs:/logs \
			-v $HOME/neo4j/import:/var/lib/neo4j/import \
			-v $HOME/neo4j/plugins:/plugins \
			-v $HOME/ethernet_assets:/ethernet_assets \
			-v $HOME/ethernet_work_dir:/ethernet_work_dir \
			--env NEO4J_AUTH=neo4j/test \
			neo4j:latest
