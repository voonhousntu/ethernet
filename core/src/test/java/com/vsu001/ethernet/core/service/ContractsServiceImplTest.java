package com.vsu001.ethernet.core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.cloud.bigquery.TableResult;
import com.vsu001.ethernet.core.repository.GenericHiveRepository;
import com.vsu001.ethernet.core.util.NonceUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
    "grpc.server.port=2003",
    "grpc.client.GLOBAL.negotiationType=PLAINTEXT",
    "spring.datasource.hivedb.schema=ContractsServiceImplTest"
})
@ActiveProfiles("test")
public class ContractsServiceImplTest {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private GenericHiveRepository hiveRepository;

  @Autowired
  private ContractsServiceImpl contractsService;

  @Autowired
  private BlockTsMappingServiceImpl blockTsMappingService;

  @Autowired
  private CoreServiceImpl coreService;

  @BeforeEach
  public void init() {
    // Initialise the required schema
    String createSchema = "CREATE SCHEMA %s";
    createSchema = String.format(createSchema, hiveRepository.getSchema());
    jdbcTemplate.execute(createSchema);

    // Initialise the required tables
    // Create `blocks`
    String createBlocksTable =
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
    createBlocksTable = String.format(createBlocksTable, hiveRepository.getSchema());

    // Create `contracts`
    String createContractsTable =
        "CREATE TABLE %s.contracts "
            + "(`address` string,"
            + "`bytecode` string,"
            + "`function_sighashes` string,"
            + "`is_erc20` boolean,"
            + "`is_erc721` boolean,"
            + "`block_hash` string,"
            + "`block_number` bigint,"
            + "`block_timestamp` timestamp) "
            + "STORED AS ORC TBLPROPERTIES ('ORC.COMPRESS' = 'ZLIB')";
    createContractsTable = String.format(createContractsTable, hiveRepository.getSchema());

    // Create `block_timestamp_mapping`
    String createBlockTsMapTable =
        "CREATE TABLE %s.block_timestamp_mapping "
            + "( `number` bigint, `timestamp` timestamp ) "
            + "STORED AS ORC TBLPROPERTIES ('ORC.COMPRESS' = 'ZLIB')";
    createBlockTsMapTable = String.format(createBlockTsMapTable, hiveRepository.getSchema());

    jdbcTemplate.execute(createBlocksTable);
    jdbcTemplate.execute(createContractsTable);
    jdbcTemplate.execute(createBlockTsMapTable);
  }

  @AfterEach
  public void teardown() {
    // Remove schema that have been initialised
    String dropSchema = "DROP SCHEMA IF EXISTS %s CASCADE";
    dropSchema = String.format(dropSchema, hiveRepository.getSchema());
    jdbcTemplate.execute(dropSchema);
  }

  /**
   * Inserts dummy rows into the `blocks` table
   *
   * @param rows Number of rows to insert
   */
  private void insertDataIntoBlocks(int rows) {
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
  public void testFetchFromBq() {
    // Create an update request
    UpdateRequest updateRequest = UpdateRequest.newBuilder()
        .setStartBlockNumber(0L)
        .setEndBlockNumber(447767L)
        .build();

    TableResult tableResult = null;
    try {
      String nonce = NonceUtil.generateNonce();

      // Update `block_timestamp_mapping` table
      coreService.fetchAndPopulateHiveTable(blockTsMappingService, updateRequest, nonce);

      // Insert two rows into the required table
      insertDataIntoBlocks(2);

      tableResult = contractsService.fetchFromBq(updateRequest);
    } catch (InterruptedException | IOException e) {
      e.printStackTrace();
    }

    assertEquals(3053L, tableResult.getTotalRows());
  }

  @Test
  public void testGetTableName() {
    String expected = "contracts";
    assertEquals(expected, contractsService.getTableName());
  }

  @Test
  public void testGetTmpTableName() {
    String expected = "tmp_contracts";
    assertEquals(expected, contractsService.getTmpTableName());
  }

  @Test
  public void testGetSchemaStr() {
    String expected =
        "`address` string,`bytecode` string,"
            + "`function_sighashes` string,`is_erc20` boolean,"
            + "`is_erc721` boolean,`block_timestamp` timestamp,"
            + "`block_number` bigint,`block_hash` string";
    assertEquals(expected, contractsService.getSchemaStr());
  }

  @Test
  public void testGetStructStr() {
    String expected =
        "struct<"
            + "address:string,bytecode:string,"
            + "function_sighashes:string,is_erc20:boolean,"
            + "is_erc721:boolean,block_timestamp:timestamp,"
            + "block_number:bigint,block_hash:string>";
    assertEquals(expected, contractsService.getStructStr());
  }

}
