package io.github.ruedigerk.contractfirst.generator.integrationtest.spring

import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.client.api.NonJsonEntityAsJsonApiClient
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.client.model.CItem
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.server.model.SItem
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.server.resources.NonJsonEntityAsJsonApi
import io.github.ruedigerk.contractfirst.generator.integrationtest.spring.spec.SpringWebIntegrationSpecification
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.bind.annotation.RestController
import spock.lang.Subject

/**
 * Test for the generated client to handle a JSON response when the contract declares only non-JSON content types.
 */
@ContextConfiguration(classes = EmbeddedRestController)
class NonJsonEntityAsJsonTest extends SpringWebIntegrationSpecification {

  @Subject
  NonJsonEntityAsJsonApiClient apiClient = new NonJsonEntityAsJsonApiClient(apiRequestExecutor)

  def "Test post without body"() {
    when:
    def result = apiClient.getNonJsonEntityAsJson()

    then:
    result == new CItem(id: 42, name: "the name", tag: "the tag")
  }

  /**
   * Spring REST controller used in this test.
   */
  @RestController
  static class EmbeddedRestController implements NonJsonEntityAsJsonApi {

    @Override
    GetNonJsonEntityAsJsonResponse getNonJsonEntityAsJson() {
      def response = ResponseEntity.status(200)
          .contentType(MediaType.APPLICATION_JSON)
          .body(new SItem(id: 42, name: "the name", tag: "the tag"))

      return GetNonJsonEntityAsJsonResponse.withCustomResponse(response)
    }
  }
}
