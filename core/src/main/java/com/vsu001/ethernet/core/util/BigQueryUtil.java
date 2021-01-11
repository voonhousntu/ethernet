package com.vsu001.ethernet.core.util;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.TableResult;


public class BigQueryUtil {

  private static final String PROJECT_ID = "bigquery-public-data";
  private static final String DATASET_NAME = "crypto_ethereum";
  private static final String QUERY = "SELECT %s FROM `%s.%s.%s` WHERE timestamp BETWEEN %s and %s";

  public static TableResult query(String query) throws InterruptedException {
    // Initialize client that will be used to send requests. This client only needs to be created
    // once, and can be reused for multiple requests.
    BigQuery bigquery = BigQueryOptions.getDefaultInstance().getService();

    QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query).build();
    return bigquery.query(queryConfig);

  }

}