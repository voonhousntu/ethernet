package com.vsu001.ethernet.core.service;

import com.vsu001.ethernet.core.repository.GenericHiveRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
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

}
