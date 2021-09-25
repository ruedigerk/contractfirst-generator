package io.github.ruedigerk.contractfirst.generator.integrationtest

import okhttp3.logging.HttpLoggingInterceptor
import io.github.ruedigerk.contractfirst.generator.client.DefinedResponse
import io.github.ruedigerk.contractfirst.generator.client.GenericResponse
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.client.api.WildcardContentTypesApiClient
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.server.resources.WildcardContentTypesApi
import io.github.ruedigerk.contractfirst.generator.integrationtest.spec.EmbeddedJaxRsServerSpecification
import spock.lang.Subject

import javax.ws.rs.core.Response

/**
 * Test for wildcards in response content types.
 */
class WildcardContentTypesTest extends EmbeddedJaxRsServerSpecification {

  @Subject
  WildcardContentTypesApiClient restClient = new WildcardContentTypesApiClient(restClientSupport)

  @Override
  Class<?> getTestResource() {
    EmbeddedServerResource
  }

  def "Operation with multiple wildcard response content types"() {
    when:
    io.github.ruedigerk.contractfirst.generator.client.GenericResponse genericResponse = restClient.getWildcardContentTypesWithResponse("text")
    io.github.ruedigerk.contractfirst.generator.client.DefinedResponse response = genericResponse.asDefinedResponse()

    then:
    response.request.url == "$BASE_URL/wildcardContentTypes"
    response.request.method == "GET"
    response.statusCode == 200
    response.httpStatusMessage == "OK"
    response.contentType == "text/plain"
    response.javaType == String.class
    response.entity == "Some Text"

    when:
    // First, disable logging of request body
    setLoggingInterceptorLevel(HttpLoggingInterceptor.Level.HEADERS)
    
    genericResponse = restClient.getWildcardContentTypesWithResponse("pdf")
    response = genericResponse.asDefinedResponse()

    then:
    response.request.url == "$BASE_URL/wildcardContentTypes"
    response.request.method == "GET"
    response.statusCode == 200
    response.httpStatusMessage == "OK"
    response.contentType == "application/pdf"
    response.javaType == InputStream.class
    (response.entity as InputStream).bytes == getClass().getResourceAsStream("/sample.pdf").bytes
  }

  /**
   * JAX-RS resource implementation used in this test.
   */
  static class EmbeddedServerResource implements io.github.ruedigerk.contractfirst.generator.integrationtest.generated.server.resources.WildcardContentTypesApi {

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
