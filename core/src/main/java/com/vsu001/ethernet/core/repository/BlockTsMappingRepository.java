package com.vsu001.ethernet.core.repository;

import com.vsu001.ethernet.core.model.BlockTimestampMapping;
import com.vsu001.ethernet.core.repository.mapper.BlockTsMappingMapper;
import java.util.List;
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

  public BlockTimestampMapping findMostRecent() {
    String query =
        "SELECT * FROM ethernet.block_timestamp_mapping "
        + "ORDER BY `timestamp` DESC "
        + "LIMIT 1";
    return jdbcTemplate.queryForObject(query, new BlockTsMappingMapper());
  }

  public BlockTimestampMapping findByNumber(Long number) {
    String sql =
        "SELECT * FROM ethernet.block_timestamp_mapping "
            + "WHERE number = %s";
    String query = String.format(sql, number);
    return jdbcTemplate.queryForObject(query, new BlockTsMappingMapper());
  }

  public List<BlockTimestampMapping> findByStartEndNumber(Long startNumber, Long endNumber) {
    String sql =
        "SELECT * FROM ethernet.block_timestamp_mapping "
            + "WHERE number = %s OR number = %s "
            + "ORDER BY number ASC";
    String query = String.format(sql, startNumber, endNumber);
    // Result should only have two rows
    return jdbcTemplate.query(query, new BlockTsMappingMapper());
  }

}
