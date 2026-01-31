package io.github.ruedigerk.contractfirst.generator.integrationtest.spring

import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.client.api.NonJavaPropertyNamesApiClient
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.client.model.CProblematicName
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.client.model.CProblematicNameProblematC
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.server.model.SProblematicName
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.server.model.SProblematicNameProblematC
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.server.resources.NonJavaPropertyNamesApi
import io.github.ruedigerk.contractfirst.generator.integrationtest.spring.spec.SpringWebIntegrationSpecification
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.bind.annotation.RestController
import spock.lang.Subject

/**
 * Tests serialization of models with properties that are not valid Java identifiers.
 */
@ContextConfiguration(classes = EmbeddedRestController)
class NonJavaPropertyNamesClientTest extends SpringWebIntegrationSpecification {

  @Subject
  NonJavaPropertyNamesApiClient apiClient = new NonJavaPropertyNamesApiClient(apiRequestExecutor)

  def "Test postNonJavaPropertyNames"() {
    given:
    CProblematicName entity = new CProblematicName(_2name: "1", nameAndValue: "2", problematC: CProblematicNameProblematC.THREE)

    when:
    CProblematicName response = apiClient.postNonJavaPropertyNames(entity)

    then:
    response == entity
  }

  /**
   * Spring REST controller used in this test.
   */
  @RestController
  static class EmbeddedRestController implements NonJavaPropertyNamesApi {

    @Override
    PostNonJavaPropertyNamesResponse postNonJavaPropertyNames(SProblematicName requestBody) {
      assert requestBody._2name == "1"
      assert requestBody.nameAndValue == "2"
      assert requestBody.problematC == SProblematicNameProblematC.THREE

      return PostNonJavaPropertyNamesResponse.with200ApplicationJson(requestBody)
    }
  }
}
