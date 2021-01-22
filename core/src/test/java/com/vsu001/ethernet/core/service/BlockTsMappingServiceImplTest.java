package com.vsu001.ethernet.core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.vsu001.ethernet.core.repository.GenericHiveRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class BlockTsMappingServiceImplTest {

  @Autowired
  private BlockTsMappingServiceImpl blockTsMappingService;

  @Autowired
  private GenericHiveRepository hiveRepository;

  @Test
  public void testFetchfromBq() throws InterruptedException {
    // Ensure we are using the test-schema
    assertEquals("ethernet_test", hiveRepository.getSchema());

    // Initialise the `UpdateRequest` object
    // `start` and `end` numbers are not used
    UpdateRequest updateRequest = UpdateRequest.newBuilder().build();

//    TableResult tableResult = blockTsMappingService.fetchFromBq(updateRequest);
  }

}
