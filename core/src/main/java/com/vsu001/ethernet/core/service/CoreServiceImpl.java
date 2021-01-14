package com.vsu001.ethernet.core.service;

import com.google.cloud.bigquery.TableResult;
import com.google.protobuf.Timestamp;
import com.vsu001.ethernet.core.util.OrcFileWriter;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
@GrpcService
public class CoreServiceImpl extends CoreServiceGrpc.CoreServiceImplBase {

  private final JdbcTemplate jdbcTemplate;
  private final BlockTsMappingServiceImpl blockTsMappingService;

  public CoreServiceImpl(
      JdbcTemplate jdbcTemplate,
      BlockTsMappingServiceImpl blockTsMappingService
  ) {
    this.jdbcTemplate = jdbcTemplate;
    this.blockTsMappingService = blockTsMappingService;
  }

  @Override
  public void updateBlockTsMapping(
      UpdateRequest request,
      StreamObserver<UpdateResponse> responseObserver
  ) {
    log.info("Updating `block_timestamp_mapping`");

    try {
      String outputPath = blockTsMappingService.getOutputPath();
      String filename = blockTsMappingService.getFilename();
      String struct = blockTsMappingService.getStructStr();
      String tableName = blockTsMappingService.getTableName();
      String tmpTableName = blockTsMappingService.getTmpTableName();
      String schema = blockTsMappingService.getSchemaStr();

      // Fetch results from BigQuery
      TableResult tableResult = blockTsMappingService.fetchFromBq(request);

      // Write query results to ORC file with random (UUID) filename in HDFS
      writeTableResults(outputPath, filename, struct, tableResult);

      // Create temporary Hive table
      createTmpTable(schema, tmpTableName, outputPath);

      // Insert data from temporary Hive table
      populateHiveTable(tableName, tmpTableName);

      // Remove temporary Hive table + temporary ORC file
      dropTmpTable(tmpTableName);

      Instant time = Instant.now();
      UpdateResponse reply = UpdateResponse.newBuilder()
          .setNumber(-1L)
          .setTimestamp(
              Timestamp.newBuilder()
                  .setSeconds(time.getEpochSecond())
                  .setNanos(time.getNano())
                  .build()
          )
          .build();

      // Return response
      responseObserver.onNext(reply);
      responseObserver.onCompleted();

    } catch (InterruptedException | IOException e) {
      e.printStackTrace();
      responseObserver.onError(e.fillInStackTrace());
    }
  }

  @Override
  public void updateBlocks(UpdateRequest request, StreamObserver<UpdateResponse> responseObserver) {
    // TODO: Implement this method
  }

  @Override
  public void updateContracts(
      UpdateRequest request,
      StreamObserver<UpdateResponse> responseObserver
  ) {
    // TODO: Implement this method
  }

  @Override
  public void updateLogs(UpdateRequest request, StreamObserver<UpdateResponse> responseObserver) {
    // TODO: Implement this method
  }

  @Override
  public void updateTokenTransfers(
      UpdateRequest request,
      StreamObserver<UpdateResponse> responseObserver
  ) {
    // TODO: Implement this method
  }

  @Override
  public void updateTokens(UpdateRequest request, StreamObserver<UpdateResponse> responseObserver) {
    // TODO: Implement this method
  }

  @Override
  public void updateTraces(UpdateRequest request, StreamObserver<UpdateResponse> responseObserver) {
    // TODO: Implement this method
  }

  @Override
  public void updateTransactions(
      UpdateRequest request,
      StreamObserver<UpdateResponse> responseObserver
  ) {
    // TODO: Implement this method
  }

  private void writeTableResults(
      String outputPath,
      String fileName,
      String struct,
      TableResult tableResult
  ) throws IOException {
    OrcFileWriter.writeTableResults(outputPath + fileName, struct, tableResult);
  }

  private void createTmpTable(String schema, String tmpTableName, String orcParentDir) {
    // Use non-external table so that temporary file can be removed with table drop
    String sql =
        "CREATE TABLE %s (%s)"
            + " STORED AS ORC LOCATION '%s' TBLPROPERTIES ('ORC.COMPRESS' = 'ZLIB')";
    String query = String.format(sql, schema, tmpTableName, orcParentDir);
    jdbcTemplate.execute(query);
  }

  private void populateHiveTable(String desTable, String srcTable) {
    String sql =
        "INSERT INTO ethernet.%s SELECT * FROM %s";
    String query = String.format(sql, desTable, srcTable);
    jdbcTemplate.execute(query);
  }

  private void dropTmpTable(String tmpTableName) {
    String sql = "DROP TABLE %s";
    String query = String.format(sql, tmpTableName);
    jdbcTemplate.execute(query);
  }
}
