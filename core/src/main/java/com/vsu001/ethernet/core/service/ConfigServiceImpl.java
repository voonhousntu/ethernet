package com.vsu001.ethernet.core.service;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@GrpcService
public class ConfigServiceImpl extends ConfigServiceGrpc.ConfigServiceImplBase {

  @Value("${spring.neo4j.uri}")
  private String neo4jUri;

  @Value("${spring.data.neo4j.username}")
  private String neo4jUsername;

  @Value("${spring.data.neo4j.password}")
  private String neo4jPassword;

  @Override
  public void getNeo4jServingConfig(
      GetNeo4jServingConfigRequest request,
      StreamObserver<GetNeo4jServingConfigResponse> responseObserver
  ) {
    log.info("Returning Neo4j connection configuration");

    GetNeo4jServingConfigResponse response = GetNeo4jServingConfigResponse.newBuilder()
        .setConnectionUri(neo4jUri)
        .setUsername(neo4jUsername)
        .setPassword(neo4jPassword)
        .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

}
