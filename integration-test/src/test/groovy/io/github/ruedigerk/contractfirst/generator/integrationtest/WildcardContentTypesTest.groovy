package io.github.ruedigerk.contractfirst.generator.integrationtest

import io.github.ruedigerk.contractfirst.generator.client.ApiResponse
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.client.api.WildcardContentTypesApiClient
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.server.resources.WildcardContentTypesApi
import io.github.ruedigerk.contractfirst.generator.integrationtest.spec.EmbeddedJaxRsServerSpecification
import okhttp3.logging.HttpLoggingInterceptor
import spock.lang.Subject

import javax.ws.rs.core.Response

/**
 * Test for wildcards in response content types.
 */
class WildcardContentTypesTest extends EmbeddedJaxRsServerSpecification {

  @Subject
  WildcardContentTypesApiClient apiClient = new WildcardContentTypesApiClient(apiClientSupport)

  @Override
  Class<?> getTestResource() {
    EmbeddedServerResource
  }

  def "Operation with multiple wildcard response content types"() {
    when:
    def successfulResponse = apiClient.getWildcardContentTypes("text")
    ApiResponse apiResponse = successfulResponse.apiResponse

    then:
    apiResponse.request.url == "$BASE_URL/wildcardContentTypes"
    apiResponse.request.method == "GET"
    apiResponse.statusCode == 200
    apiResponse.httpStatusMessage == "OK"
    apiResponse.contentType == "text/plain"
    apiResponse.entityType == String.class
    apiResponse.entity == "Some Text"

    when:
    // Disable logging of request body
    setLoggingInterceptorLevel(HttpLoggingInterceptor.Level.HEADERS)

    successfulResponse = apiClient.getWildcardContentTypes("pdf")
    apiResponse = successfulResponse.apiResponse

    then:
    apiResponse.request.url == "$BASE_URL/wildcardContentTypes"
    apiResponse.request.method == "GET"
    apiResponse.statusCode == 200
    apiResponse.httpStatusMessage == "OK"
    apiResponse.contentType == "application/pdf"
    apiResponse.entityType == InputStream.class
    (apiResponse.entity as InputStream).bytes == getClass().getResourceAsStream("/sample.pdf").bytes
  }

  /**
   * JAX-RS resource implementation used in this test.
   */
  static class EmbeddedServerResource implements WildcardContentTypesApi {

    @Override
    GetWildcardContentTypesResponse getWildcardContentTypes(String testCaseSelector) {
      switch (testCaseSelector) {
        case "text":
          return GetWildcardContentTypesResponse.withCustomResponse(Response.ok("Some Text", "text/plain").build())
        default:
          def pdfInputStream = getClass().getResourceAsStream("/sample.pdf")
          return GetWildcardContentTypesResponse.withCustomResponse(Response.ok(pdfInputStream, "application/pdf").build())
      }
    }
  }
}
