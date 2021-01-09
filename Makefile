PROJECT_ROOT 	:= $(shell git rev-parse --show-toplevel)

# General

protos:
	compile-protos-python

build:
	protos build-java

# Java

build-java:
	mvn clean verify

# Python

compile-protos-python:
	cd ./core/src/main/proto; \
	python3 -m grpc_tools.protoc -I . \
			--python_out=../../../../sdk/python/ \
			--grpc_python_out=../../../../sdk/python/ \
			*.proto
