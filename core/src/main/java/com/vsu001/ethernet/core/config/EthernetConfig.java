package com.vsu001.ethernet.core.config;

import static com.vsu001.ethernet.core.util.FileUtil.createDir;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class EthernetConfig {

  @Value("${ethernet.work-dir}")
  private String ethernetWorkDir;

  public String getEthernetWorkDir() {
    return ethernetWorkDir;
  }

  public void createWorkDir() {
    try {
      createDir(getEthernetWorkDir());
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }

}
