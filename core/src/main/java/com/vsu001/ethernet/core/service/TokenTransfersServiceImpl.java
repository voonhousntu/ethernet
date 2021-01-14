package com.vsu001.ethernet.core.service;

import com.google.cloud.bigquery.TableResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TokenTransfersServiceImpl implements GenericService {

  private static final String TABLE_NAME = "token_transfers";
  private static final String TMP_TABLE_NAME = "tmp_" + TABLE_NAME;
  private static final String STRUCT = "struct<>";
  private static final String SCHEMA = "";

  @Override
  public TableResult fetchFromBq(UpdateRequest request) throws InterruptedException {
    return null;
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
