package com.vsu001.ethernet.core.repository;

import com.vsu001.ethernet.core.model.Transaction;
import com.vsu001.ethernet.core.repository.mapper.TransactionMapper;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class TransactionRepository {

  public static final String TABLE_NAME = "transactions";

  private final JdbcTemplate jdbcTemplate;

  @Value("${spring.datasource.hivedb.schema}")
  private String schema;

  public TransactionRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * Retrieve all <code>Transaction</code>s within the range of start (inclusive) and end
   * (inclusive).
   *
   * @param start Start <code>Block</code> number.
   * @param end   End <code>Block</code> number.
   * @return The <code>List</code> of <code>Transaction</code>s within the start (inclusive) and end
   * (inclusive) range.
   */
  public List<Transaction> findByBlockNumberRange(Long start, Long end) {
    String sql = "SELECT * FROM %s.%s "
        + "WHERE block_number BETWEEN %s AND %s";
    String query = String.format(sql, schema, TABLE_NAME, start, end);
    return jdbcTemplate.query(query, new TransactionMapper());
  }

}
