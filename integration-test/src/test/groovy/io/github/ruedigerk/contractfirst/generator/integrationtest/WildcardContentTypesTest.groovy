package io.github.ruedigerk.contractfirst.generator.integrationtest

import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.client.api.WildcardContentTypesApiClient
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.server.resources.WildcardContentTypesApi
import io.github.ruedigerk.contractfirst.generator.integrationtest.spec.EmbeddedJaxRsServerSpecification
import okhttp3.logging.HttpLoggingInterceptor
import spock.lang.Subject

import jakarta.ws.rs.core.Response

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
