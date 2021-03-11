package com.vsu001.ethernet.core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import com.vsu001.ethernet.core.config.EthernetConfig;
import com.vsu001.ethernet.core.config.Neo4jConfig;
import io.grpc.internal.testing.StreamRecorder;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
    "grpc.server.port=2002",
    "grpc.client.GLOBAL.negotiationType=PLAINTEXT",
    "spring.datasource.hivedb.schema=ConfigServiceImplTest"
})
@ActiveProfiles("test")
public class ConfigServiceImplTest {


  @Value("${spring.neo4j.uri}")
  private String neo4jUri;

  @Value("${spring.data.neo4j.username}")
  private String neo4jUsername;

  @Value("${spring.data.neo4j.password}")
  private String neo4jPassword;

  @Autowired
  private Neo4jConfig neo4jConfig;

  @Autowired
  private EthernetConfig ethernetConfig;

  private ConfigServiceImpl configService;

  @BeforeEach
  public void setup() {
    configService = new ConfigServiceImpl(neo4jConfig, ethernetConfig);
  }

  @Test
  void testgetNeo4jServingConfig() throws Exception {
    GetNeo4jServingConfigRequest request = GetNeo4jServingConfigRequest.newBuilder().build();

    StreamRecorder<GetNeo4jServingConfigResponse> responseObserver = StreamRecorder.create();
    configService.getNeo4jServingConfig(request, responseObserver);
    if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
      fail("The call did not terminate in time");
    }

    assertNull(responseObserver.getError());
    List<GetNeo4jServingConfigResponse> results = responseObserver.getValues();
    assertEquals(1, results.size());

    GetNeo4jServingConfigResponse response = results.get(0);
    assertEquals(
        GetNeo4jServingConfigResponse.newBuilder()
            .setConnectionUri(neo4jUri)
            .setUsername(neo4jUsername)
            .setPassword(neo4jPassword)
            .build(),
        response
    );
  }

}