package io.github.ruedigerk.contractfirst.generator.integrationtest

import io.github.ruedigerk.contractfirst.generator.client.ApiResponse
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
    def expectedManual = new CManual(title: "The Title", content: "Content")

    when:
    def response = apiClient.getManual("application/json")

    then:
    response.isStatus200ReturningCManual()
    !response.isStatus200ReturningInputStream()
    !response.isStatus202ReturningString()
    response.getEntityIfCManual() == Optional.of(expectedManual)
    response.getEntityAsCManual() == expectedManual
    response.getEntityIfInputStream() == Optional.empty()
    response.getEntityAsInputStream() == null
    response.getEntityIfString() == Optional.empty()
    response.getEntityAsString() == null

    and:
    ApiResponse apiResponse = response.apiResponse
    apiResponse.request.url == "$BASE_URL/manuals"
    apiResponse.request.method == "GET"
    apiResponse.request.headers == [
        new Header("testCaseSelector", "application/json"),
        new Header("Accept", "application/json, application/pdf; q=0.5, text/plain; q=0.5"),
        new Header("Host", HOST),
        new Header("Connection", "Keep-Alive"),
        new Header("Accept-Encoding", "gzip"),
        new Header("User-Agent", "okhttp/4.9.1"),
    ]
    apiResponse.headers == [
        new Header("Content-Type", "application/json"),
        new Header("Content-Length", "41"),
    ]
    apiResponse.statusCode == 200
    apiResponse.httpStatusMessage == "OK"
    apiResponse.contentType == "application/json"
    apiResponse.entityType == CManual.class
    apiResponse.entity == expectedManual

    when:
    response = apiClient.getManual("application/pdf")
    apiResponse = response.apiResponse

    then:
    !response.isStatus200ReturningCManual()
    response.isStatus200ReturningInputStream()
    !response.isStatus202ReturningString()
    response.getEntityIfCManual() == Optional.empty()
    response.getEntityAsCManual() == null
    response.getEntityIfInputStream().isPresent()
    response.getEntityIfInputStream().get() == response.getEntityAsInputStream()
    response.getEntityAsInputStream().bytes == getClass().getResourceAsStream("/sample.pdf").bytes
    response.getEntityIfString() == Optional.empty()
    response.getEntityAsString() == null

    and:
    apiResponse.request.url == "$BASE_URL/manuals"
    apiResponse.request.method == "GET"
    apiResponse.request.headers == [
        new Header("testCaseSelector", "application/pdf"),
        new Header("Accept", "application/json, application/pdf; q=0.5, text/plain; q=0.5"),
        new Header("Host", HOST),
        new Header("Connection", "Keep-Alive"),
        new Header("Accept-Encoding", "gzip"),
        new Header("User-Agent", "okhttp/4.9.1"),
    ]
    apiResponse.headers == [
        new Header("Content-Type", "application/pdf"),
        new Header("Transfer-Encoding", "chunked"),
    ]
    apiResponse.statusCode == 200
    apiResponse.httpStatusMessage == "OK"
    apiResponse.contentType == "application/pdf"
    apiResponse.entityType == InputStream
    apiResponse.entity instanceof InputStream

    when:
    response = apiClient.getManual("text/plain")
    apiResponse = response.apiResponse

    then:
    !response.isStatus200ReturningCManual()
    !response.isStatus200ReturningInputStream()
    response.isStatus202ReturningString()
    response.getEntityIfCManual() == Optional.empty()
    response.getEntityAsCManual() == null
    response.getEntityIfInputStream() == Optional.empty()
    response.getEntityAsInputStream() == null
    response.getEntityIfString() == Optional.of("Just plain text")
    response.getEntityAsString() == "Just plain text"

    and:
    apiResponse.request.url == "$BASE_URL/manuals"
    apiResponse.request.method == "GET"
    apiResponse.request.headers == [
        new Header("testCaseSelector", "text/plain"),
        new Header("Accept", "application/json, application/pdf; q=0.5, text/plain; q=0.5"),
        new Header("Host", HOST),
        new Header("Connection", "Keep-Alive"),
        new Header("Accept-Encoding", "gzip"),
        new Header("User-Agent", "okhttp/4.9.1"),
    ]
    apiResponse.headers == [
        new Header("Content-Type", "text/plain"),
        new Header("Content-Length", "15"),
    ]
    apiResponse.statusCode == 202
    apiResponse.httpStatusMessage == "Accepted"
    apiResponse.contentType == "text/plain"
    apiResponse.entityType == String.class
    apiResponse.entity == "Just plain text"
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
