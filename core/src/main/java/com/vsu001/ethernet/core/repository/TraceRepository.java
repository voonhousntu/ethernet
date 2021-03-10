package com.vsu001.ethernet.core.repository;

import com.vsu001.ethernet.core.model.Trace;
import com.vsu001.ethernet.core.repository.mapper.TraceMapper;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class TraceRepository {

  public static final String TABLE_NAME = "traces";

  private final JdbcTemplate jdbcTemplate;

  @Value("${spring.datasource.hivedb.schema}")
  private String schema;

  public TraceRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * Retrieve all <code>Trace</code>s within the range of start (inclusive) and end (inclusive).
   *
   * @param start Start <code>Block</code> number.
   * @param end   End <code>Block</code> number.
   * @return The <code>List</code> of <code>Trace</code>s within the start (inclusive) and end
   * (inclusive) range.
   */
  public List<Trace> findByBlockNumberRange(Long start, Long end) {
    String sql = "SELECT * FROM %s.traces "
        + "WHERE `block_number` BETWEEN %s AND %s";
    String query = String.format(sql, schema, start, end);
    return jdbcTemplate.query(query, new TraceMapper());
  }

}
