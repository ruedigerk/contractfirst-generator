package io.github.ruedigerk.contractfirst.generator.integrationtest

import io.github.ruedigerk.contractfirst.generator.combinations.generated.client.api.ApiClientErrorWithCBookEntityException
import io.github.ruedigerk.contractfirst.generator.combinations.generated.client.api.ApiClientErrorWithCCtcErrorEntityException
import io.github.ruedigerk.contractfirst.generator.combinations.generated.client.api.ApiClientErrorWithCSevereCtcErrorEntityException
import io.github.ruedigerk.contractfirst.generator.combinations.generated.client.api.ContentTypeCombinationsApiClient
import io.github.ruedigerk.contractfirst.generator.combinations.generated.client.model.CBook
import io.github.ruedigerk.contractfirst.generator.combinations.generated.client.model.CCtcError
import io.github.ruedigerk.contractfirst.generator.combinations.generated.client.model.CSevereCtcError
import io.github.ruedigerk.contractfirst.generator.combinations.generated.server.model.SBook
import io.github.ruedigerk.contractfirst.generator.combinations.generated.server.model.SCtcError
import io.github.ruedigerk.contractfirst.generator.combinations.generated.server.model.SSevereCtcError
import io.github.ruedigerk.contractfirst.generator.combinations.generated.server.resources.ContentTypeCombinationsApi
import io.github.ruedigerk.contractfirst.generator.integrationtest.spec.EmbeddedJaxRsServerSpecification
import spock.lang.Subject

class ContentTypeCombinationsTest extends EmbeddedJaxRsServerSpecification {

  private static final CBook CBOOK = new CBook(title: "The Book", isbn: "The ISBN")
  private static final CCtcError CCTCERROR = new CCtcError(code: "Error")
  private static final CSevereCtcError CSEVERECTCERROR = new CSevereCtcError(code: 42)

  @Subject
  ContentTypeCombinationsApiClient apiClient = new ContentTypeCombinationsApiClient(apiRequestExecutor)

  @Override
  Class<?> getTestResource() {
    EmbeddedServerResource
  }

  def "getDefaultOnly"() {
    when:
    def result = apiClient.returningResult().getDefaultOnly("success")

    then:
    result.isReturningCBook()
    result.entity == CBOOK
    result.response.statusCode == 200

    when:
    apiClient.getDefaultOnly("failure")

    then:
    def e = thrown ApiClientErrorWithCBookEntityException
    e.statusCode == 400
    e.entity == CBOOK
  }

  def "getSuccessOnly"() {
    when:
    def result = apiClient.returningResult().getSuccessOnly()

    then:
    result.isStatus200ReturningCBook()
    result.entity == CBOOK
  }

  def "getFailureOnly"() {
    given:
    def expectedErrorEntity = new CCtcError(code: "FailureOnly")

    when:
    def result = apiClient.returningResult().getFailureOnly()

    then:
    result.isStatus400ReturningCCtcError()
    result.entity == expectedErrorEntity

    when:
    apiClient.getFailureOnly()

    then:
    def e = thrown ApiClientErrorWithCCtcErrorEntityException
    e.statusCode == 400
    e.entity == expectedErrorEntity
  }

  def "getSuccessEntityAndErrorDefault"() {
    when:
    def result = apiClient.returningResult().getSuccessEntityAndErrorDefault("success")

    then:
    result.isStatus200ReturningCBook()
    result.entityAsCBook == CBOOK

    when:
    apiClient.getSuccessEntityAndErrorDefault("failure")

    then:
    def e = thrown ApiClientErrorWithCCtcErrorEntityException
    e.entity == CCTCERROR
  }

  def "getMultipleSuccessEntities"() {
    when:
    def result = apiClient.returningResult().getMultipleSuccessEntities("book")

    then:
    result.isStatus200ReturningCBook()
    result.entityAsCBook == CBOOK
    result.entityIfCBook == Optional.of(CBOOK)
    result.entityAsCCtcError == null
    result.entityIfCCtcError == Optional.empty()

    when:
    result = apiClient.returningResult().getMultipleSuccessEntities("error")

    then:
    result.isStatus201ReturningCCtcError()
    result.entityAsCBook == null
    result.entityIfCBook == Optional.empty()
    result.entityAsCCtcError == CCTCERROR
    result.entityIfCCtcError == Optional.of(CCTCERROR)
  }

