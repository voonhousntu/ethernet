package com.vsu001.ethernet.core.service;

import com.google.cloud.bigquery.TableResult;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.vsu001.ethernet.core.model.Log;
import com.vsu001.ethernet.core.util.OrcFileWriter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LogsServiceImpl implements GenericService {

  private static final String TABLE_NAME = "logs";
  private static final String TMP_TABLE_NAME = "tmp_" + TABLE_NAME;
  private static final List<FieldDescriptor> FIELD_DESCRIPTOR_LIST = Log.getDescriptor()
      .getFields();

  /**
   * {@inheritDoc}
   */
  @Override
  public TableResult fetchFromBq(UpdateRequest request) {
    throw new RuntimeException("This method is not implemented");
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

}
