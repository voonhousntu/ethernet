package com.vsu001.ethernet.core.service;

import com.google.cloud.bigquery.TableResult;
import com.vsu001.ethernet.core.model.BlockTimestampMapping;
import com.vsu001.ethernet.core.util.BigQueryUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BlockTsMappingServiceImpl implements GenericService {

  private static final String TABLE_NAME = "block_timestamp_mapping";
  private static final String TMP_TABLE_NAME = "tmp_" + TABLE_NAME;
  private static final String STRUCT = "struct<number:bigint,timestamp:timestamp>";
  private static final String SCHEMA = "`number` bigint, `timestamp` timestamp";

  @Override
  public TableResult fetchFromBq(UpdateRequest request) throws InterruptedException {

    // For debugging
    TableResult tableResult = BigQueryUtil.query(
        BlockTimestampMapping.getDescriptor(),
        "blocks",
        "timestamp < '2015-07-30 15:46:52'"
    );

    // TODO: Fetch the largest timestamp from the table
//      TableResult tableResult = BigQueryUtil.query(
//          BlockTimestampMapping.getDescriptor(),
//          "blocks".
//              "timestamp > ''"
//      );

    long rowsFetched = tableResult.getTotalRows();
    log.info("Rows fetched: [{}]", rowsFetched);

    return tableResult;
  }

  @Override
  public String getStructStr() {
    return STRUCT;
  }

  @Override
  public String getTableName() {
    return TABLE_NAME;
  }

  @Override
  public String getTmpTableName() {
    return TMP_TABLE_NAME;
  }

  @Override
  public String getSchemaStr() {
    return SCHEMA;
  }

}
