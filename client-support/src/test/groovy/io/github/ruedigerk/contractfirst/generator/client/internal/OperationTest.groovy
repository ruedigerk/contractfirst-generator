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
    builder.response(StatusCode.of(1), "application/json", Model)
    def operation = builder.build()

    expect:
    operation.determineAcceptHeaderValue() == "application/json"
  }

  def "determineAcceptHeaderValue with complex responses"() {
    given:
    Operation.Builder builder = new Operation.Builder("/path", "GET")
    builder.response(StatusCode.of(1), "application/json", Model)
    builder.response(StatusCode.of(1), "application/pdf", InputStream)
    builder.response(StatusCode.of(2), "application/json", Model)
    builder.response(StatusCode.of(2), "text/plain", String)
    builder.response(StatusCode.of(3), "application/*", Model)
    builder.response(StatusCode.of(4), "*/*", Model)
    builder.response(StatusCode.of(5))
    builder.response(StatusCode.DEFAULT, "application/json", DefaultModel)
    def operation = builder.build()

    expect:
    operation.determineAcceptHeaderValue() == "application/json, application/pdf; q=0.5, text/plain; q=0.5, application/*; q=0.5, */*; q=0.5"
  }

  @Unroll
  def "determineTypeOfContent #statusCode #contentType"() {
    given:
    Operation.Builder builder = new Operation.Builder("/path", "GET")
    builder.response(StatusCode.of(0))
    builder.response(StatusCode.of(1), "application/json", Model)
    builder.response(StatusCode.of(1), "application/pdf", InputStream)
    builder.response(StatusCode.of(2), "text/plain", String)
    builder.response(StatusCode.of(3), "application/*", Model)
    builder.response(StatusCode.of(4), "*/*", Model)
    builder.response(StatusCode.of(5), "application/xml", Model)
    builder.response(StatusCode.DEFAULT, "application/json", DefaultModel)
    def operation = builder.build()

    expect:
    operation.determineMatchingResponseType(statusCode, contentType) == expectedType

    where:
    statusCode | contentType                                               | expectedType
    0          | "application/json"                                        | Void.TYPE
    0          | "text/plain"                                              | Void.TYPE
    0          | "nonsense"                                                | Void.TYPE
    0          | null                                                      | Void.TYPE
    1          | "application/json"                                        | Model
    1          | "application/json; charset=UTF-8"                         | Model
    1          | "application/json;Charset=UTF-8; whatever=\"some value\"" | Model
    1          | "application/pdf"                                         | InputStream
    1          | "text/plain"                                              | null
    1          | "nonsense"                                                | null
    1          | null                                                      | null
    2          | "text/plain"                                              | String
    2          | "application/json"                                        | String
    2          | "nonsense"                                                | null
    2          | null                                                      | null
    3          | "application/json"                                        | Model
    3          | "application/json; charset=UTF-8"                         | Model
    3          | "application/json;Charset=UTF-8; whatever=\"some value\"" | Model
    3          | "application/pdf"                                         | Model
    3          | "text/plain"                                              | null
    3          | "nonsense"                                                | null
    3          | null                                                      | null
    4          | "application/json"                                        | Model
    4          | "application/json; charset=UTF-8"                         | Model
    4          | "application/json;Charset=UTF-8; whatever=\"some value\"" | Model
    4          | "application/pdf"                                         | Model
    4          | "text/plain"                                              | Model
    4          | "nonsense"                                                | Model
    4          | null                                                      | null
    5          | "application/xml"                                         | Model
    5          | "application/json"                                        | Model
    5          | "application/json; charset=UTF-8"                         | Model
    5          | "application/json;Charset=UTF-8; whatever=\"some value\"" | Model
    5          | "application/vnd.custom+json"                             | Model
    5          | "application/pdf"                                         | null
    5          | "text/plain"                                              | null
    5          | "nonsense"                                                | null
    5          | null                                                      | null
    99         | "application/json"                                        | DefaultModel
    99         | "text/plain"                                              | null
    99         | null                                                      | null
  }

  @Unroll
  def "determineTypeOfContent with invalid content type in definition #statusCode #contentType"() {
    given:
    Operation.Builder builder = new Operation.Builder("/path", "GET")
    builder.response(StatusCode.of(1), "nonsense", Model)
    builder.response(StatusCode.DEFAULT, "application/json", DefaultModel)
    def operation = builder.build()

    expect:
    operation.determineMatchingResponseType(statusCode, contentType) == expectedType

    where:
    statusCode | contentType        | expectedType
    1          | "application/json" | Model
    1          | "text/plain"       | null
    1          | "nonsense"         | null
    1          | null               | null
    99         | "application/json" | DefaultModel
    99         | "text/plain"       | null
    99         | "nonsense"         | null
    99         | null               | null
  }

  static class Model {
  }

  static class DefaultModel {
  }
}
