package com.vsu001.ethernet.core.repository;

import com.vsu001.ethernet.core.model.Block;
import com.vsu001.ethernet.core.repository.mapper.BlockMapper;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class BlockRepository {

  public static final String TABLE_NAME = "blocks";

  private final JdbcTemplate jdbcTemplate;

  @Value("${spring.datasource.hivedb.schema}")
  private String schema;

  public BlockRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * Retrieve all <code>Block</code> numbers within the range of start (inclusive) and end
   * (inclusive).
   *
   * @param start Start <code>Block</code> number.
   * @param end   End <code>Block</code> number.
   * @return The <code>List</code> of <code>Block</code> numbers within the start (inclusive) and
   * end (inclusive) range.
   */
  public List<Long> findNumberByNumberRange(Long start, Long end) {
    String sql =
        "SELECT number FROM %s.blocks "
            + "WHERE number BETWEEN %s AND %s";
    String query = String.format(sql, schema, start, end);
    return jdbcTemplate.query(query, (resultSet, i) -> resultSet.getLong("number"));
  }

  /**
   * Retrieve all <code>Block</code>s within the range of start (inclusive) and end (inclusive).
   *
   * @param start Start <code>Block</code> number.
   * @param end   End <code>Block</code> number.
   * @return The <code>List</code> of <code>Block</code>s within the start (inclusive) and end
   * (inclusive) range.
   */
  public List<Block> findByNumberRange(Long start, Long end) {
    String sql = "SELECT * FROM %s.blocks "
        + "WHERE number BETWEEN %s AND %s";
    String query = String.format(sql, schema, start, end);
    return jdbcTemplate.query(query, new BlockMapper());
  }

}
