package com.vsu001.ethernet.core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
    "grpc.server.port=2003",
    "grpc.client.GLOBAL.negotiationType=PLAINTEXT",
    "spring.datasource.hivedb.schema=LogsServiceImplTest"
})
@ActiveProfiles("test")
public class LogsServiceImplTest {

  @Autowired
  private LogsServiceImpl logsService;

  @Test
  public void testFetchFromBq() {
    assertThrows(
        RuntimeException.class,
        () -> logsService.fetchFromBq(UpdateRequest.getDefaultInstance()),
        "This method is not implemented"
    );
  }

  @Test
  public void testGetTableName() {
    String expected = "logs";
    assertEquals(expected, logsService.getTableName());
  }

  @Test
  public void testGetTmpTableName() {
    String expected = "tmp_logs";
    assertEquals(expected, logsService.getTmpTableName());
  }

  @Test
  public void testGetSchemaStr() {
    String expected =
        "`log_index` bigint,`transaction_hash` string,"
            + "`transaction_index` bigint,`address` string,"
            + "`data` string,`topics` string,"
            + "`block_timestamp` timestamp,`block_number` bigint,"
            + "`block_hash` string";
    assertEquals(expected, logsService.getSchemaStr());
  }

  @Test
  public void testGetStructStr() {
    String expected =
        "struct<"
            + "log_index:bigint,transaction_hash:string,"
            + "transaction_index:bigint,address:string,"
            + "data:string,topics:string,"
            + "block_timestamp:timestamp,block_number:bigint,"
            + "block_hash:string>";
    assertEquals(expected, logsService.getStructStr());
  }

}
