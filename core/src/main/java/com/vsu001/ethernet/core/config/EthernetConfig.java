package com.vsu001.ethernet.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EthernetConfig {

  @Value("${ethernet.work-dir}")
  private String ethernetWorkDir;

  public String getEthernetWorkDir() {
    return ethernetWorkDir;
  }

}
