package com.vsu001.ethernet.core.service;

import com.google.cloud.bigquery.TableResult;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.vsu001.ethernet.core.model.BlockTimestampMapping;
import com.vsu001.ethernet.core.model.Transaction;
import com.vsu001.ethernet.core.repository.BlockTsMappingRepository;
import com.vsu001.ethernet.core.repository.GenericHiveRepository;
import com.vsu001.ethernet.core.repository.TransactionRepository;
import com.vsu001.ethernet.core.util.BigQueryUtil;
import com.vsu001.ethernet.core.util.BlockUtil;
import com.vsu001.ethernet.core.util.DatetimeUtil;
import com.vsu001.ethernet.core.util.OrcFileWriter;
import com.vsu001.ethernet.core.util.ProcessUtil;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TransactionsServiceImpl implements GenericService {

  private static final String TMP_TABLE_NAME = "tmp_" + TransactionRepository.TABLE_NAME;
  private static final List<FieldDescriptor> FIELD_DESCRIPTOR_LIST = Transaction.getDescriptor()
      .getFields();
  private final GenericHiveRepository genericHiveRepository;
  private final BlockTsMappingRepository blockTsMappingRepository;

  public TransactionsServiceImpl(
      GenericHiveRepository genericHiveRepository,
      BlockTsMappingRepository blockTsMappingRepository
  ) {
    this.genericHiveRepository = genericHiveRepository;
    this.blockTsMappingRepository = blockTsMappingRepository;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TableResult fetchFromBq(UpdateRequest request) throws InterruptedException {
    // Find blocks that are already in Hive table
    List<Long> blockNumbers = genericHiveRepository.findByNumberRange(
        TransactionRepository.TABLE_NAME,
        request.getStartBlockNumber(),
        request.getEndBlockNumber()
    );

    // Find contiguous block numbers that are missing from the Hive table
    List<List<Long>> lLists = BlockUtil.findMissingContRange(
        blockNumbers,
        request.getStartBlockNumber(),
        request.getEndBlockNumber()
    );

    StringBuilder timestampSB = new StringBuilder("1=1 ");
    for (List<Long> lList : lLists) {
      if (lList.get(0).equals(lList.get(1))) {
        // Cost to run query will be the same as querying for a day's worth of data
        BlockTimestampMapping blockTspMapping = blockTsMappingRepository.findByNumber(lList.get(0));
        timestampSB.append("AND `block_timestamp` = ");
        timestampSB.append(
            String.format("'%s' ", BlockUtil.protoTsToISO(blockTspMapping.getTimestamp()))
        );
      } else {
        BlockTimestampMapping startBTM = blockTsMappingRepository.findByNumber(lList.get(0));
        BlockTimestampMapping endBTM = blockTsMappingRepository.findByNumber(lList.get(1));
        timestampSB.append("AND `block_timestamp` >= ");
        timestampSB.append(String.format("'%s' ", BlockUtil.protoTsToISO(startBTM.getTimestamp())));
        timestampSB.append("AND `block_timestamp` <= ");
        timestampSB.append(String.format("'%s' ", BlockUtil.protoTsToISO(endBTM.getTimestamp()))
        );
      }
    }

    String queryCriteria = String.format(
        timestampSB.toString() + " AND `block_number` NOT IN (%s)",
        blockNumbers.stream().map(String::valueOf).collect(Collectors.joining(","))
    );

    // Fetch results from BigQuery
    TableResult tableResult = BigQueryUtil.query(
        Transaction.getDescriptor(),
        "transactions",
        queryCriteria
    );

    // Table results should not be null
    assert tableResult != null;
    log.info("Rows fetched: [{}]", tableResult.getTotalRows());

    return tableResult;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getTableName() {
    return TransactionRepository.TABLE_NAME;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getTmpTableName() {
    return TMP_TABLE_NAME;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getSchemaStr() {
    return FIELD_DESCRIPTOR_LIST.stream()
        .map(s -> String.format("`%s` %s", s.getName(), OrcFileWriter.protoToOrcType(s)))
        .collect(Collectors.joining(","));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getStructStr() {
    return OrcFileWriter.protoToOrcStructStr(FIELD_DESCRIPTOR_LIST);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String doNeo4jImport(UpdateRequest request) throws IOException {
    String dbName = generateNeo4jDbName(request.getStartBlockNumber(), request.getEndBlockNumber());
    return doNeo4jImport(dbName);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String doNeo4jImport(String databaseName) throws IOException {
    // Export required block rows to CSV
    // TODO

    // Export required addresses rows to CSV
    // TODO

    // Do import to Neo4j
    String cmd = "sudo -u neo4j neo4j-admin import "
        + "--database " + databaseName + ".db "
        + "--report-file /tmp/import-report.txt "
        + "--nodes: Address \"headers/addresses.csv,ethernet_workdir/addresses/addresses.csv\""
        + "--nodes: Block \"headers/blocks.csv,ethernet_workdir/traces/blocks.csv\""
        + "--relationships:TRANSACTION"
        + "\"headers/transactions.csv,ethernet_workdir/transactions/transactions.csv\"";

    // Execute command
    ProcessUtil.createProcess(cmd);

    return databaseName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String generateNeo4jDbName(long blockStartNo, long blockEndNo) {
    return String.format(
        TABLE_NAME_PATTERN,
        TransactionRepository.TABLE_NAME,
        blockStartNo,
        blockEndNo,
        DatetimeUtil.getCurrentISOStr(ISO_STRING_PATTERN)
    );
  }

}
