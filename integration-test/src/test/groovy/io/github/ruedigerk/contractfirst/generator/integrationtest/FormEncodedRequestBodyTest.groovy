package io.github.ruedigerk.contractfirst.generator.integrationtest

import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.client.api.FormEncodedRequestBodyApiClient
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.server.resources.FormEncodedRequestBodyApi
import io.github.ruedigerk.contractfirst.generator.integrationtest.spec.EmbeddedJaxRsServerSpecification
import spock.lang.Subject

/**
 * Tests serialization of form encoded request bodies.
 */
class FormEncodedRequestBodyTest extends EmbeddedJaxRsServerSpecification {

  @Subject
  FormEncodedRequestBodyApiClient apiClient = new FormEncodedRequestBodyApiClient(apiClientSupport)

  @Override
  Class<?> getTestResource() {
    EmbeddedServerResource
  }

  def "Test form encoded request body"() {
    when:
    def result = apiClient.returningResult().formEncodedRequestBody("a&1", "b = really great!")

    then:
    result.isStatus204WithoutEntity()
  }

  /**
   * JAX-RS resource implementation used in this test.
   */
  static class EmbeddedServerResource implements FormEncodedRequestBodyApi {

    @Override
    FormEncodedRequestBodyResponse formEncodedRequestBody(String fieldA, String fieldB) {
      assert fieldA == "a&1" && fieldB == "b = really great!"
      return FormEncodedRequestBodyResponse.with204()
    }
  }
}
