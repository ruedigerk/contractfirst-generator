package de.rk42.openapi.codegen.integrationtest

import com.google.gson.reflect.TypeToken
import de.rk42.openapi.codegen.client.DefinedResponse
import de.rk42.openapi.codegen.client.GenericResponse
import de.rk42.openapi.codegen.integrationtest.generated.client.model.CPet
import de.rk42.openapi.codegen.integrationtest.generated.client.resources.PetsApiRestClient
import de.rk42.openapi.codegen.integrationtest.generated.server.model.SPet
import de.rk42.openapi.codegen.integrationtest.generated.server.resources.PetsApi
import okhttp3.logging.HttpLoggingInterceptor
import spock.lang.Subject

// TODO: Test-Case for parameters and entities containing LocalDate and OffsetDateTime
// TODO: Test-Case for operations that can respond with multiple content types, e.g. application/json and application/pdf in multiple status codes and/or media-types
// TODO: Test-Case for operation responses with wildcard content type, e.g. application/*
// TODO: Test-Case for Accept header with non-JSON types
class PayloadVariantsTest extends EmbeddedJaxRsServerSpecification {

  @Subject
  PetsApiRestClient restClient = new PetsApiRestClient(restClientSupport)

  @Override
  Class<?> getTestResource() {
    EmbeddedServerResource
  }

  def "Response without body (status 204)"() {
    when:
    restClient.changePet(new CPet(id: 1L, name: "Buddy", tag: "new tag"))

    then:
    noExceptionThrown()

    when:
    GenericResponse genericResponse = restClient.changePetWithResponse(new CPet(id: 1L, name: "Buddy", tag: "new tag"))

    then:
    DefinedResponse response = genericResponse.asExpectedResponse()
    response.request.url == "$BASE_URL/pets"
    response.request.method == "PUT"
    response.statusCode == 204
    response.httpStatusMessage == "No Content"
    response.contentType == null
    response.javaType == Void.TYPE
    response.entity == null
  }

  def "Request and response bodies of generic type, like List"() {
    given:
    def input = [pet(1, "Buddy"), pet(2, "Cleopatra"), pet(3, "Snoopy")]
    def expectedOutput = [pet(1, "Buddy"), pet(3, "Snoopy")]

    when:
    def responsePets = restClient.filterPets(input)

    then:
    responsePets == expectedOutput

    when:
    GenericResponse genericResponse = restClient.filterPetsWithResponse(input)

    then:
    DefinedResponse response = genericResponse.asExpectedResponse()
    response.request.url == "$BASE_URL/pets"
    response.request.method == "POST"
    response.statusCode == 200
    response.httpStatusMessage == "OK"
    response.contentType == "application/json"
    response.javaType == new TypeToken<List<CPet>>() {
    }.getType()
    response.entity == expectedOutput
  }

  def "Request and response bodies of type InputStream"() {
    given:
    setLoggingInterceptorLevel(HttpLoggingInterceptor.Level.HEADERS)
    byte[] pdfBytes = getClass().getResourceAsStream("/sample.pdf").bytes

    when:
    InputStream responseInputStream = restClient.uploadAndReturnBinary(new ByteArrayInputStream(pdfBytes))

    then:
    byte[] responseBytes = responseInputStream.bytes
    responseBytes == pdfBytes

    when:
    GenericResponse genericResponse = restClient.uploadAndReturnBinaryWithResponse(new ByteArrayInputStream(pdfBytes))

    then:
    DefinedResponse response = genericResponse.asExpectedResponse()
    response.request.url == "$BASE_URL/manuals"
    response.request.method == "PUT"
    response.statusCode == 200
    response.httpStatusMessage == "OK"
    response.contentType == "application/octet-stream"
    response.javaType == InputStream.class
    (response.entity as InputStream).bytes == pdfBytes
  }

  private static CPet pet(long id, String name) {
    new CPet(id: id, name: name)
  }

  /**
   * JAX-RS resource implementation used in this test.
   */
  static class EmbeddedServerResource implements PetsApi {

    @Override
    ChangePetResponse changePet(SPet requestBody) {
      return ChangePetResponse.with204()
    }

    @Override
    FilterPetsResponse filterPets(List<SPet> requestBody) {
      return FilterPetsResponse.with200ApplicationJson(requestBody.findAll { it.id % 2 != 0 })
    }

    @Override
    GetManualResponse getManual() {
      return null
    }

    @Override
    UploadAndReturnBinaryResponse uploadAndReturnBinary(InputStream requestBody) {
      return UploadAndReturnBinaryResponse.with200ApplicationOctetStream(requestBody)
    }

    @Override
    PostManualResponse postManual(String testCaseSelector) {
      return null
    }
  }
}
