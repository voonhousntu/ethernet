package com.vsu001.ethernet.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Neo4jConfig {

  @Value("${spring.neo4j.uri}")
  private String neo4jUri;

  @Value("${spring.data.neo4j.username}")
  private String neo4jUsername;

  @Value("${spring.data.neo4j.password}")
  private String neo4jPassword;

  public String getNeo4jUri() {
    return neo4jUri;
  }

  public String getNeo4jUsername() {
    return neo4jUsername;
  }

  public String getNeo4jPassword() {
    return neo4jPassword;
  }

}
