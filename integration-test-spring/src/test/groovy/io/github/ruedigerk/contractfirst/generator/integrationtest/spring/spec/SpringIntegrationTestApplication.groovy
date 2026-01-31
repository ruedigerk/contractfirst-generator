package io.github.ruedigerk.contractfirst.generator.integrationtest.spring.spec

import org.springframework.boot.SpringApplication
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration

/**
 * The Spring application used in tests. The only beans/controllers are defined inside the actual test classes.
 */
@SpringBootConfiguration
@EnableAutoConfiguration
class SpringIntegrationTestApplication {

  static void main(String[] args) {
    SpringApplication.run(SpringIntegrationTestApplication, args)
  }
}