  def "getMultipleSuccessResponsesWithoutContent"() {
    when:
    def result = apiClient.returningResult().getMultipleSuccessResponsesWithoutContent("200")

    then:
    result.isStatus200WithoutEntity()

    when:
    result = apiClient.returningResult().getMultipleSuccessResponsesWithoutContent("204")

    then:
    result.isStatus204WithoutEntity()
  }

  def "getMultipleErrorEntities"() {
    when:
    apiClient.getMultipleErrorEntities("400")

    then:
    def ctcErrorEntityException = thrown ApiClientErrorWithCCtcErrorEntityException
    ctcErrorEntityException.statusCode == 400
    ctcErrorEntityException.entity == CCTCERROR

    when:
    apiClient.getMultipleErrorEntities("500")

    then:
    def severeCtcErrorEntityException = thrown ApiClientErrorWithCSevereCtcErrorEntityException
    severeCtcErrorEntityException.statusCode == 500
    severeCtcErrorEntityException.entity == CSEVERECTCERROR
  }

  def "getContentFor204"() {
    when:
    def result = apiClient.returningResult().getContentFor204()

    then:
    result.isStatus204WithoutEntity()
  }

  /**
   * JAX-RS resource implementation used in this test.
   */
  static class EmbeddedServerResource implements ContentTypeCombinationsApi {

    private static final SBook SBOOK = new SBook(title: "The Book", isbn: "The ISBN")
    private static final SCtcError SCTCERROR = new SCtcError(code: "Error")
    private static final SSevereCtcError SSEVERECTCERROR = new SSevereCtcError(code: 42)

    @Override
    GetDefaultOnlyResponse getDefaultOnly(String testCaseSelector) {
      switch (testCaseSelector) {
        case "success":
          return GetDefaultOnlyResponse.withApplicationJson(200, SBOOK)
        default:
          return GetDefaultOnlyResponse.withApplicationJson(400, SBOOK)
      }
    }

    @Override
    GetSuccessOnlyResponse getSuccessOnly() {
      return GetSuccessOnlyResponse.with200ApplicationJson(SBOOK)
    }

    @Override
    GetFailureOnlyResponse getFailureOnly() {
      return GetFailureOnlyResponse.with400ApplicationJson(new SCtcError(code: "FailureOnly"))
    }

    @Override
    GetSuccessEntityAndErrorDefaultResponse getSuccessEntityAndErrorDefault(String testCaseSelector) {
      switch (testCaseSelector) {
        case "success":
          return GetSuccessEntityAndErrorDefaultResponse.with200ApplicationJson(SBOOK)
        default:
          return GetSuccessEntityAndErrorDefaultResponse.withApplicationJson(400, SCTCERROR)
      }
    }

    @Override
    GetMultipleSuccessEntitiesResponse getMultipleSuccessEntities(String testCaseSelector) {
      switch (testCaseSelector) {
        case "book":
          return GetMultipleSuccessEntitiesResponse.with200ApplicationJson(SBOOK)
        default:
          return GetMultipleSuccessEntitiesResponse.with201ApplicationJson(SCTCERROR)
      }
    }

    @Override
    GetMultipleSuccessResponsesWithoutContentResponse getMultipleSuccessResponsesWithoutContent(String testCaseSelector) {
      switch (testCaseSelector) {
        case "200":
          return GetMultipleSuccessResponsesWithoutContentResponse.with200()
        default:
          return GetMultipleSuccessResponsesWithoutContentResponse.with204()
      }
    }

    @Override
    GetMultipleErrorEntitiesResponse getMultipleErrorEntities(String testCaseSelector) {
      switch (testCaseSelector) {
        case "400":
          return GetMultipleErrorEntitiesResponse.with400ApplicationJson(SCTCERROR)
        default:
          return GetMultipleErrorEntitiesResponse.with500ApplicationJson(SSEVERECTCERROR)
      }
    }

    @Override
    GetContentFor204Response getContentFor204() {
      return GetContentFor204Response.with204()
    }
  }
}
