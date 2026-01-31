package io.github.ruedigerk.contractfirst.generator.integrationtest.spring

import io.github.ruedigerk.contractfirst.generator.client.ApiClientIoException
import io.github.ruedigerk.contractfirst.generator.client.ApiRequestExecutor
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.client.api.MultipleContentTypesApiClient
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.server.resources.MultipleContentTypesApi
import io.github.ruedigerk.contractfirst.generator.integrationtest.spring.spec.SpringWebIntegrationSpecification
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.bind.annotation.RestController

import java.time.Duration

/**
 * Test for IOExceptions thrown by the API client.
 */
@ContextConfiguration(classes = EmbeddedRestController)
class IoExceptionsTest extends SpringWebIntegrationSpecification {

  def "IOException for timeout"() {
    given:
    def clientSupport = new ApiRequestExecutor(okHttpClient.newBuilder().callTimeout(Duration.ofMillis(1)).build(), BASE_URL)
    MultipleContentTypesApiClient apiClient = new MultipleContentTypesApiClient(clientSupport)

    when:
    apiClient.getManual(null)

    then:
    def e = thrown ApiClientIoException
    e.message.contains("timeout")
    e.message.contains("GET http://localhost:17249/manuals")
  }

  /**
   * Spring REST controller used in this test.
   */
  @RestController
  static class EmbeddedRestController implements MultipleContentTypesApi {

    @Override
    GetManualResponse getManual(String testCaseSelector) {
      Thread.sleep(50)
      GetManualResponse.with204()
    }
  }
}
