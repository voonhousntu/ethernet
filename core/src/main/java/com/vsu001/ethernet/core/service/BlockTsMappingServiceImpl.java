package com.vsu001.ethernet.core.service;

import com.google.cloud.bigquery.TableResult;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Timestamp;
import com.vsu001.ethernet.core.model.BlockTimestampMapping;
import com.vsu001.ethernet.core.repository.BlockTsMappingRepository;
import com.vsu001.ethernet.core.util.BigQueryUtil;
import com.vsu001.ethernet.core.util.OrcFileWriter;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BlockTsMappingServiceImpl implements GenericService {

  private final BlockTsMappingRepository blockTsMappingRepository;

  private static final String TABLE_NAME = "block_timestamp_mapping";
  private static final String TMP_TABLE_NAME = "tmp_" + TABLE_NAME;
  private static final List<FieldDescriptor> FIELD_DESCRIPTOR_LIST = BlockTimestampMapping
      .getDescriptor().getFields();

  public BlockTsMappingServiceImpl(BlockTsMappingRepository blockTsMappingRepository) {
    this.blockTsMappingRepository = blockTsMappingRepository;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TableResult fetchFromBq(UpdateRequest request) throws InterruptedException {

    // For debugging
//    TableResult tableResult = BigQueryUtil.query(
//        BlockTimestampMapping.getDescriptor(),
//        "blocks",
//        "`timestamp` < '2015-07-30 15:46:52'"
//    );

    // Find the most recent BlockTimestampMapping in Hive
    BlockTimestampMapping blockTimestampMapping = blockTsMappingRepository.findMostRecent();

    // Convert to instant so we can get the ISO8601 timestamp format
    Timestamp ts = blockTimestampMapping.getTimestamp();
    Instant instantTs = Instant.ofEpochSecond(ts.getSeconds(), ts.getNanos());

    // Build query
    String queryCriteria = String.format(
        "`number` > '%s' AND `timestamp` >= '%s'",
        blockTimestampMapping.getNumber(),
        instantTs.toString()
    );

    // Get results from BigQuery
    TableResult tableResult = BigQueryUtil.query(
        BlockTimestampMapping.getDescriptor(),
        "blocks",
        queryCriteria
    );

    log.info("Rows fetched: [{}]", tableResult.getTotalRows());

    return tableResult;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getTableName() {
    return TABLE_NAME;
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

}
