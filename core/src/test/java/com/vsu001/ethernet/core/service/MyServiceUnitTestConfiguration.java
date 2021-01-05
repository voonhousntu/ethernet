package com.vsu001.ethernet.core.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyServiceUnitTestConfiguration {

  @Bean
  MyServiceImpl myService() {
    return new MyServiceImpl();
  }

}