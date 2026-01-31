package io.github.ruedigerk.contractfirst.generator.integrationtest.spring

import io.github.ruedigerk.contractfirst.generator.client.ApiResponse
import io.github.ruedigerk.contractfirst.generator.client.Header
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.client.api.MultipleContentTypesApiClient
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.client.model.CManual
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.server.model.SManual
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.server.resources.MultipleContentTypesApi
import io.github.ruedigerk.contractfirst.generator.integrationtest.spring.spec.SpringWebIntegrationSpecification
import okhttp3.logging.HttpLoggingInterceptor
import org.springframework.core.io.ClassPathResource
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.bind.annotation.RestController
import spock.lang.Subject

@ContextConfiguration(classes = EmbeddedRestController)
class MultipleContentTypesTest extends SpringWebIntegrationSpecification {

  @Subject
  MultipleContentTypesApiClient apiClient = new MultipleContentTypesApiClient(apiRequestExecutor)

  def "Responses with multiple different possible content types"() {
    given:
    setLoggingInterceptorLevel(HttpLoggingInterceptor.Level.HEADERS)
    def expectedManual = new CManual(title: "The Title", content: "Content")

    when:
    def result = apiClient.getManual("application/json")

    then:
    result.isStatus200ReturningCManual()
    !result.isStatus200ReturningInputStream()
    !result.isStatus202ReturningString()
    result.getEntityIfCManual() == Optional.of(expectedManual)
    result.getEntityAsCManual() == expectedManual
    result.getEntityIfInputStream() == Optional.empty()
    result.getEntityAsInputStream() == null
    result.getEntityIfString() == Optional.empty()
    result.getEntityAsString() == null

    and:
    ApiResponse response = result.response
    response.request.url == "$BASE_URL/manuals"
    response.request.method == "GET"
    response.request.headers == [
        new Header("testCaseSelector", "application/json"),
        new Header("Accept", "application/json, application/pdf; q=0.5, text/plain; q=0.5"),
        new Header("Host", HOST),
        new Header("Connection", "Keep-Alive"),
        new Header("Accept-Encoding", "gzip"),
        new Header("User-Agent", "okhttp/5.3.2"),
    ]
    response.headers == [
        new Header("Content-Type", "application/json"),
        new Header("Content-Length", "41"),
    ]
    response.statusCode == 200
    response.httpStatusMessage == "OK"
    response.contentType == "application/json"
    response.entityType == CManual.class
    response.entity == expectedManual

    when:
    result = apiClient.getManual("application/pdf")
    response = result.response

    then:
    !result.isStatus200ReturningCManual()
    result.isStatus200ReturningInputStream()
    !result.isStatus202ReturningString()
    result.getEntityIfCManual() == Optional.empty()
    result.getEntityAsCManual() == null
    result.getEntityIfInputStream().isPresent()
    result.getEntityIfInputStream().get() == result.getEntityAsInputStream()
    result.getEntityAsInputStream().bytes == getClass().getResourceAsStream("/sample.pdf").bytes
    result.getEntityIfString() == Optional.empty()
    result.getEntityAsString() == null

    and:
    response.request.url == "$BASE_URL/manuals"
    response.request.method == "GET"
    response.request.headers == [
        new Header("testCaseSelector", "application/pdf"),
        new Header("Accept", "application/json, application/pdf; q=0.5, text/plain; q=0.5"),
        new Header("Host", HOST),
        new Header("Connection", "Keep-Alive"),
        new Header("Accept-Encoding", "gzip"),
        new Header("User-Agent", "okhttp/5.3.2"),
    ]
    response.headers == [
        new Header("Content-Type", "application/pdf"),
        new Header("Transfer-Encoding", "chunked"),
    ]
    response.statusCode == 200
    response.httpStatusMessage == "OK"
    response.contentType == "application/pdf"
    response.entity instanceof InputStream
    response.entityType == InputStream

    when:
    result = apiClient.getManual("text/plain")
    response = result.response

    then:
    !result.isStatus200ReturningCManual()
    !result.isStatus200ReturningInputStream()
    result.isStatus202ReturningString()
    result.getEntityIfCManual() == Optional.empty()
    result.getEntityAsCManual() == null
    result.getEntityIfInputStream() == Optional.empty()
    result.getEntityAsInputStream() == null
    result.getEntityIfString() == Optional.of("Just plain text")
    result.getEntityAsString() == "Just plain text"

    and:
    response.request.url == "$BASE_URL/manuals"
    response.request.method == "GET"
    response.request.headers == [
        new Header("testCaseSelector", "text/plain"),
        new Header("Accept", "application/json, application/pdf; q=0.5, text/plain; q=0.5"),
        new Header("Host", HOST),
        new Header("Connection", "Keep-Alive"),
        new Header("Accept-Encoding", "gzip"),
        new Header("User-Agent", "okhttp/5.3.2"),
    ]
    response.headers == [
        new Header("Content-Type", "text/plain"),
        new Header("Content-Length", "15"),
    ]
    response.statusCode == 202
    response.httpStatusMessage == "Accepted"
    response.contentType == "text/plain"
    response.entityType == String.class
    response.entity == "Just plain text"
  }

  /**
   * Spring REST controller used in this test.
   */
  @RestController
  static class EmbeddedRestController implements MultipleContentTypesApi {

    @Override
    GetManualResponse getManual(String testCaseSelector) {
      switch (testCaseSelector) {
        case "application/pdf":
          return GetManualResponse.with200ApplicationPdf(new ClassPathResource("/sample.pdf"))
        case "text/plain":
          return GetManualResponse.with202TextPlain("Just plain text")
        default:
          return GetManualResponse.with200ApplicationJson(new SManual(title: "The Title", content: "Content"))
      }
    }
  }
}
