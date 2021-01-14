package com.vsu001.ethernet.core.service;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
public class ExampleServiceImpl extends ExampleServiceGrpc.ExampleServiceImplBase {

  // TODO: This is just an example class, it can be removed later

  @Override
  public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
    log.info("Received HelloRequest with from [{}]", request.getName());

    HelloReply reply = HelloReply.newBuilder()
        .setMessage("Hello ==> " + request.getName())
        .build();
    responseObserver.onNext(reply);
    responseObserver.onCompleted();
  }

}
