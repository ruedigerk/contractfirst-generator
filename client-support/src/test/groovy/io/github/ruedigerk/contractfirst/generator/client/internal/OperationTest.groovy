package io.github.ruedigerk.contractfirst.generator.client.internal

import spock.lang.Specification
import spock.lang.Unroll

class OperationTest extends Specification {

  def "determineAcceptHeaderValue with no responses"() {
    given:
    Operation.Builder builder = new Operation.Builder("/path", "GET")
    def operation = builder.build()

    expect:
    operation.determineAcceptHeaderValue() == ""
  }

  def "determineAcceptHeaderValue with simple responses"() {
    given:
    Operation.Builder builder = new Operation.Builder("/path", "GET")
    builder.response(StatusCode.of(200), "application/json", Model)
    def operation = builder.build()

    expect:
    operation.determineAcceptHeaderValue() == "application/json"
  }

  def "determineAcceptHeaderValue with complex responses"() {
    given:
    Operation.Builder builder = new Operation.Builder("/path", "GET")
    builder.response(StatusCode.of(200), "application/json", Model)
    builder.response(StatusCode.of(200), "application/pdf", InputStream)
    builder.response(StatusCode.of(201), "application/json", Model)
    builder.response(StatusCode.of(201), "text/plain", String)
    builder.response(StatusCode.of(202), "application/*", Model)
    builder.response(StatusCode.of(203), "*/*", Model)
    builder.response(StatusCode.of(204))
    builder.response(StatusCode.DEFAULT, "application/json", DefaultError)
    def operation = builder.build()

    expect:
    operation.determineAcceptHeaderValue() == "application/json, application/pdf; q=0.5, text/plain; q=0.5, application/*; q=0.5, */*; q=0.5"
  }

  @Unroll
  def "determineTypeOfContent #statusCode #contentType"() {
    given:
    Operation.Builder builder = new Operation.Builder("/path", "GET")
    builder.response(StatusCode.of(200), "application/json", Model)
    builder.response(StatusCode.of(200), "application/pdf", InputStream)
    builder.response(StatusCode.of(201), "text/plain", String)
    builder.response(StatusCode.of(202), "application/*", Model)
    builder.response(StatusCode.of(203), "*/*", Model)
    builder.response(StatusCode.of(204))
    builder.response(StatusCode.DEFAULT, "application/json", DefaultError)
    def operation = builder.build()

    expect:
    operation.determineMatchingResponseType(statusCode, contentType) == expectedType

    where:
    statusCode | contentType                                               | expectedType
    200        | "application/json"                                        | Model
    200        | "application/json; charset=UTF-8"                         | Model
    200        | "application/json;Charset=UTF-8; whatever=\"some value\"" | Model
    200        | "application/pdf"                                         | InputStream
    200        | "text/plain"                                              | null
    200        | "nonsense"                                                | null
    200        | null                                                      | null
    201        | "text/plain"                                              | String
    201        | "application/json"                                        | null
    201        | "nonsense"                                                | null
    201        | null                                                      | null
    202        | "application/json"                                        | Model
    202        | "application/json; charset=UTF-8"                         | Model
    202        | "application/json;Charset=UTF-8; whatever=\"some value\"" | Model
    202        | "application/pdf"                                         | Model
    202        | "text/plain"                                              | null
    202        | "nonsense"                                                | null
    202        | null                                                      | null
    203        | "application/json"                                        | Model
    203        | "application/json; charset=UTF-8"                         | Model
    203        | "application/json;Charset=UTF-8; whatever=\"some value\"" | Model
    203        | "application/pdf"                                         | Model
    203        | "text/plain"                                              | Model
    203        | "nonsense"                                                | Model
    203        | null                                                      | null
    204        | "application/json"                                        | null
    204        | null                                                      | Void.TYPE
    500        | "application/json"                                        | DefaultError
    500        | "text/plain"                                              | null
    500        | null                                                      | null
  }

  @Unroll
  def "determineTypeOfContent with invalid content type in definition #statusCode #contentType"() {
    given:
    Operation.Builder builder = new Operation.Builder("/path", "GET")
    builder.response(StatusCode.of(200), "nonsense", Model)
    builder.response(StatusCode.DEFAULT, "application/json", DefaultError)
    def operation = builder.build()

    expect:
    operation.determineMatchingResponseType(statusCode, contentType) == expectedType

    where:
    statusCode | contentType        | expectedType
    200        | "application/json" | null
    200        | "text/plain"       | null
    200        | "nonsense"         | null
    200        | null               | null
    500        | "application/json" | DefaultError
    500        | "text/plain"       | null
    500        | "nonsense"         | null
    500        | null               | null
  }

  static class Model {
  }

  static class DefaultError {
  }
}
