PROJECT_ROOT 	:= $(shell git rev-parse --show-toplevel)

# General

protos:
	compile-protos-java compile-protos-python

build:
	protos build-java

# Java

build-java:
	mvn clean verify

compile-protos-java:
	protobuf:compile

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
