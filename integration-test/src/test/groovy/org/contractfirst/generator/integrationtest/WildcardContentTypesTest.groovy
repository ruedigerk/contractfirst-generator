package org.contractfirst.generator.integrationtest

import okhttp3.logging.HttpLoggingInterceptor
import org.contractfirst.generator.client.DefinedResponse
import org.contractfirst.generator.client.GenericResponse
import org.contractfirst.generator.integrationtest.generated.client.api.WildcardContentTypesApiClient
import org.contractfirst.generator.integrationtest.generated.server.resources.WildcardContentTypesApi
import org.contractfirst.generator.integrationtest.spec.EmbeddedJaxRsServerSpecification
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
    GenericResponse genericResponse = restClient.getWildcardContentTypesWithResponse("text")
    DefinedResponse response = genericResponse.asDefinedResponse()

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