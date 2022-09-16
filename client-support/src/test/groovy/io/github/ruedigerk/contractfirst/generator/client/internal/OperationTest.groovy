package io.github.ruedigerk.contractfirst.generator.client.internal

import spock.lang.Specification

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

  def "determineTypeOfContent selects expected response definition, default is defined"() {
    given:
    Operation.Builder builder = new Operation.Builder("/path", "GET")
    builder.response(StatusCode.of(200), "application/json", Model)
    builder.response(StatusCode.of(200), "application/pdf", InputStream)
    builder.response(StatusCode.of(201), "text/plain", String)
    builder.response(StatusCode.of(202), "application/*", Model)
    builder.response(StatusCode.of(203), "*/*", Model)
    builder.response(StatusCode.of(204))
    builder.response(StatusCode.of(205), "application/xml", Model)
    builder.response(StatusCode.DEFAULT, "application/json", DefaultModel)
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
    201        | "application/json"                                        | String
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
    204        | "application/json"                                        | Void.TYPE
    204        | "text/plain"                                              | Void.TYPE
    204        | "nonsense"                                                | Void.TYPE
    204        | null                                                      | Void.TYPE
    205        | "application/xml"                                         | Model
    205        | "application/json"                                        | Model
    205        | "application/json; charset=UTF-8"                         | Model
    205        | "application/json;Charset=UTF-8; whatever=\"some value\"" | Model
    205        | "application/vnd.custom+json"                             | Model
    205        | "application/pdf"                                         | null
    205        | "text/plain"                                              | null
    205        | "nonsense"                                                | null
    205        | null                                                      | null
    299        | "application/json"                                        | DefaultModel
    299        | "text/plain"                                              | null
    299        | null                                                      | null
    400        | "application/json"                                        | DefaultModel
    400        | "text/plain"                                              | null
    400        | null                                                      | null
    500        | "application/json"                                        | DefaultModel
    500        | "text/plain"                                              | null
    500        | null                                                      | null
  }

  def "determineTypeOfContent selects expected response definition, no default"() {
    given:
    Operation.Builder builder = new Operation.Builder("/path", "GET")
    builder.response(StatusCode.of(200), "application/json", Model)
    builder.response(StatusCode.of(200), "application/pdf", InputStream)
    builder.response(StatusCode.of(201), "text/plain", String)
    builder.response(StatusCode.of(202), "application/*", Model)
    builder.response(StatusCode.of(203), "*/*", Model)
    builder.response(StatusCode.of(204))
    builder.response(StatusCode.of(205), "application/xml", Model)
    builder.response(StatusCode.of(500), "application/json", DefaultModel)
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
    201        | "application/json"                                        | String
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
    204        | "application/json"                                        | Void.TYPE
    204        | "text/plain"                                              | Void.TYPE
    204        | "nonsense"                                                | Void.TYPE
    204        | null                                                      | Void.TYPE
    205        | "application/xml"                                         | Model
    205        | "application/json"                                        | Model
    205        | "application/json; charset=UTF-8"                         | Model
    205        | "application/json;Charset=UTF-8; whatever=\"some value\"" | Model
    205        | "application/vnd.custom+json"                             | Model
    205        | "application/pdf"                                         | null
    205        | "text/plain"                                              | null
    205        | "nonsense"                                                | null
    205        | null                                                      | null
    299        | "application/json"                                        | null
    299        | "text/plain"                                              | null
    299        | null                                                      | null
    400        | "application/json"                                        | null
    400        | "text/plain"                                              | null
    400        | null                                                      | null
    500        | "application/json"                                        | DefaultModel
    500        | "text/plain"                                              | null
    500        | null                                                      | null
  }

  def "determineTypeOfContent with no default only selects matching definitions if there are multiple successful definitions"() {
    given:
    Operation.Builder builder = new Operation.Builder("/path", "GET")
    builder.response(StatusCode.of(200), "application/json", Model)
    builder.response(StatusCode.of(204))
    def operation = builder.build()

    expect:
    operation.determineMatchingResponseType(statusCode, contentType) == expectedType

    where:
    statusCode | contentType        | expectedType
    200        | "application/json" | Model
    201        | "application/json" | null
    204        | null               | Void.TYPE
  }

  def "determineTypeOfContent accepts JSON content type even if not defined in the contract"() {
    given:
    Operation.Builder builder = new Operation.Builder("/path", "GET")
    builder.response(StatusCode.of(200), "nonsense", Model)
    builder.response(StatusCode.DEFAULT, "nonsense", DefaultModel)
    def operation = builder.build()

    expect:
    operation.determineMatchingResponseType(statusCode, contentType) == expectedType

    where:
    statusCode | contentType        | expectedType
    200        | "application/json" | Model
    200        | "text/plain"       | null
    200        | "nonsense"         | null
    200        | null               | null
    500        | "application/json" | DefaultModel
    500        | "text/plain"       | null
    500        | "nonsense"         | null
    500        | null               | null
  }

  static class Model {
  }

  static class DefaultModel {
  }
}
