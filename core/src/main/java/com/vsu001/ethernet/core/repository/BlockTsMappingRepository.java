package com.vsu001.ethernet.core.repository;

import com.vsu001.ethernet.core.model.BlockTimestampMapping;
import com.vsu001.ethernet.core.repository.mapper.BlockTsMappingMapper;
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

}
