package com.vsu001.ethernet.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
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
public class BlockRepositoryTest {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private GenericHiveRepository hiveRepository;

  @Autowired
  private BlockRepository blockRepository;

  @BeforeEach
  public void init() {
    // Initialise the required schema
    String createSchema = "CREATE SCHEMA %s";
    createSchema = String.format(createSchema, hiveRepository.getSchema());
    jdbcTemplate.execute(createSchema);

    // Initialise the required tables
    // Create `blocks`
    String createTable =
        "CREATE TABLE %s.blocks "
            + "(`number` bigint,"
            + "`timestamp` timestamp,"
            + "`hash` string,"
            + "`parent_hash` string,"
            + "`nonce` string,"
            + "`sha3_uncles` string,"
            + "`logs_bloom` string,"
            + "`transactions_root` string,"
            + "`state_root` string,"
            + "`receipts_root` string,"
            + "`miner` string,"
            + "`difficulty` bigint,"
            + "`total_difficulty` bigint,"
            + "`size` bigint,"
            + "`extra_data` string,"
            + "`gas_limit` bigint,"
            + "`gas_used` bigint,"
            + "`transaction_count` bigint) "
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

  private void insertData(int rows) {
    String insertQuery = "INSERT INTO %s.blocks VALUES %s";
    String rowData = "(%s,'2018-12-09 00:00:00','','','','','','','','','',"
        + "2318831663517954,8199845831287802706171,20485,'0x',7992222,7977738,129)";

    List<String> rowsToInsert = new ArrayList<>();
    for (int i = 0; i < rows; i++) {
      rowsToInsert.add(String.format(rowData, i));
    }

    String insertSql = String.format(
        insertQuery,
        hiveRepository.getSchema(),
        String.join(",", rowsToInsert)
    );
    jdbcTemplate.execute(insertSql);
  }

  @Test
  public void testFindByNumberRange() {
    // Ensure we are using the test schema
    assertEquals("ethernet_test", hiveRepository.getSchema());

    // Should return an empty list as table is empty
    List<Long> longList = blockRepository.findByNumberRange(0L, 1L);

    assertEquals(0, longList.size());

    // Insert a few rows
    insertData(5);

    longList = blockRepository.findByNumberRange(0L, 1L);
    assertEquals(2, longList.size());

    longList = blockRepository.findByNumberRange(0L, 4L);
    assertEquals(5, longList.size());
  }

}
