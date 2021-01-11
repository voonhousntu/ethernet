package com.vsu001.ethernet.core.service;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
public class CoreServiceImpl extends CoreServiceGrpc.CoreServiceImplBase {

  @Override
  public void updateBlockTsMapping(
      BlockTsMappingUpdateRequest request,
      StreamObserver<BlockTsMappingUpdateResponse> responseObserver) {

  }

}
