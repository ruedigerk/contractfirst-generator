package org.contractfirst.generator.integrationtest

import org.contractfirst.generator.client.ApiClientUndefinedResponseException
import org.contractfirst.generator.client.Header
import org.contractfirst.generator.client.UndefinedResponse
import org.contractfirst.generator.integrationtest.generated.client.api.ResponseVariantsApiClient
import org.contractfirst.generator.integrationtest.generated.client.api.RestClientCErrorEntityException
import org.contractfirst.generator.integrationtest.generated.client.model.CError
import org.contractfirst.generator.integrationtest.generated.client.model.CPet
import org.contractfirst.generator.integrationtest.generated.server.model.SError
import org.contractfirst.generator.integrationtest.generated.server.model.SPet
import org.contractfirst.generator.integrationtest.generated.server.resources.ResponseVariantsApi
import org.contractfirst.generator.integrationtest.spec.EmbeddedJaxRsServerSpecification
import spock.lang.Subject

import javax.ws.rs.core.Response

/**
 * Tests different variants of responses that a server can respond with.
 */
class ResponseVariantsTest extends EmbeddedJaxRsServerSpecification {

  CPet pet = new CPet(id: 42L, name: "name", tag: "tag")

  @Subject
  ResponseVariantsApiClient restClient = new ResponseVariantsApiClient(restClientSupport)

  @Override
  Class<?> getTestResource() {
    EmbeddedServerResource
  }

  def "Server response with explicitly defined 200"() {
    when:
    def genericResponse = restClient.createPetWithResponse("petId", true, 4711L, null, pet)

    then:
    def response = genericResponse.asDefinedResponse()
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
    def createdPet = restClient.createPet("petId", true, 4711L, null, pet)

    then:
    createdPet == pet
  }

  def "Server response with explicitly defined 201"() {
    when:
    def genericResponse = restClient.createPetWithResponse("petId", true, 4711L, "201", pet)

    then:
    def response = genericResponse.asDefinedResponse()
    response.request.url == "$BASE_URL/petId/pets?dryRun=true"
    response.request.method == "POST"
    response.request.headers == [
        new Header("customerId", "4711"),
        new Header("testCaseSelector", "201"),
        new Header("Accept", "application/json"),
        new Header("Content-Type", "application/json; charset=utf-8"),
        new Header("Content-Length", "35"),
        new Header("Host", HOST),
        new Header("Connection", "Keep-Alive"),
        new Header("Accept-Encoding", "gzip"),
        new Header("User-Agent", "okhttp/4.9.1"),
    ]
    response.statusCode == 201
    response.httpStatusMessage == "Created"
    response.contentType == null
    response.javaType == Void.TYPE
    response.entity == null

    when:
    def responseEntity = restClient.createPet("petId", true, 4711L, "201", pet)

    then:
    responseEntity == null
  }

  def "Server response with explicitly defined 204"() {
    when:
    def genericResponse = restClient.createPetWithResponse("petId", true, 4711L, "204", pet)

    then:
    def response = genericResponse.asDefinedResponse()
    response.request.url == "$BASE_URL/petId/pets?dryRun=true"
    response.request.method == "POST"
    response.request.headers == [
        new Header("customerId", "4711"),
        new Header("testCaseSelector", "204"),
        new Header("Accept", "application/json"),
        new Header("Content-Type", "application/json; charset=utf-8"),
        new Header("Content-Length", "35"),
        new Header("Host", HOST),
        new Header("Connection", "Keep-Alive"),
        new Header("Accept-Encoding", "gzip"),
        new Header("User-Agent", "okhttp/4.9.1"),
    ]
    response.statusCode == 204
    response.httpStatusMessage == "No Content"
    response.contentType == null
    response.javaType == Void.TYPE
    response.entity == null

    when:
    def responseEntity = restClient.createPet("petId", true, 4711L, "204", pet)

    then:
    responseEntity == null
  }

  def "Server responds with explicitly defined 400"() {
    when:
    def genericResponse = restClient.createPetWithResponse("petId", false, 23, "400", pet)

    then:
    def response = genericResponse.asDefinedResponse()
    response.request.url == "$BASE_URL/petId/pets?dryRun=false"
    response.request.method == "POST"
    response.request.headers == [
        new Header("customerId", "23"),
        new Header("testCaseSelector", "400"),
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
    response.entity == new CError(code: 400, message: "Unknown customer id: 23")

    when:
    restClient.createPet("petId", false, 23, "400", pet)

    then:
    def e = thrown RestClientCErrorEntityException
    e.httpStatusCode == 400
    e.entity == new CError(code: 400, message: "Unknown customer id: 23")
  }

  def "Server responds with 500, covered by default"() {
    when:
    def genericResponse = restClient.createPetWithResponse("petId", false, 42, "500", pet)

    then:
    def response = genericResponse.asDefinedResponse()
    response.request.url == "$BASE_URL/petId/pets?dryRun=false"
    response.request.method == "POST"
    response.request.headers == [
        new Header("customerId", "42"),
        new Header("testCaseSelector", "500"),
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
    restClient.createPet("petId", false, 42, "500", pet)

    then:
    def e = thrown RestClientCErrorEntityException
    e.httpStatusCode == 500
    e.entity == new CError(code: 500, message: "Internal Server Error :(")
  }

  def "Server responds with response not conforming to the contract"() {
    when:
    def genericResponse = restClient.createPetWithResponse("petId", false, 999, "undefined", pet)

    then:
    def response = genericResponse as UndefinedResponse
    response.request.url == "$BASE_URL/petId/pets?dryRun=false"
    response.request.method == "POST"
    response.request.headers == [
        new Header("customerId", "999"),
        new Header("testCaseSelector", "undefined"),
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
    restClient.createPet("petId", false, 999, "undefined", pet)

    then:
    def e = thrown ApiClientUndefinedResponseException
    e.response.request.url == "$BASE_URL/petId/pets?dryRun=false"
    e.response.request.method == "POST"
    e.response.request.headers == [
        new Header("customerId", "999"),
        new Header("testCaseSelector", "undefined"),
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

  /**
   * JAX-RS resource implementation used in this test.
   */
  static class EmbeddedServerResource implements ResponseVariantsApi {

    @Override
    CreatePetResponse createPet(String petStoreId, Boolean dryRun, Long customerId, String testCaseSelector, SPet requestBody) {
      log("SERVER: createPet, testCaseSelector: $testCaseSelector, petStoreId: $petStoreId, dryRun: $dryRun, customerId: $customerId, body: $requestBody")

      if (testCaseSelector == "201") {
        return CreatePetResponse.with201()
      } else if (testCaseSelector == "204") {
        return CreatePetResponse.with204()
      } else if (testCaseSelector == "400") {
        return CreatePetResponse.with400ApplicationJson(new SError().code(400).message("Unknown customer id: " + customerId))
      } else if (testCaseSelector == "500") {
        return CreatePetResponse.withApplicationJson(500, new SError().code(500).message("Internal Server Error :("))
      } else if (testCaseSelector == "undefined") {
        return CreatePetResponse.withCustomResponse(Response.serverError().type("text/plain").entity("This is just plain text").build())
      } else {
        return CreatePetResponse.with200ApplicationJson(requestBody)
      }
    }
  }
}
