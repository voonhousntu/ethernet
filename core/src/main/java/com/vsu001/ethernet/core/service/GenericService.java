package com.vsu001.ethernet.core.service;

import com.google.cloud.bigquery.TableResult;
import java.util.UUID;

public interface GenericService {

  TableResult fetchFromBq(UpdateRequest request) throws InterruptedException;

  default String getOutputPath() {
    return "/user/hive/warehouse/orc_tmp/";
  }

  default String getFilename() {
    return UUID.randomUUID().toString() + ".orc";
  }

  String getTableName();

  String getTmpTableName();

  String getSchemaStr();

  String getStructStr();

}
