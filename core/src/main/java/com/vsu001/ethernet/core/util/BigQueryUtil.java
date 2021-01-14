package com.vsu001.ethernet.core.util;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.FieldList;
import com.google.cloud.bigquery.FieldValue;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.TableResult;
import com.google.protobuf.Descriptors.Descriptor;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BigQueryUtil {

  private static final String PROJECT_ID = "bigquery-public-data";
  private static final String DATASET_NAME = "crypto_ethereum";
  private static final String QUERY = "SELECT %s FROM `%s.%s.%s` WHERE %s;";

  public static TableResult query(
      Descriptor descriptor,
      String tableName
  ) throws InterruptedException {
    return query(descriptor, tableName, "1=1");
  }

  public static TableResult query(
      Descriptor descriptor,
      String tableName,
      String queryCriteria
  ) throws InterruptedException {
    // Initialize client that will be used to send requests.
    // This client only needs to be created once, and can be reused for multiple requests.
    BigQuery bigquery = BigQueryOptions.getDefaultInstance().getService();

    // Get all the columns
    String columnStr = descriptor.getFields().stream()
        .map(s -> "`" + s.getName() + "`")
        .collect(Collectors.joining(","));

    // Build the full query
    String query = String.format(
        QUERY, columnStr, PROJECT_ID, DATASET_NAME, tableName, queryCriteria
    );
    QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query).build();

    log.info("Query: {}", query);

    // Query for the results
    return bigquery.query(queryConfig);
  }

  public static List<Map<String, Object>> formatTableResult(TableResult tableResult) {
    List<Map<String, Object>> data = new LinkedList<>();
    FieldList fieldList = tableResult.getSchema().getFields();

    // Loop through every row
    for (FieldValueList fieldValueList : tableResult.iterateAll()) {

      // Loop through every field
      Map<String, Object> stringObjectMap = new HashMap<>();
      for (int i = 0; i < fieldValueList.size(); i++) {
        Field field = fieldList.get(i);
        stringObjectMap.put(
            field.getName(),
            getJavaValue(field, fieldValueList.get(i))
        );
      }

      data.add(stringObjectMap);
    }
    return data;
  }

  public static Object getJavaValue(Field field, FieldValue fieldValue) {
    switch (field.getType().name()) {
      case "STRING":
        return fieldValue.getStringValue();
      case "BYTES":
        return fieldValue.getBytesValue();
      case "INTEGER":
        return fieldValue.getLongValue();
      case "FLOAT":
        return fieldValue.getDoubleValue();
      case "BOOLEAN":
        return fieldValue.getBooleanValue();
      case "TIMESTAMP":
        long timestamp = fieldValue.getTimestampValue() / 1000;
        return new java.sql.Timestamp(timestamp);
      default:
        return fieldValue.getValue();
    }
  }

  public static String getOrcType(Field field) {
    String fieldTypeName = field.getType().name();
    switch (fieldTypeName) {
      case "STRING":
        return "string";
      case "BYTES":
        throw new RuntimeException(fieldTypeName + "type not supported");
      case "INTEGER":
        return "bigint";
      case "FLOAT":
        return "double";
      case "BOOLEAN":
        return "boolean";
      case "TIMESTAMP":
        return "timestamp";
      default:
        throw new RuntimeException(fieldTypeName + "type not supported");
    }
  }

}