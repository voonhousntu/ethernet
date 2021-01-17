package com.vsu001.ethernet.core.repository;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class BlockRepository {

  private final JdbcTemplate jdbcTemplate;

  public BlockRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public List<Long> findByNumberRange(long start, long end) {
    String sql =
        "SELECT number FROM ethernet.blocks "
            + "WHERE number BETWEEN %s AND %s";
    String query = String.format(sql, start, end);
    return jdbcTemplate.query(query, (resultSet, i) -> resultSet.getLong("number"));
  }

}
