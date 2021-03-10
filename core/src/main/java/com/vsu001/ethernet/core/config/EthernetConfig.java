package com.vsu001.ethernet.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class EthernetConfig {

  @Value("${ethernet.work-dir}")
  private String ethernetWorkDir;

  @Value("${ethernet.rpyc-host}")
  private String rpycHost;

  @Value("${ethernet.rpyc-port}")
  private String rpycPort;

  @Value("${ethernet.neo4j-container-name}")
  private String neo4jContainerName;

  public String getEthernetWorkDir() {
    return ethernetWorkDir.replaceFirst("^~", System.getProperty("user.home"));
  }

  public String getRpycHost() {
    return rpycHost;
  }

  public String getRpycPort() {
    return rpycPort;
  }

  public String getNeo4jContainerName() {
    return neo4jContainerName;
  }

}
