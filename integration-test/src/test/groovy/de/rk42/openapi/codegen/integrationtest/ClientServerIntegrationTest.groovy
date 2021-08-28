package de.rk42.openapi.codegen.integrationtest

import com.google.gson.reflect.TypeToken
import de.rk42.openapi.codegen.client.DefinedResponse
import de.rk42.openapi.codegen.client.GenericResponse
import de.rk42.openapi.codegen.client.Header
import de.rk42.openapi.codegen.client.RestClientSupport
import de.rk42.openapi.codegen.client.RestClientUndefinedResponseException
import de.rk42.openapi.codegen.client.UndefinedResponse
import de.rk42.openapi.codegen.integrationtest.generated.client.model.CError
import de.rk42.openapi.codegen.integrationtest.generated.client.model.CPet
import de.rk42.openapi.codegen.integrationtest.generated.client.resources.PetsApiRestClient
import de.rk42.openapi.codegen.integrationtest.generated.client.resources.RestClientCErrorEntityException
import de.rk42.openapi.codegen.integrationtest.generated.server.model.SError
import de.rk42.openapi.codegen.integrationtest.generated.server.model.SPet
import de.rk42.openapi.codegen.integrationtest.generated.server.resources.PetsApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import javax.ws.rs.core.Response

// TODO: Test-Case for mixed 200 (with entity) and 204 responses
// TODO: Test-Case for parameters and entities containing LocalDate and OffsetDateTime
// TODO: Test-Case for operations that can respond with multiple content types, e.g. application/json and application/pdf in multiple status codes and/or media-types
// TODO: Test-Case for operation responses with wildcard content type, e.g. application/*
class ClientServerIntegrationTest extends Specification {

  private static final String HOST = "localhost:17249"
  private static final String BASE_URL = "http://$HOST"

  @Shared
  private EmbeddedJaxRsServer embeddedServer = new EmbeddedJaxRsServer(BASE_URL, LocalPetsResource)
  @Shared
  private HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor({ System.out.println("CLIENT: $it") })

  CPet pet = new CPet(id: 42L, name: "name", tag: "tag")
  RestClientSupport support = new RestClientSupport(new OkHttpClient.Builder().addNetworkInterceptor(loggingInterceptor).build(), BASE_URL)

  @Subject
  PetsApiRestClient restClient = new PetsApiRestClient(support)

  def setupSpec() {
    embeddedServer.startServer()
  }

  def setup() {
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
  }

  def cleanupSpec() {
    embeddedServer.stopServer()
  }

  def "createPet: ok"() {
    when:
    def genericResponse = restClient.createPetWithResponse("petId", true, 4711L, null, pet)

    then:
    def response = genericResponse.asExpectedResponse()
    response.request.url == "$BASE_URL/petId/pets?dryRun=true"
    response.request.method == "POST"
    response.request.headers == [
        new Header("customerId", "4711"),
        new Header("Accept", "application/json"),
        new Header("Content-Type", "application/json; charset=utf-8"),
        new Header("Content-Length", "35"),
        new Header("Host", HOST),
        new Header("Connection", "Keep-Alive"),
        new Header("Accept-Encoding", "gzip"),
        new Header("User-Agent", "okhttp/4.9.1"),
    ]
    response.statusCode == 200
    response.httpStatusMessage == "OK"
    response.contentType == "application/json"
    response.javaType == CPet.class
    response.entity == pet

    when:
    // TODO: Test-case-selector!
    def createdPet = restClient.createPet("petId", true, 4711L, null, pet)

    then:
    createdPet == pet
  }

  def "createPet: unknown customer ID"() {
    when:
    def genericResponse = restClient.createPetWithResponse("petId", false, 400, null, pet)

    then:
    def response = genericResponse.asExpectedResponse()
    response.request.url == "$BASE_URL/petId/pets?dryRun=false"
    response.request.method == "POST"
    response.request.headers == [
        new Header("customerId", "400"),
        new Header("Accept", "application/json"),
        new Header("Content-Type", "application/json; charset=utf-8"),
        new Header("Content-Length", "35"),
        new Header("Host", HOST),
        new Header("Connection", "Keep-Alive"),
        new Header("Accept-Encoding", "gzip"),
        new Header("User-Agent", "okhttp/4.9.1"),
    ]
    response.statusCode == 400
    response.httpStatusMessage == "Bad Request"
    response.contentType == "application/json"
    response.javaType == CError.class
    response.entity == new CError(code: 400, message: "Unknown customer id: 400")

    when:
    restClient.createPet("petId", false, 400, null, pet)

    then:
    def e = thrown RestClientCErrorEntityException
    e.httpStatusCode == 400
    e.entity == new CError(code: 400, message: "Unknown customer id: 400")
  }

