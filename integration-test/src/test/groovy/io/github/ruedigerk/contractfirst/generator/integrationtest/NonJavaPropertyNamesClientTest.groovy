package io.github.ruedigerk.contractfirst.generator.integrationtest

import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.client.api.NonJavaPropertyNamesApiClient
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.client.model.CProblematicName
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.client.model.CProblematicNameProblematC
import io.github.ruedigerk.contractfirst.generator.integrationtest.spec.EmbeddedJaxRsServerSpecification
import spock.lang.Subject

import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces

/**
 * Tests serialization of models with properties that are not valid Java identifiers.
 */
class NonJavaPropertyNamesClientTest extends EmbeddedJaxRsServerSpecification {

  @Subject
  NonJavaPropertyNamesApiClient apiClient = new NonJavaPropertyNamesApiClient(apiClientSupport)

  @Override
  Class<?> getTestResource() {
    EmbeddedServerResource
  }

  def "Test postNonJavaPropertyNames"() {
    given:
    CProblematicName entity = new CProblematicName(_2name: "1", nameAndValue: "2", problematC: CProblematicNameProblematC.THREE)

    when:
    CProblematicName response = apiClient.postNonJavaPropertyNames(entity)

    then:
    response == entity
  }

  /**
   * JAX-RS resource implementation used in this test.
   */
  @Path("")
  static class EmbeddedServerResource {

    @POST
    @Path("/nonJavaPropertyNames")
    @Consumes("application/json")
    @Produces("application/json")
    JsonObject postNonJavaPropertyNames(JsonObject requestBody) {
      assert requestBody.getAsJsonPrimitive("2name") == new JsonPrimitive("1")
      assert requestBody.getAsJsonPrimitive("name-and-value") == new JsonPrimitive("2")
      assert requestBody.getAsJsonPrimitive("problemat%c") == new JsonPrimitive("three?")

      return requestBody
    }
  }
}
