package io.github.ruedigerk.contractfirst.generator.integrationtest.spring

import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.client.api.MultiValuedParametersApiClient
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.client.model.CSimpleEnum
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.server.model.SSimpleEnum
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.server.resources.MultiValuedParametersApi
import io.github.ruedigerk.contractfirst.generator.integrationtest.spring.spec.SpringWebIntegrationSpecification
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.bind.annotation.RestController
import spock.lang.Subject

/**
 * Tests support for array-valued parameters of different types.
 */
@ContextConfiguration(classes = EmbeddedRestController)
class MultiValuedParametersTest extends SpringWebIntegrationSpecification {

  @Subject
  MultiValuedParametersApiClient apiClient = new MultiValuedParametersApiClient(apiRequestExecutor)

  def "Server accepts List-valued query parameters"() {
    when:
    def result = apiClient.returningResult().multiValuedParametersTest(
        EmbeddedRestController.TEST_QUERY_PARAMS,
        ["whatever"],
        [CSimpleEnum.FIRST, CSimpleEnum.SECOND, CSimpleEnum.THIRD],
        null,
        ["whatever"] as Set,
        null,
        null,
    )

    then:
    result.status == 204
  }

  def "Server accepts Set-valued query parameters"() {
    when:
    def result = apiClient.returningResult().multiValuedParametersTest(
        EmbeddedRestController.TEST_QUERY_SET_PARAMS,
        ["whatever"],
        null,
        null,
        ["whatever"] as Set,
        [CSimpleEnum.FIRST, CSimpleEnum.SECOND, CSimpleEnum.THIRD] as Set,
        null,
    )

    then:
    result.status == 204
  }

  def "Server accepts List-valued header parameters"() {
    when:
    def result = apiClient.returningResult().multiValuedParametersTest(
        EmbeddedRestController.TEST_HEADER_PARAMS,
        ["whatever"],
        null,
        [1, 2, 3],
        ["whatever"] as Set,
        null,
        null,
    )

    then:
    result.status == 204
  }

  def "Server accepts Set-valued header parameters"() {
    when:
    def result = apiClient.returningResult().multiValuedParametersTest(
        EmbeddedRestController.TEST_HEADER_SET_PARAMS,
        ["whatever"],
        null,
        null,
        ["whatever"] as Set,
        null,
        [1, 2, 3] as Set,
    )

    then:
    result.status == 204
  }

  def "Server accepts List-valued path parameters"() {
    when:
    def result = apiClient.returningResult().multiValuedParametersTest(
        EmbeddedRestController.TEST_PATH_PARAMS,
        ["path1", "path2", "path3"],
        null,
        null,
        ["whatever"] as Set,
        null,
        null,
    )

    then:
    result.status == 204
  }

  def "Server accepts Set-valued path parameters"() {
    when:
    def result = apiClient.returningResult().multiValuedParametersTest(
        EmbeddedRestController.TEST_PATH_SET_PARAMS,
        ["whatever"],
        null,
        null,
        ["path1", "path2", "path3"] as Set,
        null,
        null,
    )

    then:
    result.status == 204
  }

  /**
   * Spring REST controller used in this test.
   */
  @RestController
  static class EmbeddedRestController implements MultiValuedParametersApi {

    static final TEST_QUERY_PARAMS = "queryParams"
    static final TEST_HEADER_PARAMS = "headerParams"
    static final TEST_PATH_PARAMS = "pathParams"
    static final TEST_QUERY_SET_PARAMS = "querySetParams"
    static final TEST_HEADER_SET_PARAMS = "headerSetParams"
    static final TEST_PATH_SET_PARAMS = "pathSetParams"

    @Override
    MultiValuedParametersTestResponse multiValuedParametersTest(
        String testSelector,
        List<String> pathParam,
        List<SSimpleEnum> queryParam,
        List<Integer> headerParam,
        Set<String> pathSetParam,
        Set<SSimpleEnum> querySetParam,
        Set<Integer> headerSetParam
    ) {
      switch (testSelector) {
        case TEST_QUERY_PARAMS:
          assert queryParam == [SSimpleEnum.FIRST, SSimpleEnum.SECOND, SSimpleEnum.THIRD]
          break
        case TEST_HEADER_PARAMS:
          assert headerParam == [1, 2, 3]
          break
        case TEST_PATH_PARAMS:
          assert pathParam == ["path1", "path2", "path3"]
          break
        case TEST_QUERY_SET_PARAMS:
          assert querySetParam == [SSimpleEnum.FIRST, SSimpleEnum.SECOND, SSimpleEnum.THIRD] as Set
          break
        case TEST_HEADER_SET_PARAMS:
          assert headerSetParam == [1, 2, 3] as Set
          break
        case TEST_PATH_SET_PARAMS:
          assert pathSetParam == ["path1", "path2", "path3"] as Set
          break
        default:
          throw new IllegalArgumentException("Unsupported test selector: $testSelector")
      }

      return MultiValuedParametersTestResponse.with204()
    }
  }
}
