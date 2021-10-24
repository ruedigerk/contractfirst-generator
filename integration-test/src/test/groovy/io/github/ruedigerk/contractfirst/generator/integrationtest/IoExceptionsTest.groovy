package io.github.ruedigerk.contractfirst.generator.integrationtest

import io.github.ruedigerk.contractfirst.generator.client.ApiClientIoException
import io.github.ruedigerk.contractfirst.generator.client.RequestExecutor
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.client.api.MultipleContentTypesApiClient
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.server.resources.MultipleContentTypesApi
import io.github.ruedigerk.contractfirst.generator.integrationtest.spec.EmbeddedJaxRsServerSpecification

import java.time.Duration

/**
 * Test for IOExceptions thrown by the API client.
 */
class IoExceptionsTest extends EmbeddedJaxRsServerSpecification {

  @Override
  Class<?> getTestResource() {
    EmbeddedServerResource
  }

  def "IOException for callTimeout"() {
    given:
    def clientSupport = new RequestExecutor(okHttpClient.newBuilder().callTimeout(Duration.ofMillis(1)).build(), BASE_URL)
    MultipleContentTypesApiClient apiClient = new MultipleContentTypesApiClient(clientSupport)

    when:
    apiClient.getManual(null)

    then:
    def e = thrown ApiClientIoException
    e.message.contains("timeout")
    e.message.contains("GET http://localhost:17249/manuals")
  }

  def "IOException for readTimeout"() {
    given:
    def clientSupport = new RequestExecutor(okHttpClient.newBuilder().readTimeout(Duration.ofMillis(1)).build(), BASE_URL)
    MultipleContentTypesApiClient apiClient = new MultipleContentTypesApiClient(clientSupport)

    when:
    apiClient.getManual(null)

    then:
    def e = thrown ApiClientIoException
    e.message.contains("timeout")
    e.message.contains("GET http://localhost:17249/manuals")
  }

  /**
   * JAX-RS resource implementation used in this test.
   */
  static class EmbeddedServerResource implements MultipleContentTypesApi {

    @Override
    GetManualResponse getManual(String testCaseSelector) {
      Thread.sleep(1000)
      GetManualResponse.with204()
    }
  }
}
