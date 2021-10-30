package io.github.ruedigerk.contractfirst.generator.integrationtest

import com.google.gson.reflect.TypeToken
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.client.api.PayloadVariantsApiClient
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.client.model.CItem
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.server.model.SItem
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.server.resources.PayloadVariantsApi
import io.github.ruedigerk.contractfirst.generator.integrationtest.spec.EmbeddedJaxRsServerSpecification
import okhttp3.logging.HttpLoggingInterceptor
import spock.lang.Subject

/**
 * Tests for various request and response payloads.
 */
class PayloadVariantsTest extends EmbeddedJaxRsServerSpecification {

  @Subject
  PayloadVariantsApiClient apiClient = new PayloadVariantsApiClient(apiClientSupport)

  @Override
  Class<?> getTestResource() {
    EmbeddedServerResource
  }

  def "Response without body (status 204)"() {
    when:
    apiClient.changeItem(new CItem(id: 1L, name: "Buddy", tag: "new tag"))

    then:
    noExceptionThrown()

    when:
    def result = apiClient.returningResult().changeItem(new CItem(id: 1L, name: "Buddy", tag: "new tag"))
    def response = result.response

    then:
    result.isSuccessful()
    result.isStatus204WithoutEntity()
    
    and:
    response.request.url == "$BASE_URL/itemBinaries"
    response.request.method == "POST"
    response.statusCode == 204
    response.httpStatusMessage == "No Content"
    response.contentType == null
    response.entityType == Void.TYPE
    response.entity == null
  }

  def "Request and response bodies of generic type, like List"() {
    given:
    def input = [item(1, "Buddy"), item(2, "Cleopatra"), item(3, "Snoopy")]
    def expectedOutput = [item(1, "Buddy"), item(3, "Snoopy")]

    when:
    def responseItems = apiClient.filterItems(input)

    then:
    responseItems == expectedOutput

    when:
    def result = apiClient.returningResult().filterItems(input)
    def response = result.response

    then:
    result.isStatus200ReturningListOfCItem()
    result.entityAsListOfCItem == expectedOutput
    
    and:
    response.request.url == "$BASE_URL/items"
    response.request.method == "POST"
    response.statusCode == 200
    response.httpStatusMessage == "OK"
    response.contentType == "application/json"
    response.entityType == new TypeToken<List<CItem>>() {
    }.getType()
    response.entity == expectedOutput
  }

  def "Request and response bodies of type InputStream"() {
    given:
    setLoggingInterceptorLevel(HttpLoggingInterceptor.Level.HEADERS)
    byte[] pdfBytes = getClass().getResourceAsStream("/sample.pdf").bytes

    when:
    InputStream responseInputStream = apiClient.uploadAndReturnBinary(new ByteArrayInputStream(pdfBytes))

    then:
    byte[] responseBytes = responseInputStream.bytes
    responseBytes == pdfBytes

    when:
    def result = apiClient.returningResult().uploadAndReturnBinary(new ByteArrayInputStream(pdfBytes))
    def response = result.response

    then:
    result.isStatus200ReturningInputStream()
    
    and:
    response.request.url == "$BASE_URL/itemBinaries"
    response.request.method == "PUT"
    response.statusCode == 200
    response.httpStatusMessage == "OK"
    response.contentType == "application/octet-stream"
    response.entityType == InputStream.class
    (response.entity as InputStream).bytes == pdfBytes
  }

  private static CItem item(long id, String name) {
    new CItem(id: id, name: name)
  }

  /**
   * JAX-RS resource implementation used in this test.
   */
  static class EmbeddedServerResource implements PayloadVariantsApi {

    @Override
    ChangeItemResponse changeItem(SItem requestBody) {
      return ChangeItemResponse.with204()
    }

    @Override
    FilterItemsResponse filterItems(List<SItem> requestBody) {
      return FilterItemsResponse.with200ApplicationJson(requestBody.findAll { it.id % 2 != 0 })
    }

    @Override
    UploadAndReturnBinaryResponse uploadAndReturnBinary(InputStream requestBody) {
      return UploadAndReturnBinaryResponse.with200ApplicationOctetStream(requestBody)
    }
  }
}
