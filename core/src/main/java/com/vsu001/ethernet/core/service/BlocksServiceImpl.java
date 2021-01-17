package com.vsu001.ethernet.core.service;

import com.google.cloud.bigquery.TableResult;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.vsu001.ethernet.core.model.Block;
import com.vsu001.ethernet.core.model.BlockTimestampMapping;
import com.vsu001.ethernet.core.repository.BlockRepository;
import com.vsu001.ethernet.core.repository.BlockTsMappingRepository;
import com.vsu001.ethernet.core.util.BigQueryUtil;
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

    List<BlockTimestampMapping> blockTsMappings = blockTsMappingRepository.findByStartEndNumber(
        request.getStartBlockNumber(),
        request.getEndBlockNumber()
    );

    // TODO: Implement a way to handle non-contiguous data
    String queryCriteria = String.format(
        "`timestamp` >= '%s' AND `timestamp` < '%s' "
            + "AND number >= '%s' AND number < '%s' "
            + "AND number NOT IN (%s)",
        "", "",
        blockTsMappings.get(0).getNumber(), blockTsMappings.get(1).getNumber(),
        blockNumbers.stream().map(String::valueOf).collect(Collectors.joining(","))
    );

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
