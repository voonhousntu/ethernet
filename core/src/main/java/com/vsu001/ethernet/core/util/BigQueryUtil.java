package com.vsu001.ethernet.core.util;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.TableResult;
import com.google.protobuf.Descriptors.Descriptor;
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

}