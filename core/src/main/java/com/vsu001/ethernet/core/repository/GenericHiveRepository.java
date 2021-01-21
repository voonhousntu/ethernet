package com.vsu001.ethernet.core.repository;

import com.google.cloud.bigquery.TableResult;
import com.vsu001.ethernet.core.service.GenericService;
import com.vsu001.ethernet.core.util.OrcFileWriter;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class GenericHiveRepository {

  private final JdbcTemplate jdbcTemplate;

  public GenericHiveRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * Write the <code>TableResult</code> obtained from a BigQuery job into HDFS directory as a ORC
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
        tableResult
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
        genericService.getSchemaStr(),
        genericService.getTmpTableName(),
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
        "INSERT INTO ethernet.%s "
            + "SELECT * FROM %s a "
            + "LEFT OUTER JOIN %s b ON a.number = b.number "
            + "WHERE b.number IS NULL";
    String query = String.format(
        sql,
        genericService.getTableName(),
        genericService.getTmpTableName(),
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
    String query = String.format(sql, genericService.getTableName());
    jdbcTemplate.execute(query);
  }

}
