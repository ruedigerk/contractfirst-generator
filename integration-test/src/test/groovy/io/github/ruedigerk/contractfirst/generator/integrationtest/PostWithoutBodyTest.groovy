package io.github.ruedigerk.contractfirst.generator.integrationtest

import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.client.api.PostWithoutBodyApiClient
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.server.resources.PostWithoutBodyApi
import io.github.ruedigerk.contractfirst.generator.integrationtest.spec.EmbeddedJaxRsServerSpecification
import spock.lang.Subject

/**
 * Test for HTTP methods POST, PUT and PATCH without request body. Background is the OkHttp client requiring these methods to always send a request body.
 */
class PostWithoutBodyTest extends EmbeddedJaxRsServerSpecification {

  @Subject
  PostWithoutBodyApiClient apiClient = new PostWithoutBodyApiClient(apiClientSupport)

  @Override
  Class<?> getTestResource() {
    EmbeddedServerResource
  }

  def "Test post without body"() {
    when:
    def result = apiClient.returningSuccessfulResult().postWithoutBody()

    then:
    result.isStatus204WithoutEntity()
  }

  def "Test put without body"() {
    when:
    def result = apiClient.returningSuccessfulResult().putWithoutBody()

    then:
    result.isStatus204WithoutEntity()
  }

  def "Test patch without body"() {
    when:
    def result = apiClient.returningSuccessfulResult().patchWithoutBody()

    then:
    result.isStatus204WithoutEntity()
  }

  /**
   * JAX-RS resource implementation used in this test.
   */
  static class EmbeddedServerResource implements PostWithoutBodyApi {

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
