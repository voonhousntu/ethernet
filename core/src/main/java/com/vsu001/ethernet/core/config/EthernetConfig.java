package com.vsu001.ethernet.core.config;

import com.vsu001.ethernet.core.util.FileUtil;
import java.io.IOException;
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
    return ethernetWorkDir;
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

  public void createWorkDir() {
    try {
      FileUtil.createDir(getEthernetWorkDir());
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }

}
