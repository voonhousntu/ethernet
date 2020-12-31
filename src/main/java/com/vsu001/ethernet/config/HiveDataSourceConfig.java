package com.vsu001.ethernet.config;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class HiveDataSourceConfig {

  @Value("${spring.datasource.hivedb.url}")
  private String hiveConnectionURL;

  @Value("${spring.datasource.hivedb.username}")
  private String username;

  @Value("${spring.datasource.hivedb.password}")
  private String password;

  @Value("${spring.datasource.hivedb.driver-class-name}")
  private String driverClassName;

  @Value("${spring.datasource.hivedb.initial-size}")
  private int initialSize;

  @Value("${spring.datasource.hivedb.validation-query}")
  private String validationQuery;

  @Value("${spring.datasource.hivedb.validation-query-timeout}")
  private int validationQueryTimeout;

  @Value("${spring.datasource.hivedb.test-on-borrow}")
  private boolean testOnBorrow;

  @Value("${spring.datasource.hivedb.max-wait}")
  private int maxWait;

  @Bean(name = "hiveDataSource")
  public DataSource hiveDataSource() {
    DataSource dataSource = new DataSource();
    dataSource.setUsername(username);
    dataSource.setPassword(password);
    dataSource.setUrl(hiveConnectionURL);
    dataSource.setDriverClassName(driverClassName);
    dataSource.setInitialSize(initialSize);
    dataSource.setValidationQuery(validationQuery);
    dataSource.setValidationQueryTimeout(validationQueryTimeout);
    dataSource.setTestOnBorrow(testOnBorrow);
    dataSource.setMaxWait(maxWait);
    return dataSource;
  }

  @Bean(name = "jdbcTemplate")
  public JdbcTemplate jdbcTemplate() {
    return new JdbcTemplate(hiveDataSource());
  }

}
