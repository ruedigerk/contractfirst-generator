package io.github.ruedigerk.contractfirst.generator.integrationtest

import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.client.api.FormEncodedRequestBodyApiClient
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.client.model.CFormEncodedRequestBodyRequestBodyFieldC
import io.github.ruedigerk.contractfirst.generator.integrationtest.spec.EmbeddedJaxRsServerSpecification
import spock.lang.Subject

import javax.ws.rs.*

/**
 * Tests serialization of form encoded request bodies.
 */
class FormEncodedRequestBodyClientTest extends EmbeddedJaxRsServerSpecification {

  @Subject
  FormEncodedRequestBodyApiClient apiClient = new FormEncodedRequestBodyApiClient(apiClientSupport)

  @Override
  Class<?> getTestResource() {
    EmbeddedServerResource
  }

  def "Test form encoded request body"() {
    when:
    def result = apiClient.returningResult().formEncodedRequestBody("a&1", "b = really great!", CFormEncodedRequestBodyRequestBodyFieldC.SECOND_VALUE)

    then:
    result.isStatus204WithoutEntity()
  }

  /**
   * JAX-RS resource implementation used in this test.
   */
  @Path("")
  static class EmbeddedServerResource {

    @POST
    @Path("/formEncodedRequestBody")
    @Consumes("application/x-www-form-urlencoded")
    @Produces
    void formEncodedRequestBody(@FormParam("fieldA") String fieldA, @FormParam("fieldB") String fieldB, @FormParam("fieldC") String fieldC) {
      assert fieldA == "a&1" 
      assert fieldB == "b = really great!"
      assert fieldC == "second%value"
    }
  }
}
