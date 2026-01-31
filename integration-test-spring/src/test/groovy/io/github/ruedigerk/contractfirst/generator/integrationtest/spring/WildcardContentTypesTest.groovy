package io.github.ruedigerk.contractfirst.generator.integrationtest.spring

import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.client.api.WildcardContentTypesApiClient
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.server.resources.WildcardContentTypesApi
import io.github.ruedigerk.contractfirst.generator.integrationtest.spring.spec.SpringWebIntegrationSpecification
import okhttp3.logging.HttpLoggingInterceptor
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.bind.annotation.RestController
import spock.lang.Subject

/**
 * Test for wildcards in response content types.
 */
@ContextConfiguration(classes = EmbeddedRestController)
class WildcardContentTypesTest extends SpringWebIntegrationSpecification {

  @Subject
  WildcardContentTypesApiClient apiClient = new WildcardContentTypesApiClient(apiRequestExecutor)

  def "Operation with multiple wildcard response content types"() {
    when:
    def result = apiClient.getWildcardContentTypes("text")
    def response = result.response

    then:
    response.request.url == "$BASE_URL/wildcardContentTypes"
    response.request.method == "GET"
    response.statusCode == 200
    response.httpStatusMessage == "OK"
    response.contentType == "text/plain"
    response.entityType == String.class
    response.entity == "Some Text"

    when:
    // Disable logging of request body
    setLoggingInterceptorLevel(HttpLoggingInterceptor.Level.HEADERS)

    result = apiClient.getWildcardContentTypes("pdf")
    response = result.response

    then:
    response.request.url == "$BASE_URL/wildcardContentTypes"
    response.request.method == "GET"
    response.statusCode == 200
    response.httpStatusMessage == "OK"
    response.contentType == "application/pdf"
    response.entityType == InputStream.class
    (response.entity as InputStream).bytes == getClass().getResourceAsStream("/sample.pdf").bytes
  }

  /**
   * Spring REST controller used in this test.
   */
  @RestController
  static class EmbeddedRestController implements WildcardContentTypesApi {

    @Override
    GetWildcardContentTypesResponse getWildcardContentTypes(String testCaseSelector) {
      switch (testCaseSelector) {
        case "text":
          return GetWildcardContentTypesResponse.withCustomResponse(ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body("Some Text"))
        default:
          def pdfInputStream = new ClassPathResource("/sample.pdf")
          return GetWildcardContentTypesResponse.withCustomResponse(ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(pdfInputStream))
      }
    }
  }
}
