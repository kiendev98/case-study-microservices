package com.kien.microservices.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {"spring.profiles.active=native"})
class ConfigurationServerApplicationTests {

  @Test
  void contextLoads() {

  }
}
