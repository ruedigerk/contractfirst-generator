package io.github.ruedigerk.contractfirst.generator.integrationtest

import io.github.ruedigerk.contractfirst.generator.client.ApiClientUndefinedResponseException
import io.github.ruedigerk.contractfirst.generator.client.Header
import io.github.ruedigerk.contractfirst.generator.client.UndefinedResponse
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.client.api.ResponseVariantsApiClient
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.client.api.RestClientCFailureEntityException
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.client.model.CFailure
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.client.model.CItem
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.server.model.SFailure
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.server.model.SItem
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.server.resources.ResponseVariantsApi
import io.github.ruedigerk.contractfirst.generator.integrationtest.spec.EmbeddedJaxRsServerSpecification
import spock.lang.Subject

import javax.ws.rs.core.Response

/**
 * Tests different variants of responses that a server can respond with.
 */
class ResponseVariantsTest extends io.github.ruedigerk.contractfirst.generator.integrationtest.spec.EmbeddedJaxRsServerSpecification {

  CItem item = new CItem(id: 42L, name: "name", tag: "tag")

  @Subject
  ResponseVariantsApiClient restClient = new ResponseVariantsApiClient(restClientSupport)

  @Override
  Class<?> getTestResource() {
    EmbeddedServerResource
  }

  def "Server response with explicitly defined 200"() {
    when:
    def genericResponse = restClient.createItemWithResponse("systemId", true, 4711L, null, item)

    then:
    def response = genericResponse.asDefinedResponse()
    response.request.url == "$BASE_URL/systemId/components?dryRun=true"
    response.request.method == "POST"
    response.request.headers == [
        new Header("partNumber", "4711"),
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
    response.javaType == CItem.class
    response.entity == item

    when:
    def createdItem = restClient.createItem("systemId", true, 4711L, null, item)

    then:
    createdItem == item
  }

  def "Server response with explicitly defined 201"() {
    when:
    def genericResponse = restClient.createItemWithResponse("systemId", true, 4711L, "201", item)

    then:
    def response = genericResponse.asDefinedResponse()
    response.request.url == "$BASE_URL/systemId/components?dryRun=true"
    response.request.method == "POST"
    response.request.headers == [
        new Header("partNumber", "4711"),
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
    def responseEntity = restClient.createItem("systemId", true, 4711L, "201", item)

    then:
    responseEntity == null
  }

  def "Server response with explicitly defined 204"() {
    when:
    def genericResponse = restClient.createItemWithResponse("systemId", true, 4711L, "204", item)

    then:
    def response = genericResponse.asDefinedResponse()
    response.request.url == "$BASE_URL/systemId/components?dryRun=true"
    response.request.method == "POST"
    response.request.headers == [
        new Header("partNumber", "4711"),
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
    def responseEntity = restClient.createItem("systemId", true, 4711L, "204", item)

    then:
    responseEntity == null
  }

  def "Server responds with explicitly defined 400"() {
    when:
    def genericResponse = restClient.createItemWithResponse("systemId", false, 23, "400", item)

    then:
    def response = genericResponse.asDefinedResponse()
    response.request.url == "$BASE_URL/systemId/components?dryRun=false"
    response.request.method == "POST"
    response.request.headers == [
        new Header("partNumber", "23"),
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
    response.javaType == CFailure.class
    response.entity == new CFailure(code: 400, message: "Unknown customer id: 23")

    when:
    restClient.createItem("systemId", false, 23, "400", item)

    then:
    def e = thrown RestClientCFailureEntityException
    e.httpStatusCode == 400
    e.entity == new CFailure(code: 400, message: "Unknown customer id: 23")
  }

  def "Server responds with 500, covered by default"() {
    when:
    def genericResponse = restClient.createItemWithResponse("systemId", false, 42, "500", item)

    then:
    def response = genericResponse.asDefinedResponse()
    response.request.url == "$BASE_URL/systemId/components?dryRun=false"
    response.request.method == "POST"
    response.request.headers == [
        new Header("partNumber", "42"),
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
    response.javaType == CFailure.class
    response.entity == new CFailure(code: 500, message: "Internal Server Error :(")

    when:
    restClient.createItem("systemId", false, 42, "500", item)

    then:
    def e = thrown RestClientCFailureEntityException
    e.httpStatusCode == 500
    e.entity == new CFailure(code: 500, message: "Internal Server Error :(")
  }

  def "Server responds with response not conforming to the contract"() {
    when:
    def genericResponse = restClient.createItemWithResponse("systemId", false, 999, "undefined", item)

    then:
    def response = genericResponse as io.github.ruedigerk.contractfirst.generator.client.UndefinedResponse
    response.request.url == "$BASE_URL/systemId/components?dryRun=false"
    response.request.method == "POST"
    response.request.headers == [
        new Header("partNumber", "999"),
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
    restClient.createItem("systemId", false, 999, "undefined", item)

    then:
    def e = thrown io.github.ruedigerk.contractfirst.generator.client.ApiClientUndefinedResponseException
    e.response.request.url == "$BASE_URL/systemId/components?dryRun=false"
    e.response.request.method == "POST"
    e.response.request.headers == [
        new Header("partNumber", "999"),
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
    CreateItemResponse createItem(String systemId, Boolean dryRun, Long partNumber, String testCaseSelector, SItem requestBody) {
      log("SERVER: createItem, testCaseSelector: $testCaseSelector, systemId: $systemId, dryRun: $dryRun, partNumber: $partNumber, body: $requestBody")

      if (testCaseSelector == "201") {
        return CreateItemResponse.with201()
      } else if (testCaseSelector == "204") {
        return CreateItemResponse.with204()
      } else if (testCaseSelector == "400") {
        return CreateItemResponse.with400ApplicationJson(new SFailure().code(400).message("Unknown customer id: " + partNumber))
      } else if (testCaseSelector == "500") {
        return CreateItemResponse.withApplicationJson(500, new SFailure().code(500).message("Internal Server Error :("))
      } else if (testCaseSelector == "undefined") {
        return CreateItemResponse.withCustomResponse(Response.serverError().type("text/plain").entity("This is just plain text").build())
      } else {
        return CreateItemResponse.with200ApplicationJson(requestBody)
      }
    }
  }
}
