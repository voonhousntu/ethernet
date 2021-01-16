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

  public void populateHiveTable(GenericService genericService) {
    String sql =
        "INSERT INTO ethernet.%s SELECT * FROM %s";
    String query = String.format(
        sql,
        genericService.getTableName(),
        genericService.getTmpTableName()
    );
    jdbcTemplate.execute(query);
  }

  public void dropTmpTable(GenericService genericService) {
    String sql = "DROP TABLE %s";
    String query = String.format(sql, genericService.getTableName());
    jdbcTemplate.execute(query);
  }

}
