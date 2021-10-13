package io.github.ruedigerk.contractfirst.generator.integrationtest

import io.github.ruedigerk.contractfirst.generator.client.DefinedResponse
import io.github.ruedigerk.contractfirst.generator.client.GenericResponse
import io.github.ruedigerk.contractfirst.generator.client.Header
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.client.api.MultipleContentTypesApiClient
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.client.model.CManual
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.server.model.SManual
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.server.resources.MultipleContentTypesApi
import io.github.ruedigerk.contractfirst.generator.integrationtest.spec.EmbeddedJaxRsServerSpecification
import okhttp3.logging.HttpLoggingInterceptor
import spock.lang.Subject

class MultipleContentTypesTest extends EmbeddedJaxRsServerSpecification {

  @Subject
  MultipleContentTypesApiClient apiClient = new MultipleContentTypesApiClient(apiClientSupport)

  @Override
  Class<?> getTestResource() {
    EmbeddedServerResource
  }

  def "Responses with multiple different possible content types"() {
    given:
    setLoggingInterceptorLevel(HttpLoggingInterceptor.Level.HEADERS)

    when:
    GenericResponse genericResponse = apiClient.getManualWithResponse("application/json")

    then:
    DefinedResponse response = genericResponse.asDefinedResponse()
    response.request.url == "$BASE_URL/manuals"
    response.request.method == "GET"
    response.request.headers == [
        new Header("testCaseSelector", "application/json"),
        new Header("Accept", "application/json, application/pdf; q=0.5, text/plain; q=0.5"),
        new Header("Host", HOST),
        new Header("Connection", "Keep-Alive"),
        new Header("Accept-Encoding", "gzip"),
        new Header("User-Agent", "okhttp/4.9.1"),
    ]
    response.headers == [
        new Header("Content-Type", "application/json"),
        new Header("Content-Length", "41"),
    ]
    response.statusCode == 200
    response.httpStatusMessage == "OK"
    response.contentType == "application/json"
    response.javaType == CManual.class
    response.entity == new CManual(title: "The Title", content: "Content")

    when:
    genericResponse = apiClient.getManualWithResponse("application/pdf")
    response = genericResponse.asDefinedResponse()

    then:
    response.request.url == "$BASE_URL/manuals"
    response.request.method == "GET"
    response.request.headers == [
        new Header("testCaseSelector", "application/pdf"),
        new Header("Accept", "application/json, application/pdf; q=0.5, text/plain; q=0.5"),
        new Header("Host", HOST),
        new Header("Connection", "Keep-Alive"),
        new Header("Accept-Encoding", "gzip"),
        new Header("User-Agent", "okhttp/4.9.1"),
    ]
    response.headers == [
        new Header("Content-Type", "application/pdf"),
        new Header("Transfer-Encoding", "chunked"),
    ]
    response.statusCode == 200
    response.httpStatusMessage == "OK"
    response.contentType == "application/pdf"
    response.javaType == InputStream
    (response.entity as InputStream).bytes == getClass().getResourceAsStream("/sample.pdf").bytes

    when:
    genericResponse = apiClient.getManualWithResponse("text/plain")
    response = genericResponse.asDefinedResponse()

    then:
    response.request.url == "$BASE_URL/manuals"
    response.request.method == "GET"
    response.request.headers == [
        new Header("testCaseSelector", "text/plain"),
        new Header("Accept", "application/json, application/pdf; q=0.5, text/plain; q=0.5"),
        new Header("Host", HOST),
        new Header("Connection", "Keep-Alive"),
        new Header("Accept-Encoding", "gzip"),
        new Header("User-Agent", "okhttp/4.9.1"),
    ]
    response.headers == [
        new Header("Content-Type", "text/plain"),
        new Header("Content-Length", "15"),
    ]
    response.statusCode == 202
    response.httpStatusMessage == "Accepted"
    response.contentType == "text/plain"
    response.javaType == String.class
    response.entity == "Just plain text"
  }

  /**
   * JAX-RS resource implementation used in this test.
   */
  static class EmbeddedServerResource implements MultipleContentTypesApi {

    @Override
    GetManualResponse getManual(String testCaseSelector) {
      switch (testCaseSelector) {
        case "application/pdf":
          return GetManualResponse.with200ApplicationPdf(getClass().getResourceAsStream("/sample.pdf"))
        case "text/plain":
          return GetManualResponse.with202TextPlain("Just plain text")
        default:
          return GetManualResponse.with200ApplicationJson(new SManual(title: "The Title", content: "Content"))
      }
    }
  }
}
