package com.vsu001.ethernet.core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import io.grpc.internal.testing.StreamRecorder;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ExampleServiceTest {

  private ExampleServiceImpl exampleService;

  @BeforeEach
  public void setup() {
    exampleService = new ExampleServiceImpl();
  }

  @Test
  void testSayHello() throws Exception {
    HelloRequest request = HelloRequest.newBuilder()
        .setName("Test")
        .build();
    StreamRecorder<HelloReply> responseObserver = StreamRecorder.create();
    exampleService.sayHello(request, responseObserver);
    if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
      fail("The call did not terminate in time");
    }
    assertNull(responseObserver.getError());
    List<HelloReply> results = responseObserver.getValues();
    assertEquals(1, results.size());
    HelloReply response = results.get(0);
    assertEquals(
        HelloReply.newBuilder()
            .setMessage("Hello ==> Test")
            .build(), response);
  }

}