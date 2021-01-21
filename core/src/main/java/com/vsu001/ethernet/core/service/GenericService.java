package com.vsu001.ethernet.core.service;

import com.google.cloud.bigquery.TableResult;
import java.util.UUID;

public interface GenericService {

  /**
   * Fetch the row(s) of interest from BigQuery.
   * <p>
   * The constraints of query, i.e. what to query for are defined in the UpdateRequest object.
   *
   * @param request The block-number range to query for is defined and wrapped in the UpdateRequest
   *                object. The user defined `start` and `end` object are inclusive when translated
   *                to the BigQuery legacy SQL query constraints equivalent.
   * @return The rows with block-number within the `start` and `end` number range (inclusive)
   * wrapped in a TableResult object.
   * @throws InterruptedException
   */
  TableResult fetchFromBq(UpdateRequest request) throws InterruptedException;

  /**
   * Obtain the HDFS output path where the staging ORC file will be written to.
   *
   * @return HDFS output path where the staging ORC file will be written to.
   */
  default String getOutputPath() {
    return "/user/hive/warehouse/orc_tmp/";
  }

  /**
   * Create a random (uuid) string for the staging ORC file that holds the incremental data before
   * it is ingested into the main Hive table.
   *
   * @return Random UUID string.
   */
  default String getFilename() {
    return UUID.randomUUID().toString() + ".orc";
  }

  /**
   * Obtain the actual Hive table name that will be used to persist a protobuf defined object of
   * interest.
   *
   * @return Hive table name.
   */
  String getTableName();

  /**
   * Obtain the temporary Hive table name that will be used to hold the incremental data before
   * being ingested into the main Hive table.
   *
   * @return Temporary Hive table name
   */
  String getTmpTableName();

  /**
   * Obtain the Hive SQL schema string of Hive table which will be used to persist the protobuf
   * defined object of interest.
   *
   * @return Schema string of Hive SQL table which will be used to persist the protobuf defined
   * object of interest.
   */
  String getSchemaStr();

  /**
   * Obtain the struct string required to define the "schema" of an ORC file, which is used to
   * persist the protobuf defined object of interest.
   *
   * @return Struct string of ORC file that is used to persist the protobuf defined object of
   * interest.
   */
  String getStructStr();

}