  def "createPet: internal server error"() {
    when:
    def genericResponse = restClient.createPetWithResponse("petId", false, 500, null, pet)

    then:
    def response = genericResponse.asExpectedResponse()
    response.request.url == "$BASE_URL/petId/pets?dryRun=false"
    response.request.method == "POST"
    response.request.headers == [
        new Header("customerId", "500"),
        new Header("Accept", "application/json"),
        new Header("Content-Type", "application/json; charset=utf-8"),
        new Header("Content-Length", "35"),
        new Header("Host", HOST),
        new Header("Connection", "Keep-Alive"),
        new Header("Accept-Encoding", "gzip"),
        new Header("User-Agent", "okhttp/4.9.1"),
    ]
    response.statusCode == 500
    response.httpStatusMessage == "Internal Server Error"
    response.contentType == "application/json"
    response.javaType == CError.class
    response.entity == new CError(code: 500, message: "Internal Server Error :(")

    when:
    restClient.createPet("petId", false, 500, null, pet)

    then:
    def e = thrown RestClientCErrorEntityException
    e.httpStatusCode == 500
    e.entity == new CError(code: 500, message: "Internal Server Error :(")
  }

  def "createPet: undefined response"() {
    when:
    def genericResponse = restClient.createPetWithResponse("petId", false, 999, null, pet)

    then:
    def response = genericResponse as UndefinedResponse
    response.request.url == "$BASE_URL/petId/pets?dryRun=false"
    response.request.method == "POST"
    response.request.headers == [
        new Header("customerId", "999"),
        new Header("Accept", "application/json"),
        new Header("Content-Type", "application/json; charset=utf-8"),
        new Header("Content-Length", "35"),
        new Header("Host", HOST),
        new Header("Connection", "Keep-Alive"),
        new Header("Accept-Encoding", "gzip"),
        new Header("User-Agent", "okhttp/4.9.1"),
    ]
    response.statusCode == 500
    response.httpStatusMessage == "Internal Server Error"
    response.contentType == "text/plain"
    response.bodyContent == "This is just plain text"
    response.cause == null

    when:
    restClient.createPet("petId", false, 999, null, pet)

    then:
    def e = thrown RestClientUndefinedResponseException
    e.response.request.url == "$BASE_URL/petId/pets?dryRun=false"
    e.response.request.method == "POST"
    e.response.request.headers == [
        new Header("customerId", "999"),
        new Header("Accept", "application/json"),
        new Header("Content-Type", "application/json; charset=utf-8"),
        new Header("Content-Length", "35"),
        new Header("Host", HOST),
        new Header("Connection", "Keep-Alive"),
        new Header("Accept-Encoding", "gzip"),
        new Header("User-Agent", "okhttp/4.9.1"),
    ]
    e.response.statusCode == 500
    e.response.httpStatusMessage == "Internal Server Error"
    e.response.contentType == "text/plain"
    e.response.bodyContent == "This is just plain text"
    e.response.cause == null
  }

  def "Request and response bodies of generic type, like List"() {
    when:
    def responsePets = restClient.filterPets(
        [
            new CPet(id: 1L, name: "Buddy"),
            new CPet(id: 2L, name: "Cleopatra"),
            new CPet(id: 3L, name: "Snoopy"),
        ]
    )

    then:
    responsePets == [
        new CPet(id: 1L, name: "Buddy"),
        new CPet(id: 3L, name: "Snoopy"),
    ]

    when:
    GenericResponse genericResponse = restClient.filterPetsWithResponse(
        [
            new CPet(id: 1L, name: "Buddy"),
            new CPet(id: 2L, name: "Cleopatra"),
            new CPet(id: 3L, name: "Snoopy"),
        ]
    )

    then:
    DefinedResponse response = genericResponse.asExpectedResponse()
    response.request.url == "$BASE_URL/pets"
    response.request.method == "POST"
    response.statusCode == 200
    response.httpStatusMessage == "OK"
    response.contentType == "application/json"
    response.javaType == new TypeToken<List<CPet>>() {
    }.getType()
    response.entity == [
        new CPet(id: 1L, name: "Buddy"),
        new CPet(id: 3L, name: "Snoopy"),
    ]
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

  def "Request and response bodies of type InputStream"() {
    given:
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS)
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

  /**
   * JAX-RS resource implementation used in this test.
   */
  static class LocalPetsResource implements PetsApi {

    @Override
    CreatePetResponse createPet(String petStoreId, Boolean dryRun, Long customerId, String testCaseSelector, SPet requestBody) {
      println("SERVER: createPet, petStoreId: $petStoreId, dryRun: $dryRun, customerId: $customerId, body: $requestBody")

      if (dryRun) {
        return CreatePetResponse.with200ApplicationJson(requestBody)
      }

      if (customerId == 400) {
        return CreatePetResponse.with400ApplicationJson(new SError().code(400).message("Unknown customer id: " + customerId))
      } else if (customerId == 500) {
        return CreatePetResponse.withApplicationJson(500, new SError().code(500).message("Internal Server Error :("))
      } else {
        return CreatePetResponse.withCustomResponse(
            Response.serverError()
                .entity("This is just plain text")
                .header("Content-Type", "text/plain")
                .status(500)
                .build()
        )
      }
    }

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
