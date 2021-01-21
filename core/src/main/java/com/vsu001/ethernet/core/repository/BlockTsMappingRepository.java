package com.vsu001.ethernet.core.repository;

import com.vsu001.ethernet.core.model.BlockTimestampMapping;
import com.vsu001.ethernet.core.repository.mapper.BlockTsMappingMapper;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class BlockTsMappingRepository {

  private final JdbcTemplate jdbcTemplate;

  public BlockTsMappingRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * Retrieve the most recent <code>BlockTimestampMapping</code> from the block_timestamp_mapping
   * Hive table.
   *
   * @return The row with the largest timestamp value wrapped in a <code>BlockTimestamp</code>
   * object.
   */
  public BlockTimestampMapping findMostRecent() {
    String query =
        "SELECT * FROM ethernet.block_timestamp_mapping "
            + "ORDER BY `timestamp` DESC "
            + "LIMIT 1";
    return jdbcTemplate.queryForObject(query, new BlockTsMappingMapper());
  }

  /**
   * Retrieve the row with the `number` of interest specified in the number parameter of the
   * method.
   *
   * @param number The <code>Block</code> number of interest.
   * @return The row with the <code>Block</code> number that is equal to the user provided `number`
   * argument.
   */
  public BlockTimestampMapping findByNumber(Long number) {
    String sql =
        "SELECT * FROM ethernet.block_timestamp_mapping "
            + "WHERE number = %s";
    String query = String.format(sql, number);
    return jdbcTemplate.queryForObject(query, new BlockTsMappingMapper());
  }

  /**
   * Retrieve the rows with the <code>List</code> of long-type <code>Block</code> numbers.
   *
   * @param numbers The <code>List</code> of long-type <code>Block</code> numbers of interest.
   * @return <code>List</code> of <code>BlockTimestampMapping</code> with the <code>Block</code>
   * numbers specified in the user provided method argument.
   */
  public List<BlockTimestampMapping> findByNumbers(List<Long> numbers) {
    String sql =
        "SELECT * FROM ethernet.block_timestamp_mapping "
            + "WHERE number IN (%s) "
            + "ORDER BY number ASC";
    String query = String
        .format(
            sql,
            numbers.stream().map(String::valueOf).collect(Collectors.joining(","))
        );
    return jdbcTemplate.query(query, new BlockTsMappingMapper());
  }

}
