package com.vsu001.ethernet.core.repository;

import com.google.cloud.bigquery.TableResult;
import com.vsu001.ethernet.core.service.GenericService;
import com.vsu001.ethernet.core.util.OrcFileWriter;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class GenericHiveRepository {

  private final JdbcTemplate jdbcTemplate;

  @Value("${spring.datasource.hivedb.schema}")
  private String schema;

  @Value("${hadoop.fs.default-fs}")
  private String defaultFs;

  @Value("${hadoop.dfs.datanose-use-hostname}")
  private boolean datanodeUseHostname;

  @Value("${hadoop.dfs.client-use-hostname}")
  private boolean clientUseHostname;

  public GenericHiveRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * Write the <code>TableResult</code> obtained from a BigQuery job into HDFS directory as an ORC
   * file.
   *
   * @param genericService The interface containing the implementation of a
   *                       <code>GenericService</code> to facilitate the writing of the
   *                       <code>TableResult</code> into a HDFS directory.
   * @param tableResult    The <code>TableResult</code> obtained from a BigQuery job.
   * @throws IOException If an IOException is encountered when writing to the HDFS directory.
   */
  public void writeTableResults(
      GenericService genericService,
      TableResult tableResult
  ) throws IOException {
    OrcFileWriter.writeTableResults(
        genericService.getOutputPath() + genericService.getFilename(),
        genericService.getStructStr(),
        tableResult,
        defaultFs,
        datanodeUseHostname,
        clientUseHostname
    );
  }

  /**
   * Create a staging Hive table to hold the incremental data which will be inserted into the main
   * Hive table.
   *
   * @param genericService The interface containing the implementation of a
   *                       <code>GenericService</code> that is responsible for writing a type of
   *                       protobuf of interest into a Hive table.
   */
  public void createTmpTable(GenericService genericService) {
    // Use non-external table so that temporary file can be removed with table drop
    String sql =
        "CREATE TABLE %s (%s)"
            + " STORED AS ORC LOCATION '%s' TBLPROPERTIES ('ORC.COMPRESS' = 'ZLIB')";
    String query = String.format(
        sql,
        genericService.getTmpTableName(),
        genericService.getSchemaStr(),
        genericService.getOutputPath()
    );
    jdbcTemplate.execute(query);
  }

  /**
   * Insert the incremental data from a staging Hive table into the main Hive table.
   *
   * @param genericService The interface containing the implementation of a
   *                       <code>GenericService</code> that is responsible for writing a type of
   *                       protobuf of interest into a Hive table.
   */
  public void populateHiveTable(GenericService genericService) {
    String sql =
        "INSERT INTO %s.%s "
            + "SELECT a.`number`, a.`timestamp` FROM %s a "
            + "LEFT OUTER JOIN %s.%s b ON a.number = b.number "
            + "WHERE b.number IS NULL";
    String query = String.format(
        sql,
        schema,
        genericService.getTableName(),
        genericService.getTmpTableName(),
        schema,
        genericService.getTableName()
    );
    jdbcTemplate.execute(query);
  }

  /**
   * Drop the staging Hive table holding the incremental data.
   * <p>
   * This will remove the staging file that is holding the incremental data.
   *
   * @param genericService The interface containing the implementation of a
   *                       <code>GenericService</code> that is responsible for writing a type of
   *                       protobuf of interest into a Hive table.
   */
  public void dropTmpTable(GenericService genericService) {
    String sql = "DROP TABLE %s";
    String query = String.format(sql, genericService.getTmpTableName());
    jdbcTemplate.execute(query);
  }

  /**
   * Retrieve all <code>Block</code> numbers within the range of start (inclusive) and end
   * (inclusive).
   *
   * @param tableName The table name to run the search the block number range from.
   * @param start     Start <code>Block</code> number.
   * @param end       End <code>Block</code> number.
   * @return The <code>List</code> of <code>Block</code> numbers within the start (inclusive) and
   * end (inclusive) range.
   */
  public List<Long> findByNumberRange(String tableName, Long start, Long end) {
    String numberColName = "number";
    if (!tableName.equals("blocks")) {
      numberColName = "block_number";
    }

    String sql =
        "SELECT %s FROM %s.blocks "
            + "WHERE %s BETWEEN %s AND %s";
    String query = String.format(sql, numberColName, schema, numberColName, start, end);
    // String used in lambda expressions need to be final or effectively final
    String _numberColName = numberColName;
    return jdbcTemplate.query(query, (resultSet, i) -> resultSet.getLong(_numberColName));
  }

  /**
   * Obtain the schema of which the repository will be using.
   * <p>
   * The Hive tables will be stored in this schema, which will be defined in the application's
   * property file under: `spring.datasource.hivedb.schema`.
   *
   * @return The schema which the repository will be using.
   */
  public String getSchema() {
    return schema;
  }

}
