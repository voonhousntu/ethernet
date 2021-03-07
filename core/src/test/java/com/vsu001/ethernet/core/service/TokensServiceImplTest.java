package com.vsu001.ethernet.core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
    "grpc.server.port=2004",
    "grpc.client.GLOBAL.negotiationType=PLAINTEXT",
    "spring.datasource.hivedb.schema=TokensServiceImplTest"
})
@ActiveProfiles("test")
public class TokensServiceImplTest {

  @Autowired
  private TokensServiceImpl tokensService;

  @Test
  public void testFetchFromBq() {
    assertThrows(
        RuntimeException.class,
        () -> tokensService.fetchFromBq(UpdateRequest.getDefaultInstance()),
        "This method is not implemented"
    );
  }

  @Test
  public void testGetTableName() {
    String expected = "tokens";
    assertEquals(expected, tokensService.getTableName());
  }

  @Test
  public void testGetTmpTableName() {
    String expected = "tmp_tokens";
    assertEquals(expected, tokensService.getTmpTableName());
  }

  @Test
  public void testGetSchemaStr() {
    String expected =
        "`address` string,`symbol` string,"
            + "`name` string,`decimals` string,"
            + "`total_supply` string,`block_timestamp` timestamp,"
            + "`block_number` bigint,`block_hash` string";
    assertEquals(expected, tokensService.getSchemaStr());
  }

  @Test
  public void testGetStructStr() {
    String expected =
        "struct<"
            + "address:string,symbol:string,"
            + "name:string,decimals:string,"
            + "total_supply:string,block_timestamp:timestamp,"
            + "block_number:bigint,block_hash:string>";
    assertEquals(expected, tokensService.getStructStr());
  }

}
