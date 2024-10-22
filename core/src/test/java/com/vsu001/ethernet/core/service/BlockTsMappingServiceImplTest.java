package com.vsu001.ethernet.core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.cloud.bigquery.TableResult;
import com.vsu001.ethernet.core.repository.GenericHiveRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
    "grpc.server.port=2001",
    "grpc.client.GLOBAL.negotiationType=PLAINTEXT",
    "spring.datasource.hivedb.schema=BlockTsMappingServiceImplTest"
})
@ActiveProfiles("test")
class BlockTsMappingServiceImplTest {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private GenericHiveRepository hiveRepository;

  @Autowired
  private BlockTsMappingServiceImpl blockTsMappingService;

  @BeforeEach
  public void init() {
    // Initialise the required schema
    String createSchema = "CREATE SCHEMA %s";
    createSchema = String.format(createSchema, hiveRepository.getSchema());
    jdbcTemplate.execute(createSchema);

    // Initialise the required tables
    // Create `block_timestamp_mapping`
    String createTable =
        "CREATE TABLE %s.block_timestamp_mapping "
            + "( `number` bigint, `timestamp` timestamp ) "
            + "STORED AS ORC TBLPROPERTIES ('ORC.COMPRESS' = 'ZLIB')";
    createTable = String.format(createTable, hiveRepository.getSchema());
    jdbcTemplate.execute(createTable);
  }

  @AfterEach
  public void teardown() {
    // Remove schema that have been initialised
    String dropSchema = "DROP SCHEMA IF EXISTS %s CASCADE";
    dropSchema = String.format(dropSchema, hiveRepository.getSchema());
    jdbcTemplate.execute(dropSchema);
  }

  @Test
  public void testFetchFromBq() {
    UpdateRequest updateRequest = UpdateRequest.getDefaultInstance();

    TableResult tableResult = null;
    try {
      tableResult = blockTsMappingService.fetchFromBq(updateRequest);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    assertEquals(447768L, tableResult.getTotalRows());
  }

  @Test
  public void testGetTableName() {
    String expected = "block_timestamp_mapping";
    assertEquals(expected, blockTsMappingService.getTableName());
  }

  @Test
  public void testGetTmpTableName() {
    String expected = "tmp_block_timestamp_mapping";
    assertEquals(expected, blockTsMappingService.getTmpTableName());
  }

  @Test
  public void testGetSchemaStr() {
    String expected = "`timestamp` timestamp,`number` bigint";
    assertEquals(expected, blockTsMappingService.getSchemaStr());
  }

  @Test
  public void testGetStructStr() {
    String expected = "struct<timestamp:timestamp,number:bigint>";
    assertEquals(expected, blockTsMappingService.getStructStr());
  }

}
