package com.vsu001.ethernet.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EthernetApplication {

  public static void main(String[] args) {
    // Enforce the usage of UTC timezone in JVM
    System.setProperty("user.timezone", "UTC");
    SpringApplication.run(EthernetApplication.class, args);
  }

}
