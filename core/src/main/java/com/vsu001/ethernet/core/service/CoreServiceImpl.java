package com.vsu001.ethernet.core.service;

import com.google.cloud.bigquery.TableResult;
import com.google.protobuf.Timestamp;
import com.vsu001.ethernet.core.exception.InvalidRequestException;
import com.vsu001.ethernet.core.model.BlockTimestampMapping;
import com.vsu001.ethernet.core.repository.BlockTsMappingRepository;
import com.vsu001.ethernet.core.repository.GenericHiveRepository;
import com.vsu001.ethernet.core.util.BlockUtil;
import com.vsu001.ethernet.core.util.NonceUtil;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
public class CoreServiceImpl extends CoreServiceGrpc.CoreServiceImplBase {

  private final GenericHiveRepository genericHiveRepository;
  private final BlockTsMappingRepository blockTsMappingRepository;
  private final BlocksServiceImpl blocksService;
  private final BlockTsMappingServiceImpl blockTsMappingService;
  private final ContractsServiceImpl contractsService;
  private final LogsServiceImpl logsService;
  private final TokensServiceImpl tokensService;
  private final TokenTransfersServiceImpl tokenTransfersService;
  private final TracesServiceImpl tracesService;
  private final TransactionsServiceImpl transactionsService;

  public CoreServiceImpl(
      GenericHiveRepository genericHiveRepository,
      BlockTsMappingRepository blockTsMappingRepository,
      BlocksServiceImpl blocksService,
      BlockTsMappingServiceImpl blockTsMappingService,
      ContractsServiceImpl contractsService,
      LogsServiceImpl logsService, TokensServiceImpl tokensService,
      TokenTransfersServiceImpl tokenTransfersService,
      TracesServiceImpl tracesService,
      TransactionsServiceImpl transactionsService
  ) {
    this.genericHiveRepository = genericHiveRepository;
    this.blockTsMappingRepository = blockTsMappingRepository;
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
      String nonce = NonceUtil.generateNonce();

      // Fetch and populate `block_timestamp_mapping` table
      fetchAndPopulateHiveTable(blockTsMappingService, request, nonce);

      // Return response
      // TODO: Build a proper response.
      responseObserver.onNext(buildUpdateResponse());
      responseObserver.onCompleted();
    } catch (InvalidRequestException | InterruptedException | IOException e) {
      e.printStackTrace();
      responseObserver.onError(e.fillInStackTrace());
    }
  }

  @Override
  public void updateBlocks(UpdateRequest request, StreamObserver<UpdateResponse> responseObserver) {
    log.info("Updating `blocks` table");

    try {
      String nonce = NonceUtil.generateNonce();

      // Update `block_timestamp_mapping` table
      request = fetchAndPopulateHiveTable(blockTsMappingService, request, nonce);

      // Fetch and populate `blocks` table
      fetchAndPopulateHiveTable(blocksService, request, nonce);

      // Return response
      // TODO: Build a proper response.
      responseObserver.onNext(buildUpdateResponse());
      responseObserver.onCompleted();
    } catch (InvalidRequestException | InterruptedException | IOException e) {
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
      String nonce = NonceUtil.generateNonce();

      // Update `block_timestamp_mapping` table
      request = fetchAndPopulateHiveTable(blockTsMappingService, request, nonce);

      // Fetch and populate `contracts` table
      fetchAndPopulateHiveTable(contractsService, request, nonce);

      // Return response
      // TODO: Build a proper response.
      responseObserver.onNext(buildUpdateResponse());
      responseObserver.onCompleted();
    } catch (InvalidRequestException | InterruptedException | IOException e) {
      e.printStackTrace();
      responseObserver.onError(e.fillInStackTrace());
    }
  }

  @Override
  public void updateLogs(UpdateRequest request, StreamObserver<UpdateResponse> responseObserver) {
    log.info("Updating `logs` table");

    try {
      String nonce = NonceUtil.generateNonce();

      // Update `block_timestamp_mapping` table
      request = fetchAndPopulateHiveTable(blockTsMappingService, request, nonce);

      // Fetch and populate `logs` table
      fetchAndPopulateHiveTable(logsService, request, nonce);

      // Return response
      // TODO: Build a proper response.
      responseObserver.onNext(buildUpdateResponse());
      responseObserver.onCompleted();
    } catch (InvalidRequestException | InterruptedException | IOException e) {
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
      String nonce = NonceUtil.generateNonce();

      // Update `block_timestamp_mapping` table
      request = fetchAndPopulateHiveTable(blockTsMappingService, request, nonce);

      // Fetch and populate `token_transfers` table
      fetchAndPopulateHiveTable(tokenTransfersService, request, nonce);

      // Import data into Neo4j
      String neo4jDbName = transactionsService.doNeo4jImport(request, nonce);

      // Return response
      // TODO: Build a proper response.
      responseObserver.onNext(buildUpdateResponse());
      responseObserver.onCompleted();
    } catch (InvalidRequestException | InterruptedException | IOException e) {
      e.printStackTrace();
      responseObserver.onError(e.fillInStackTrace());
    }
  }

  @Override
  public void updateTokens(UpdateRequest request, StreamObserver<UpdateResponse> responseObserver) {
    log.info("Updating `tokens` table");

    try {
      String nonce = NonceUtil.generateNonce();

      // Update `block_timestamp_mapping` table
      request = fetchAndPopulateHiveTable(blockTsMappingService, request, nonce);

      // Fetch and populate `tokens` table
      fetchAndPopulateHiveTable(tokensService, request, nonce);

      // Return response
      // TODO: Build a proper response.
      responseObserver.onNext(buildUpdateResponse());
      responseObserver.onCompleted();
    } catch (InvalidRequestException | InterruptedException | IOException e) {
      e.printStackTrace();
      responseObserver.onError(e.fillInStackTrace());
    }
  }

  @Override
  public void updateTraces(UpdateRequest request, StreamObserver<UpdateResponse> responseObserver) {
    log.info("Updating `traces` table");

    try {
      String nonce = NonceUtil.generateNonce();

      // Update `block_timestamp_mapping` table
      request = fetchAndPopulateHiveTable(blockTsMappingService, request, nonce);

      // Fetch and populate `traces` table
      fetchAndPopulateHiveTable(tracesService, request, nonce);

      // Import data into Neo4j
      String neo4jDbName = transactionsService.doNeo4jImport(request, nonce);

      // Return response
      // TODO: Build a proper response.
      responseObserver.onNext(buildUpdateResponse());
      responseObserver.onCompleted();
    } catch (InvalidRequestException | InterruptedException | IOException e) {
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
      String nonce = NonceUtil.generateNonce();

      // Update `block_timestamp_mapping` table
      request = fetchAndPopulateHiveTable(blockTsMappingService, request, nonce);

      // Fetch and populate `transactions` table
      fetchAndPopulateHiveTable(transactionsService, request, nonce);

      // Import data into Neo4j
      String neo4jDbName = transactionsService.doNeo4jImport(request, nonce);

      // Return response
      // TODO: Build a proper response.
      responseObserver.onNext(buildUpdateResponse());
      responseObserver.onCompleted();
    } catch (InvalidRequestException | InterruptedException | IOException e) {
      e.printStackTrace();
      responseObserver.onError(e.fillInStackTrace());
    }

  }

  /**
   * Helper method to call fetch the required data from BigQuery using a degenerate/simplified
   * Strategy pattern implementation.
   * <p>
   * The data is then staged and pushed to the relevant Hive tables.
   *
   * @param genericService The specific service (strategy) to use to query for BigQuery data.
   * @param updateRequest  The block-number range to query for is defined and wrapped in the
   *                       UpdateRequest object. The user defined `start` and `end` object are
   *                       inclusive when translated to the BigQuery legacy SQL query constraints
   *                       equivalent.
   * @param nonce          A random generated string that will only be used once to identify all
   *                       assets associated with the an ETl.
   * @return UpdatedRequest object, where the `endBlockNumber` will be updated if the GenericService
   * is of instance BlockTsMappingService, else the original updateRequest will be passed through
   * and returned.
   * @throws InterruptedException
   * @throws IOException
   * @throws InvalidRequestException
   */
  public UpdateRequest fetchAndPopulateHiveTable(
      GenericService genericService,
      UpdateRequest updateRequest,
      String nonce
  ) throws InterruptedException, IOException, InvalidRequestException {
    if (!BlockUtil.validateRequest(updateRequest)) {
      throw new InvalidRequestException("Invalid [start] and [end] range in request");
    }

    // Fetch results from BigQuery
    TableResult tableResult = genericService.fetchFromBq(updateRequest);

    if (tableResult != null && tableResult.getTotalRows() > 0) {
      log.info("Importing [{}] rows into Hive table: [{}]", tableResult.getTotalRows(),
          genericService.getTableName());

      // Write query results to ORC file with random (UUID) filename in HDFS
      genericHiveRepository.writeTableResults(genericService, tableResult, nonce);

      // Create temporary Hive table
      genericHiveRepository.createTmpTable(genericService, nonce);

      // Insert data from temporary Hive table
      genericHiveRepository.populateHiveTable(genericService, nonce);

      // Remove temporary Hive table + temporary ORC file
      genericHiveRepository.dropTmpTable(genericService, nonce);
    }

    // If the requested block is larger than the most recent block, update the UpdateRequest object
    // This method will already fetch the most recent block available in BigQuery
    if (genericService instanceof BlockTsMappingServiceImpl) {
      BlockTimestampMapping blockTimestampMapping = blockTsMappingRepository.findMostRecent();
      long mostRecentBlockNumber = blockTimestampMapping.getNumber();
      if (updateRequest.getEndBlockNumber() > mostRecentBlockNumber) {
        updateRequest = updateRequest.toBuilder()
            .setEndBlockNumber(mostRecentBlockNumber)
            .build();
      }
    } else {
      // Only need to update cache for other service types
      log.info("Updating cache file for: [{}] ETL", genericService.getTableName());
      genericService.updateCache(updateRequest);
    }

    return updateRequest;
  }

}
