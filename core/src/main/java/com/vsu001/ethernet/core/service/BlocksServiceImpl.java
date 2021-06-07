package com.vsu001.ethernet.core.service;

import com.google.cloud.bigquery.TableResult;
import com.google.common.base.Stopwatch;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.vsu001.ethernet.core.config.EthernetConfig;
import com.vsu001.ethernet.core.model.Block;
import com.vsu001.ethernet.core.model.BlockTimestampMapping;
import com.vsu001.ethernet.core.model.Transaction;
import com.vsu001.ethernet.core.repository.BlockRepository;
import com.vsu001.ethernet.core.repository.BlockTsMappingRepository;
import com.vsu001.ethernet.core.util.BigQueryUtil;
import com.vsu001.ethernet.core.util.BlockUtil;
import com.vsu001.ethernet.core.util.OrcFileWriter;
import com.vsu001.ethernet.core.util.interval.Interval;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BlocksServiceImpl implements GenericService {

  private static final String CACHE_FILE_NAME = BlockRepository.TABLE_NAME + ".cache";
  private static final String TMP_TABLE_NAME = "tmp_" + BlockRepository.TABLE_NAME;
  private static final List<FieldDescriptor> FIELD_DESCRIPTOR_LIST = Block.getDescriptor()
      .getFields();

  private final BlockTsMappingRepository blockTsMappingRepository;
  private final EthernetConfig ethernetConfig;

  public BlocksServiceImpl(
      BlockTsMappingRepository blockTsMappingRepository,
      EthernetConfig ethernetConfig) {
    this.blockTsMappingRepository = blockTsMappingRepository;
    this.ethernetConfig = ethernetConfig;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TableResult fetchFromBq(UpdateRequest request)
      throws InterruptedException, FileNotFoundException {
    // Get the current method name
    String methodName = new Throwable().getStackTrace()[0].getMethodName();

    // To time how long function takes to run
    Stopwatch stopwatch = Stopwatch.createStarted();

    // Find contiguous block numbers that are missing from the Hive table using cache file
    // Firstly, get all the intervals that have already been fetched
    Set<Interval<Long>> cachedIntervals = BlockUtil.readFromCache(
        String.format("%s/%s", ethernetConfig.getEthernetWorkDir(), CACHE_FILE_NAME),
        request.getStartBlockNumber(),
        request.getEndBlockNumber()
    );

    // Lastly, find contiguous block numbers that are missing from the Hive table
    List<List<Long>> lLists = BlockUtil.findMissingContRange(
        cachedIntervals,
        request.getStartBlockNumber(),
        request.getEndBlockNumber()
    );

    StringBuilder timestampSB = new StringBuilder("1=1 ");
    if (lLists.size() > 0) {

      for (int i = 0; i < lLists.size(); i++) {
        List<Long> lList = lLists.get(i);

        // Range is only bounded by 1 integer
        if (lList.get(0).equals(lList.get(1))) {
          // Cost to run query will be the same as querying for a day's worth of data
          BlockTimestampMapping blockTspMapping = blockTsMappingRepository
              .findByNumber(lList.get(0));

          if (i == 0) {
            timestampSB.append("AND ");
          } else {
            timestampSB.append("OR ");
          }

          String timestamp = BlockUtil.protoTsToISO(blockTspMapping.getTimestamp());
          timestampSB.append("`timestamp` = ").append(String.format("'%s' ", timestamp));
        } else {
          // Range is only bounded by 2 integers
          BlockTimestampMapping startBTM = blockTsMappingRepository.findByNumber(lList.get(0));
          BlockTimestampMapping endBTM = blockTsMappingRepository.findByNumber(lList.get(1));

          String startTs = BlockUtil.protoTsToISO(startBTM.getTimestamp());
          String endTs = BlockUtil.protoTsToISO(endBTM.getTimestamp());

          if (i == 0) {
            timestampSB.append("AND ");
          } else {
            timestampSB.append("OR ");
          }

          timestampSB.append("`timestamp` BETWEEN ");
          timestampSB.append(String.format("'%s' ", startTs));
          timestampSB.append(String.format("AND '%s' ", endTs));
        }
      }

      // Ignore the ranges that have already been fetched
      StringBuilder rangeToIgnore = new StringBuilder();
      for (Interval<Long> cacheInterval : cachedIntervals) {
        String range = String
            .format(" AND `number` NOT BETWEEN %s AND %s",
                cacheInterval.getStart(),
                cacheInterval.getEnd()
            );
        rangeToIgnore.append(range);
      }

      String queryCriteria = timestampSB.toString() + rangeToIgnore.toString();

      // Fetch results from BigQuery
      TableResult tableResult = BigQueryUtil.query(
          Transaction.getDescriptor(),
          BlockRepository.TABLE_NAME,
          queryCriteria
      );

      stopwatch.stop(); // Optional
      log.info("[{}] -> Time elapsed: [{}] ms",
          methodName,
          stopwatch.elapsed(TimeUnit.MILLISECONDS)
      );

      long rowsFetched = 0;
      if (tableResult != null) {
        rowsFetched = tableResult.getTotalRows();
      }
      log.info("Rows fetched: [{}]", rowsFetched);

      return tableResult;
    }

    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getTableName() {
    return BlockRepository.TABLE_NAME;
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
  public List<FieldDescriptor> getFieldDescriptors() {
    return FIELD_DESCRIPTOR_LIST;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getSchemaStr() {
    return getFieldDescriptors().stream()
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
  public String doNeo4jImport(UpdateRequest request, String nonce) {
    throw new RuntimeException("This method is not implemented");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String doNeo4jImport(String databaseName, UpdateRequest request, String nonce) {
    throw new RuntimeException("This method is not implemented");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String generateNeo4jDbName(long blockStartNo, long blockEndNo) {
    throw new RuntimeException("This method is not implemented");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateCache(UpdateRequest request) throws IOException {
    // Find contiguous block numbers that are missing from the Hive table using cache file
    // Firstly, get all the intervals that have already been fetched
    Set<Interval<Long>> cachedIntervals = BlockUtil.readFromCache(
        String.format("%s/%s", ethernetConfig.getEthernetWorkDir(), CACHE_FILE_NAME),
        request.getStartBlockNumber(),
        request.getEndBlockNumber()
    );

    // Lastly, find contiguous block numbers that are missing from the Hive table
    List<List<Long>> lLists = BlockUtil.findMissingContRange(
        cachedIntervals,
        request.getStartBlockNumber(),
        request.getEndBlockNumber()
    );

    BlockUtil.updateCache(
        String.format("%s/%s", ethernetConfig.getEthernetWorkDir(), CACHE_FILE_NAME),
        lLists
    );
  }

}
