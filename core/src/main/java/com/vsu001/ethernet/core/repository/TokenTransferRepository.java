package com.vsu001.ethernet.core.repository;

import com.vsu001.ethernet.core.model.TokenTransfer;
import com.vsu001.ethernet.core.repository.mapper.TokenTransferMapper;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class TokenTransferRepository {

  public static final String TABLE_NAME = "token_transfers";

  private final JdbcTemplate jdbcTemplate;

  @Value("${spring.datasource.hivedb.schema}")
  private String schema;

  public TokenTransferRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * Retrieve all <code>TokenTransferRepository</code>s within the range of start (inclusive) and
   * end (inclusive).
   *
   * @param start Start <code>Block</code> number.
   * @param end   End <code>Block</code> number.
   * @return The <code>List</code> of <code>TokenTransferRepository</code>s within the start
   * (inclusive) and end (inclusive) range.
   */
  public List<TokenTransfer> findByBlockNumberRange(Long start, Long end) {
    String sql = "SELECT * FROM %s.%s "
        + "WHERE number BETWEEN %s AND %s";
    String query = String.format(sql, schema, TABLE_NAME, start, end);
    return jdbcTemplate.query(query, new TokenTransferMapper());
  }

}
