package io.github.ruedigerk.contractfirst.generator.integrationtest.spring

import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.client.api.PostWithoutBodyApiClient
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.server.resources.PostWithoutBodyApi
import io.github.ruedigerk.contractfirst.generator.integrationtest.spring.spec.SpringWebIntegrationSpecification
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.bind.annotation.RestController
import spock.lang.Subject

/**
 * Test for HTTP methods POST, PUT and PATCH without request body. Background is the OkHttp client requiring these methods to always send a request body.
 */
@ContextConfiguration(classes = EmbeddedRestController)
class PostWithoutBodyTest extends SpringWebIntegrationSpecification {

  @Subject
  PostWithoutBodyApiClient apiClient = new PostWithoutBodyApiClient(apiRequestExecutor)

  def "Test post without body"() {
    when:
    def result = apiClient.returningResult().postWithoutBody()

    then:
    result.isStatus204WithoutEntity()
  }

  def "Test put without body"() {
    when:
    def result = apiClient.returningResult().putWithoutBody()

    then:
    result.isStatus204WithoutEntity()
  }

  def "Test patch without body"() {
    when:
    def result = apiClient.returningResult().patchWithoutBody()

    then:
    result.isStatus204WithoutEntity()
  }

  /**
   * Spring REST controller used in this test.
   */
  @RestController
  static class EmbeddedRestController implements PostWithoutBodyApi {

    @Override
    PostWithoutBodyResponse postWithoutBody() {
      return PostWithoutBodyResponse.with204()
    }

    @Override
    PutWithoutBodyResponse putWithoutBody() {
      return PutWithoutBodyResponse.with204()
    }

    @Override
    PatchWithoutBodyResponse patchWithoutBody() {
      return PatchWithoutBodyResponse.with204()
    }
  }
}
