package io.github.ruedigerk.contractfirst.generator.integrationtest

import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.client.api.MultiValuedParametersApiClient
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.client.model.CSimpleEnum
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.server.model.SSimpleEnum
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.server.resources.MultiValuedParametersApi
import io.github.ruedigerk.contractfirst.generator.integrationtest.spec.EmbeddedJaxRsServerSpecification
import spock.lang.Subject

import static io.github.ruedigerk.contractfirst.generator.integrationtest.MultiValuedParametersTest.EmbeddedServerResource.TEST_HEADER_PARAMS
import static io.github.ruedigerk.contractfirst.generator.integrationtest.MultiValuedParametersTest.EmbeddedServerResource.TEST_PATH_PARAMS
import static io.github.ruedigerk.contractfirst.generator.integrationtest.MultiValuedParametersTest.EmbeddedServerResource.TEST_QUERY_PARAMS

/**
 * Tests support for array-valued parameters of different types.
 */
class MultiValuedParametersTest extends EmbeddedJaxRsServerSpecification {

  @Subject
  MultiValuedParametersApiClient apiClient = new MultiValuedParametersApiClient(apiClientSupport)

  @Override
  Class<?> getTestResource() {
    EmbeddedServerResource
  }

  def "Server accepts array-valued query parameters"() {
    when:
    def result = apiClient.returningResult().multiValuedParametersTest(
        TEST_QUERY_PARAMS,
        ["whatever"],
        [CSimpleEnum.FIRST, CSimpleEnum.SECOND, CSimpleEnum.THIRD],
        null
    )

    then:
    result.status == 204
  }

  def "Server accepts array-valued header parameters"() {
    when:
    def result = apiClient.returningResult().multiValuedParametersTest(
        TEST_HEADER_PARAMS,
        ["whatever"],
        null,
        [1, 2, 3]
    )

    then:
    result.status == 204
  }

  def "Server accepts array-valued path parameters"() {
    when:
    def result = apiClient.returningResult().multiValuedParametersTest(
        TEST_PATH_PARAMS,
        ["path1", "path2", "path3"],
        null,
        null
    )

    then:
    result.status == 204
  }

  /**
   * JAX-RS resource implementation used in this test.
   */
  static class EmbeddedServerResource implements MultiValuedParametersApi {

    static final TEST_QUERY_PARAMS = "queryParams"
    static final TEST_HEADER_PARAMS = "headerParams"
    static final TEST_PATH_PARAMS = "pathParams"

    @Override
    MultiValuedParametersTestResponse multiValuedParametersTest(
        String testSelector,
        List<String> pathParam,
        List<SSimpleEnum> queryParam,
        List<Integer> headerParam
    ) {
      switch (testSelector) {
        case TEST_QUERY_PARAMS:
          assert queryParam == [SSimpleEnum.FIRST, SSimpleEnum.SECOND, SSimpleEnum.THIRD]
          break
        case TEST_HEADER_PARAMS:
          assert headerParam == [1, 2, 3]
          break
        case TEST_PATH_PARAMS:
          // The client sends a path segment "path1,path2,path3" as by the OpenAPI spec but JAX-RS can not deserialize that to a list by default.
          // See: https://docs.jboss.org/resteasy/docs/3.6.0.Final/userguide/html/StringConverter.html#d4e1631
          assert pathParam == ["path1,path2,path3"]
          break
        default:
          throw new IllegalArgumentException("Unsupported test selector: $testSelector")
      }

      return MultiValuedParametersTestResponse.with204()
    }
  }
}
