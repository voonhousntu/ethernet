package com.vsu001.ethernet.core.service;

import com.google.cloud.bigquery.TableResult;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Timestamp;
import com.vsu001.ethernet.core.model.BlockTimestampMapping;
import com.vsu001.ethernet.core.repository.BlockTsMappingRepository;
import com.vsu001.ethernet.core.util.BigQueryUtil;
import com.vsu001.ethernet.core.util.OrcFileWriter;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BlockTsMappingServiceImpl implements GenericService {

  private static final String TABLE_NAME = "block_timestamp_mapping";
  private static final String TMP_TABLE_NAME = "tmp_" + TABLE_NAME;
  private static final List<FieldDescriptor> FIELD_DESCRIPTOR_LIST = BlockTimestampMapping
      .getDescriptor().getFields();
  private final BlockTsMappingRepository blockTsMappingRepository;
  private final Environment environment;

  public BlockTsMappingServiceImpl(
      BlockTsMappingRepository blockTsMappingRepository,
      Environment environment
  ) {
    this.blockTsMappingRepository = blockTsMappingRepository;
    this.environment = environment;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TableResult fetchFromBq(UpdateRequest request) throws InterruptedException {
    // Find the most recent BlockTimestampMapping in Hive
    BlockTimestampMapping blockTimestampMapping = blockTsMappingRepository.findMostRecent();

    String iso8601Str;
    long blockNumber = -1L;
    if (blockTimestampMapping != null) {
      // Convert to instant so we can get the ISO8601 timestamp format
      Timestamp ts = blockTimestampMapping.getTimestamp();
      Instant instantTs = Instant.ofEpochSecond(ts.getSeconds(), ts.getNanos());
      blockNumber = blockTimestampMapping.getNumber();
      iso8601Str = instantTs.toString();
    } else {
      // Handle for case where no most recent block is found
      iso8601Str = "1970-01-01 00:00:00";
    }

    String query = "`number` > %s AND `timestamp` >= '%s'";
    //Check if Active profiles contains "local" or "test"
    if (Arrays.stream(environment.getActiveProfiles()).anyMatch(
        env -> (
            env.equalsIgnoreCase("test")
                || env.equalsIgnoreCase("local")
        )
    )) {
      // Ensure that we are not querying the full table during tests
      // Set the max timestamp to the 447768th block (0 - 447767)
      query += " AND `timestamp` <= '2015-10-27 12:57:26'";
    }

    // Build query
    String queryCriteria = String.format(
        query,
        blockNumber,
        iso8601Str
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
