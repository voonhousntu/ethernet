package com.vsu001.ethernet.core.service;

import com.google.cloud.bigquery.TableResult;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.vsu001.ethernet.core.model.Block;
import com.vsu001.ethernet.core.model.BlockTimestampMapping;
import com.vsu001.ethernet.core.repository.BlockRepository;
import com.vsu001.ethernet.core.repository.BlockTsMappingRepository;
import com.vsu001.ethernet.core.util.BigQueryUtil;
import com.vsu001.ethernet.core.util.BlockUtil;
import com.vsu001.ethernet.core.util.OrcFileWriter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BlocksServiceImpl implements GenericService {

  private static final String TABLE_NAME = "blocks";
  private static final String TMP_TABLE_NAME = "tmp_" + TABLE_NAME;
  private static final List<FieldDescriptor> FIELD_DESCRIPTOR_LIST = Block.getDescriptor()
      .getFields();
  private final BlockRepository blockRepository;
  private final BlockTsMappingRepository blockTsMappingRepository;

  public BlocksServiceImpl(
      BlockRepository blockRepository,
      BlockTsMappingRepository blockTsMappingRepository
  ) {
    this.blockRepository = blockRepository;
    this.blockTsMappingRepository = blockTsMappingRepository;
  }

  @Override
  public TableResult fetchFromBq(UpdateRequest request) throws InterruptedException {
    // Find blocks that are already in Hive table
    List<Long> blockNumbers = blockRepository.findByNumberRange(
        request.getStartBlockNumber(),
        request.getEndBlockNumber()
    );

    // Add start and end delimiters
    blockNumbers.add(0, request.getStartBlockNumber() - 1);
    blockNumbers.add(request.getEndBlockNumber() + 1);

    // Find contiguous block numbers that are missing from the Hive table
    List<List<Long>> lLists = BlockUtil.findMissingContRange(blockNumbers);

    StringBuilder timestampSB = new StringBuilder("1=1 ");
    for (List<Long> lList : lLists) {
      if (lList.get(0).equals(lList.get(1))) {
        // Cost to run query will be the same as querying for a day's worth of data
        BlockTimestampMapping blockTspMapping = blockTsMappingRepository.findByNumber(lList.get(0));
        timestampSB.append("AND `timestamp` = ");
        timestampSB.append(
            String.format("'%s", BlockUtil.protoTsToISO(blockTspMapping.getTimestamp()))
        );
      } else {
        BlockTimestampMapping startBTM = blockTsMappingRepository.findByNumber(lList.get(0));
        BlockTimestampMapping endBTM = blockTsMappingRepository.findByNumber(lList.get(1));
        timestampSB.append("AND `timestamp` >= ");
        timestampSB.append(
            String.format("'%s", BlockUtil.protoTsToISO(startBTM.getTimestamp()))
        );
        timestampSB.append("AND `timestamp` <= ");
        timestampSB.append(
            String.format("'%s", BlockUtil.protoTsToISO(endBTM.getTimestamp()))
        );
      }
    }

    String queryCriteria = String.format(
        timestampSB.toString() + " AND number NOT IN (%s)",
        blockNumbers.stream().map(String::valueOf).collect(Collectors.joining(","))
    );

    // Fetch results from BigQuery
    TableResult tableResult = BigQueryUtil.query(
        Block.getDescriptor(),
        "blocks",
        queryCriteria
    );

    log.info("Rows fetched: [{}]", tableResult.getTotalRows());

    return tableResult;
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
    return FIELD_DESCRIPTOR_LIST.stream()
        .map(s -> String.format("`%s` %s", s.getName(), OrcFileWriter.protoToOrcType(s)))
        .collect(Collectors.joining(","));
  }

  @Override
  public String getStructStr() {
    return OrcFileWriter.protoToOrcStructStr(FIELD_DESCRIPTOR_LIST);
  }

}
