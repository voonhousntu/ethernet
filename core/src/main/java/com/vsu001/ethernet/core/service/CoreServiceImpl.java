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
  private final BlocksServiceImpl blocksService;
  private final BlockTsMappingServiceImpl blockTsMappingService;
  private final ContractsServiceImpl contractsService;
  private final LogsServiceImpl logsService;
  private final TokensServiceImpl tokensService;
  private final TokenTransfersServiceImpl tokenTransfersService;
  private final TracesServiceImpl tracesService;
  private final TransactionsServiceImpl transactionsService;

  public CoreServiceImpl(
      JdbcTemplate jdbcTemplate,
      BlocksServiceImpl blocksService,
      BlockTsMappingServiceImpl blockTsMappingService,
      ContractsServiceImpl contractsService,
      LogsServiceImpl logsService,
      TokensServiceImpl tokensService,
      TokenTransfersServiceImpl tokenTransfersService,
      TracesServiceImpl tracesService,
      TransactionsServiceImpl transactionsService
  ) {
    this.jdbcTemplate = jdbcTemplate;
    this.blocksService = blocksService;
    this.blockTsMappingService = blockTsMappingService;
    this.contractsService = contractsService;
    this.logsService = logsService;
    this.tokensService = tokensService;
    this.tokenTransfersService = tokenTransfersService;
    this.tracesService = tracesService;
    this.transactionsService = transactionsService;
  }

  private static UpdateResponse buildUpdateResponse() {
    Instant time = Instant.now();
    return UpdateResponse.newBuilder()
        .setNumber(-1L)
        .setTimestamp(
            Timestamp.newBuilder()
                .setSeconds(time.getEpochSecond())
                .setNanos(time.getNano())
                .build()
        )
        .build();
  }

  @Override
  public void updateBlockTsMapping(
      UpdateRequest request,
      StreamObserver<UpdateResponse> responseObserver
  ) {
    log.info("Updating `block_timestamp_mapping` table");

    try {
      // Fetch and populate `block_timestamp_mapping` table
      fetchAndPopulateHiveTable(blockTsMappingService, request);

      // Return response
      responseObserver.onNext(buildUpdateResponse());
      responseObserver.onCompleted();
    } catch (InterruptedException | IOException e) {
      e.printStackTrace();
      responseObserver.onError(e.fillInStackTrace());
    }
  }

  @Override
  public void updateBlocks(UpdateRequest request, StreamObserver<UpdateResponse> responseObserver) {
    log.info("Updating `blocks` table");

    try {
      // Fetch and populate `blocks` table
      fetchAndPopulateHiveTable(blocksService, request);

      // Return response
      responseObserver.onNext(buildUpdateResponse());
      responseObserver.onCompleted();
    } catch (InterruptedException | IOException e) {
      e.printStackTrace();
      responseObserver.onError(e.fillInStackTrace());
    }
  }

  @Override
  public void updateContracts(
      UpdateRequest request,
      StreamObserver<UpdateResponse> responseObserver
  ) {
    log.info("Updating `contracts` table");

    try {
      // Fetch and populate `contracts` table
      fetchAndPopulateHiveTable(contractsService, request);

      // Return response
      responseObserver.onNext(buildUpdateResponse());
      responseObserver.onCompleted();
    } catch (InterruptedException | IOException e) {
      e.printStackTrace();
      responseObserver.onError(e.fillInStackTrace());
    }
  }

  @Override
  public void updateLogs(UpdateRequest request, StreamObserver<UpdateResponse> responseObserver) {
    log.info("Updating `logs` table");

    try {
      // Fetch and populate `logs` table
      fetchAndPopulateHiveTable(logsService, request);

      // Return response
      responseObserver.onNext(buildUpdateResponse());
      responseObserver.onCompleted();
    } catch (InterruptedException | IOException e) {
      e.printStackTrace();
      responseObserver.onError(e.fillInStackTrace());
    }
  }

  @Override
  public void updateTokenTransfers(
      UpdateRequest request,
      StreamObserver<UpdateResponse> responseObserver
  ) {
    log.info("Updating `token_transfers` table");

    try {
      // Fetch and populate `token_transfers` table
      fetchAndPopulateHiveTable(tokenTransfersService, request);

      // Return response
      responseObserver.onNext(buildUpdateResponse());
      responseObserver.onCompleted();
    } catch (InterruptedException | IOException e) {
      e.printStackTrace();
      responseObserver.onError(e.fillInStackTrace());
    }
  }

  @Override
  public void updateTokens(UpdateRequest request, StreamObserver<UpdateResponse> responseObserver) {
    log.info("Updating `tokens` table");

    try {
      // Fetch and populate `tokens` table
      fetchAndPopulateHiveTable(tokensService, request);

      // Return response
      responseObserver.onNext(buildUpdateResponse());
      responseObserver.onCompleted();
    } catch (InterruptedException | IOException e) {
      e.printStackTrace();
      responseObserver.onError(e.fillInStackTrace());
    }
  }

  @Override
  public void updateTraces(UpdateRequest request, StreamObserver<UpdateResponse> responseObserver) {
    log.info("Updating `traces` table");

    try {
      // Fetch and populate `traces` table
      fetchAndPopulateHiveTable(tracesService, request);

      // Return response
      responseObserver.onNext(buildUpdateResponse());
      responseObserver.onCompleted();
    } catch (InterruptedException | IOException e) {
      e.printStackTrace();
      responseObserver.onError(e.fillInStackTrace());
    }
  }

  @Override
  public void updateTransactions(
      UpdateRequest request,
      StreamObserver<UpdateResponse> responseObserver
  ) {
    log.info("Updating `transactions` table");

    try {
      // Fetch and populate `transactions` table
      fetchAndPopulateHiveTable(transactionsService, request);

      // Return response
      responseObserver.onNext(buildUpdateResponse());
      responseObserver.onCompleted();
    } catch (InterruptedException | IOException e) {
      e.printStackTrace();
      responseObserver.onError(e.fillInStackTrace());
    }
  }

  private void writeTableResults(
      GenericService genericService,
      TableResult tableResult
  ) throws IOException {
    OrcFileWriter.writeTableResults(
        genericService.getOutputPath() + genericService.getFilename(),
        genericService.getStructStr(),
        tableResult
    );
  }

  private void createTmpTable(GenericService genericService) {
    // Use non-external table so that temporary file can be removed with table drop
    String sql =
        "CREATE TABLE %s (%s)"
            + " STORED AS ORC LOCATION '%s' TBLPROPERTIES ('ORC.COMPRESS' = 'ZLIB')";
    String query = String.format(
        sql,
        genericService.getSchemaStr(),
        genericService.getTmpTableName(),
        genericService.getOutputPath()
    );
    jdbcTemplate.execute(query);
  }

  private void populateHiveTable(GenericService genericService) {
    String sql =
        "INSERT INTO ethernet.%s SELECT * FROM %s";
    String query = String.format(
        sql,
        genericService.getTableName(),
        genericService.getTmpTableName()
    );
    jdbcTemplate.execute(query);
  }

  private void dropTmpTable(GenericService genericService) {
    String sql = "DROP TABLE %s";
    String query = String.format(sql, genericService.getTableName());
    jdbcTemplate.execute(query);
  }

  private void fetchAndPopulateHiveTable(
      GenericService genericService,
      UpdateRequest updateRequest
  ) throws InterruptedException, IOException {
    // Fetch results from BigQuery
    TableResult tableResult = genericService.fetchFromBq(updateRequest);

    // Write query results to ORC file with random (UUID) filename in HDFS
    writeTableResults(genericService, tableResult);

    // Create temporary Hive table
    createTmpTable(genericService);

    // Insert data from temporary Hive table
    populateHiveTable(genericService);

    // Remove temporary Hive table + temporary ORC file
    dropTmpTable(genericService);
  }

}
