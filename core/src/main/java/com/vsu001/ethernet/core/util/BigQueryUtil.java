package com.vsu001.ethernet.core.util;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.FieldValue;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.TableResult;
import com.google.protobuf.Descriptors.Descriptor;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BigQueryUtil {

//  private static final String CREDENTIALS_PATH = "yolo-mdp-b9a50242cf8a.json";
  private static final String PROJECT_ID = "bigquery-public-data";
  private static final String DATASET_NAME = "crypto_ethereum";
  private static final String QUERY = "SELECT %s FROM `%s.%s.%s` WHERE %s;";

  public static TableResult query(
      Descriptor descriptor,
      String tableName
  ) throws InterruptedException {
    return query(descriptor, tableName, "1=1");
  }

  /**
   * Helper function to submit a BigQuery job to execute a query on a table.
   *
   * @param descriptor    The object's field specification defined/wrapped in a protobuf's
   *                      <code>Descriptor</code>.
   * @param tableName     Name of the BigQuery table of interest in the `crypto_ethereum`</code>`
   *                      dataset.
   * @param queryCriteria The query constraints to use in the `WHERE` block of the query.
   * @return The <code>TableResult</code> of the BigQuery job.
   * @throws InterruptedException
   */
  public static TableResult query(
      Descriptor descriptor,
      String tableName,
      String queryCriteria
  ) throws InterruptedException {
    // Load credentials from JSON key file. If you can't set the GOOGLE_APPLICATION_CREDENTIALS
    // environment variable, you can explicitly load the credentials file to construct the
    // credentials.
//    GoogleCredentials credentials;
//    try {
//      File file = new File(
//          BigQueryUtil.class.getClassLoader().getResource(CREDENTIALS_PATH).toURI());
//      FileInputStream serviceAccountStream = new FileInputStream(file);
//      credentials = ServiceAccountCredentials.fromStream(serviceAccountStream);
//    } catch (IOException | URISyntaxException e) {
//      e.printStackTrace();
//      log.error("Unable to load credential files");
//      return null;
//    }

    // Initialize client that will be used to send requests.
    // This client only needs to be created once, and can be reused for multiple requests.
//    BigQuery bigquery = BigQueryOptions.newBuilder()
//        .setCredentials(credentials)
//        .build().getService();

    BigQuery bigquery = BigQueryOptions.getDefaultInstance().getService();

    // Get all the columns
    String columnStr = descriptor.getFields().stream()
        .map(s -> columnToSqlString(tableName, s.getName()))
        .collect(Collectors.joining(","));

    // Build the full query
    String query = String.format(
        QUERY, columnStr, PROJECT_ID, DATASET_NAME, tableName, queryCriteria
    );
    QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query).build();

    log.info("Query: {}", query);

    // Query for the results
//    return bigquery.query(queryConfig);
    return null;
  }

  private static String columnToSqlString(String tableName, String fieldDescriptorName) {
    switch (tableName + "_" + fieldDescriptorName) {
      case "transactions_to_address":
        return "COALESCE(to_address, receipt_contract_address) AS `to_address`";
      case "traces_to_address":
      case "token_transfers_to_address":
        return "COALESCE(to_address, '0x0000000000000000000000000000000000000000') AS `to_address`";
      case "traces_from_address":
      case "token_transfers_from_address":
        return "COALESCE(from_address, '0x0000000000000000000000000000000000000000') AS `from_address`";
      default:
        return "`" + fieldDescriptorName + "`";
    }
  }

  /**
   * Obtain the native Java value of a <code>FieldValue</code> object by providing the
   * <code>Field</code> object definition.
   *
   * @param field      <code>Field</code> object definition containing the FieldValue
   *                   specifications.
   * @param fieldValue <code>FieldValue</code> object containing the value that should be
   *                   "converted" to Java's native type.
   * @return Native Java value of the <code>FieldValue</code> object.
   */
  public static Object getJavaValue(Field field, FieldValue fieldValue) {
    switch (field.getType().name()) {
      case "STRING":
        if (field.getMode() != null && field.getMode().name().equals("REPEATED")) {
          return fieldValue.getRepeatedValue().stream()
              .map(e -> e != null ? e.getStringValue() : "")
              .collect(Collectors.joining(","));
        } else {
          return fieldValue.getValue() != null ? fieldValue.getStringValue() : null;
        }
      case "BYTES":
        return fieldValue.getBytesValue();
      case "INTEGER":
        return fieldValue.getValue() != null ? fieldValue.getLongValue() : -1;
      case "NUMERIC":
        return fieldValue.getValue() != null ? fieldValue.getStringValue() : null;
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

  /**
   * Obtain the ORC type string of a <code>Field</code> object specification.
   * <p>
   * This function is a degenerate HashMap containing the mappings between the BigQuery
   * <code>Field</code> object specification types and the ORC types.
   * <p>
   * Only types that are required for the Ethernet project is implemented in this method.
   *
   * @param field <code>Field</code> object definition containing the <code>FieldValue</code>
   *              specifications.
   * @return The equivalent ORC type string that is of the equivalent type as the <code>Field</code>
   * object specification.
   */
  public static String getOrcType(Field field) {
    String fieldTypeName = field.getType().name();
    switch (fieldTypeName) {
      case "STRING":
        return "string";
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