package com.vsu001.ethernet.core.service;

import com.vsu001.ethernet.core.config.EthernetConfig;
import com.vsu001.ethernet.core.config.Neo4jConfig;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
public class ConfigServiceImpl extends ConfigServiceGrpc.ConfigServiceImplBase {

  private final Neo4jConfig neo4jConfig;
  private final EthernetConfig ethernetConfig;

  ConfigServiceImpl(
      Neo4jConfig neo4jConfig,
      EthernetConfig ethernetConfig) {
    this.neo4jConfig = neo4jConfig;
    this.ethernetConfig = ethernetConfig;
  }

  /**
   * gRPC service to handle request for Neo4J connection properties.
   * <p>
   * This service will return the connection url, username and password to connect to the Neo4j
   * backing store.
   *
   * @param request          Request object for getting Neo4J connection properties.
   * @param responseObserver StreamObserver wrapping a Neo4J config connection response.
   */
  @Override
  public void getNeo4jServingConfig(
      GetNeo4jServingConfigRequest request,
      StreamObserver<GetNeo4jServingConfigResponse> responseObserver
  ) {
    log.info("Returning Neo4j connection configuration");

    GetNeo4jServingConfigResponse response = GetNeo4jServingConfigResponse.newBuilder()
        .setConnectionUri(neo4jConfig.getNeo4jUri())
        .setUsername(neo4jConfig.getNeo4jUsername())
        .setPassword(neo4jConfig.getNeo4jPassword())
        .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void getServingConfig(GetServingConfigRequest request,
      StreamObserver<GetServingConfigResponse> responseObserver) {
    log.info("Returning Ethernet serving configurations");

    GetServingConfigResponse response = GetServingConfigResponse.newBuilder()
        .setRpycHost(ethernetConfig.getRpycHost())
        .setRpycPort(ethernetConfig.getRpycPort())
        .setDockerContainerName(ethernetConfig.getNeo4jContainerName())
        .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

}
