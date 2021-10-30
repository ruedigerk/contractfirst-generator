package io.github.ruedigerk.contractfirst.generator.integrationtest

import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.client.api.NonJavaPropertyNamesApiClient
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.client.model.CProblematicName
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.client.model.CProblematicNameProblematC
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.server.model.SProblematicName
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.server.resources.NonJavaPropertyNamesApi
import io.github.ruedigerk.contractfirst.generator.integrationtest.spec.EmbeddedJaxRsServerSpecification
import spock.lang.Subject

/**
 * Tests serialization of models with properties that are not valid Java identifiers.
 */
class NonJavaPropertyNamesTest extends EmbeddedJaxRsServerSpecification {

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
  static class EmbeddedServerResource implements NonJavaPropertyNamesApi {

    @Override
    PostNonJavaPropertyNamesResponse postNonJavaPropertyNames(SProblematicName requestBody) {
      return PostNonJavaPropertyNamesResponse.with200ApplicationJson(requestBody)
    }
  }
}
