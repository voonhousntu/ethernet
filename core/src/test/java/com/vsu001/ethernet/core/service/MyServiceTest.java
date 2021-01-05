package com.vsu001.ethernet.core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.vsu001.ethernet.core.HelloReply;
import com.vsu001.ethernet.core.HelloRequest;
import io.grpc.internal.testing.StreamRecorder;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringBootTest
@SpringJUnitConfig(classes = {MyServiceUnitTestConfiguration.class})
// Spring doesn't start without a config (might be empty)
// Don't use @EnableAutoConfiguration in this scenario
public class MyServiceTest {

  @Autowired
  private MyServiceImpl myService;

  @Test
  void testSayHello() throws Exception {
    HelloRequest request = HelloRequest.newBuilder()
        .setName("Test")
        .build();
    StreamRecorder<HelloReply> responseObserver = StreamRecorder.create();
    myService.sayHello(request, responseObserver);
    if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
      Assertions.fail("The call did not terminate in time");
    }
    assertNull(responseObserver.getError());
    List<HelloReply> results = responseObserver.getValues();
    assertEquals(1, results.size());
    HelloReply response = results.get(0);
    assertEquals(HelloReply.newBuilder()
        .setMessage("Hello ==> Test")
        .build(), response);
  }

}