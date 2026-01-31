package io.github.ruedigerk.contractfirst.generator.integrationtest

import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.client.api.NonJsonEntityAsJsonApiClient
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.client.model.CItem
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.server.model.SItem
import io.github.ruedigerk.contractfirst.generator.integrationtest.spec.EmbeddedJaxRsServerSpecification
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import spock.lang.Subject

/**
 * Test for the generated client to handle a JSON response when the contract declares only non-JSON content types.
 */
class NonJsonEntityAsJsonTest extends EmbeddedJaxRsServerSpecification {

  @Subject
  NonJsonEntityAsJsonApiClient apiClient = new NonJsonEntityAsJsonApiClient(apiRequestExecutor)

  @Override
  Class<?> getTestResource() {
    EmbeddedServerResource
  }

  def "Test post without body"() {
    when:
    def result = apiClient.getNonJsonEntityAsJson()

    then:
    result == new CItem(id: 42, name: "the name", tag: "the tag")
  }

  /**
   * JAX-RS resource implementation used in this test.
   */
  @Path("")
  static class EmbeddedServerResource {

    @GET
    @Path("/nonJsonEntityAsJson")
    Response getNonJsonEntityAsJson() {
      return Response.ok(new SItem(id: 42, name: "the name", tag: "the tag"), MediaType.APPLICATION_JSON_TYPE).build()
    }
  }
}
