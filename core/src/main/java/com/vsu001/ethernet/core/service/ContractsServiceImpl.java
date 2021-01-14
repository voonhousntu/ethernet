package com.vsu001.ethernet.core.service;

import com.google.cloud.bigquery.TableResult;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.vsu001.ethernet.core.model.Contract;
import com.vsu001.ethernet.core.util.OrcFileWriter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ContractsServiceImpl implements GenericService {

  private static final String TABLE_NAME = "contracts";
  private static final String TMP_TABLE_NAME = "tmp_" + TABLE_NAME;
  private static final List<FieldDescriptor> FIELD_DESCRIPTOR_LIST = Contract.getDescriptor()
      .getFields();

  @Override
  public TableResult fetchFromBq(UpdateRequest request) throws InterruptedException {
    // TODO: Implement this method
    return null;
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
