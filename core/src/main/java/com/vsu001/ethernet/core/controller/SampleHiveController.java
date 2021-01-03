package com.vsu001.ethernet.core.controller;

import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/debug")
public class SampleHiveController {

  private final JdbcTemplate jdbcTemplate;

  public SampleHiveController(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * Fetches the tables that are available in the `schemaName` provided.
   *
   * @param schemaName The schemaName of interest in which table names should be fetched.
   * @return List of tables in schema.
   */
  @RequestMapping(
      value = "/{schemaName}",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<List<Map<String, Object>>> showTables(@PathVariable String schemaName) {
    log.info("Fetching table(s) in schema: [{}]", schemaName);

    List<Map<String, Object>> rows = null;
    jdbcTemplate.execute("USE " + schemaName);
    rows = jdbcTemplate.queryForList("SHOW TABLES");

    return new ResponseEntity<>(rows, HttpStatus.OK);
  }

  /**
   * Returns the databases that are stored in hive.
   *
   * @return List of databases stored in Hive.
   */
  @RequestMapping(
      value = "/databases",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<List<Map<String, Object>>> showDatabases() {
    log.info("Fetching database(s)");

    List<Map<String, Object>> rows = null;
    rows = jdbcTemplate.queryForList("SHOW DATABASES");
    return new ResponseEntity<>(rows, HttpStatus.OK);
  }


}
