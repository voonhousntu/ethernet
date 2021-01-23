package com.vsu001.ethernet.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.vsu001.ethernet.core.model.BlockTimestampMapping;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class BlockTsMappingRepositoryTest {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private GenericHiveRepository hiveRepository;

  @Autowired
  private BlockTsMappingRepository blockTsMappingRepository;

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

  private void insertTestData() {
    String insertQuery =
        "INSERT INTO %s.block_timestamp_mapping VALUES "
            + "(0, '1970-01-01 07:30:00'), (1, '2015-07-30 23:45:08')";
    insertQuery = String.format(insertQuery, hiveRepository.getSchema());
    jdbcTemplate.execute(insertQuery);
  }

  @Test
  public void testFindMostRecent() {
    // Ensure we are using the test schema
    assertEquals("ethernet_test", hiveRepository.getSchema());

    // Should return null as table is empty
    BlockTimestampMapping blockTimestampMapping = blockTsMappingRepository.findMostRecent();
    assertNull(blockTimestampMapping);

    // Insert a few rows
    insertTestData();

    // Should `Block` with `number` field of 1L
    blockTimestampMapping = blockTsMappingRepository.findMostRecent();
    assertEquals(1L, blockTimestampMapping.getNumber());
    assertEquals(1438271108000L, blockTimestampMapping.getTimestamp().getSeconds());
  }

  @Test
  public void testFindByNumber() {
    // Ensure we are using the test schema
    assertEquals("ethernet_test", hiveRepository.getSchema());

    // Should return null as table is empty
    BlockTimestampMapping blockTimestampMapping = blockTsMappingRepository.findByNumber(1L);
    assertNull(blockTimestampMapping);

    // Insert a few rows
    insertTestData();

    // Should `Block` with `number` field of 1L
    blockTimestampMapping = blockTsMappingRepository.findByNumber(1L);
    assertEquals(1L, blockTimestampMapping.getNumber());
    assertEquals(1438271108000L, blockTimestampMapping.getTimestamp().getSeconds());
  }

  @Test
  public void testFindByNumbers() {
    // Ensure we are using the test schema
    assertEquals("ethernet_test", hiveRepository.getSchema());

    List<Long> longList = new ArrayList<>(Arrays.asList(0L, 1L));

    // Should return an empty list as table is empty
    List<BlockTimestampMapping> blockTimestampMappings = blockTsMappingRepository.findByNumbers(longList);
    assertEquals(0, blockTimestampMappings.size());

    // Insert a few rows
    insertTestData();

    // Should `Block` with `number` field of 1L
    blockTimestampMappings = blockTsMappingRepository.findByNumbers(longList);
    assertEquals(2, blockTimestampMappings.size());
  }

}
