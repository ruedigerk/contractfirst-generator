package io.github.ruedigerk.contractfirst.generator.integrationtest

import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.client.api.NonJsonEntityAsJsonApiClient
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.client.model.CItem
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.server.model.SItem
import io.github.ruedigerk.contractfirst.generator.integrationtest.spec.EmbeddedJaxRsServerSpecification
import spock.lang.Subject

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * Test for HTTP methods POST, PUT and PATCH without request body. Background is the OkHttp client requiring these methods to always send a request body.
 */
class NonJsonEntityAsJsonTest extends EmbeddedJaxRsServerSpecification {

  @Subject
  NonJsonEntityAsJsonApiClient apiClient = new NonJsonEntityAsJsonApiClient(apiClientSupport)

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
