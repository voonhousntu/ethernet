package com.vsu001.ethernet.core.service;

import com.google.cloud.bigquery.TableResult;
import com.google.protobuf.Timestamp;
import com.vsu001.ethernet.core.model.BlockTimestampMapping;
import com.vsu001.ethernet.core.util.BigQueryUtil;
import com.vsu001.ethernet.core.util.OrcFileWriter;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.time.Instant;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
@GrpcService
public class CoreServiceImpl extends CoreServiceGrpc.CoreServiceImplBase {

  private static final String TMP_BLOCK_TS_MAPPING_TABLE = "block_timestamp_mapping_tmp";
  private static final String BLOCK_TS_MAPPING_TABLE = "block_timestamp_mapping";
  private final JdbcTemplate jdbcTemplate;

  public CoreServiceImpl(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void updateBlockTsMapping(
      BlockTsMappingUpdateRequest request,
      StreamObserver<BlockTsMappingUpdateResponse> responseObserver) {

    try {
      log.info("Updating `block_timestamp_mapping`");
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

      // Write results to an ORC file with random (UUID) filename
      String outputPath = "/user/hive/warehouse/orc_tmp/";
      String fileName = UUID.randomUUID().toString() + ".orc";
      String struct = "struct<number:bigint,timestamp:timestamp>";

      OrcFileWriter.writeTableResults(outputPath + fileName, struct, tableResult);

      Instant time = Instant.now();
      BlockTsMappingUpdateResponse reply = BlockTsMappingUpdateResponse.newBuilder()
          .setNumber(-1L)
          .setTimestamp(
              Timestamp.newBuilder()
                  .setSeconds(time.getEpochSecond())
                  .setNanos(time.getNano())
                  .build()
          )
          .build();

      // Create temporary Hive table
      createTmpTable(outputPath);

      // Insert data from temporary Hive table
      populateHiveTable();

      // Remove temporary Hive table
      // Will remove temporary file too
      dropTmpTable();

      // Return request response
      responseObserver.onNext(reply);
      responseObserver.onCompleted();
    } catch (InterruptedException | IOException e) {
      responseObserver.onError(e.getCause());
      e.printStackTrace();
    }
  }

  private void createTmpTable(String orcFilePath) {
    // Use non-external table so that temporary file can be removed with table drop
    String sql =
        "CREATE TABLE %s (`number` bigint, `timestamp` timestamp)"
            + " STORED AS ORC LOCATION '%s' TBLPROPERTIES ('ORC.COMPRESS' = 'ZLIB')";
    String query = String.format(sql, TMP_BLOCK_TS_MAPPING_TABLE, orcFilePath);
    jdbcTemplate.execute(query);
  }

  private void populateHiveTable() {
    String sql =
        "INSERT INTO ethernet.%s "
            + "SELECT * FROM %s";
    String query = String.format(sql, BLOCK_TS_MAPPING_TABLE, TMP_BLOCK_TS_MAPPING_TABLE);
    jdbcTemplate.execute(query);
  }

  private void dropTmpTable() {
    String sql = "DROP TABLE %s";
    String query = String.format(sql, TMP_BLOCK_TS_MAPPING_TABLE);
    jdbcTemplate.execute(query);
  }

}
