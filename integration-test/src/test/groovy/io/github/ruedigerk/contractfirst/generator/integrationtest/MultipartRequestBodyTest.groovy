package io.github.ruedigerk.contractfirst.generator.integrationtest

import io.github.ruedigerk.contractfirst.generator.client.Attachment
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.client.api.MultipartRequestBodyApiClient
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.client.model.CFormEncodedRequestBodyRequestBodyApplicationXWwwFormUrlencodedEnumProperty
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.client.model.CMultipartRequestBodyRequestBodyMultipartFormDataObjectProperty
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.server.model.SFormEncodedRequestBodyRequestBodyApplicationXWwwFormUrlencodedEnumProperty
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.server.model.SMultipartRequestBodyRequestBodyMultipartFormDataObjectProperty
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.server.resources.MultipartRequestBodyApi
import io.github.ruedigerk.contractfirst.generator.integrationtest.spec.EmbeddedJaxRsServerSpecification
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.EntityPart
import jakarta.ws.rs.core.MediaType
import spock.lang.Subject

/**
 * Tests serialization of form encoded request bodies.
 */
class MultipartRequestBodyTest extends EmbeddedJaxRsServerSpecification {

  @Subject
  MultipartRequestBodyApiClient apiClient = new MultipartRequestBodyApiClient(apiRequestExecutor)

  @Override
  Class<?> getTestResource() {
    EmbeddedServerResource
  }

  def "Test form encoded request body"() {
    when:
    def result = apiClient.returningResult().formEncodedRequestBody(
        "a&1",
        42L,
        CFormEncodedRequestBodyRequestBodyApplicationXWwwFormUrlencodedEnumProperty.SECOND_VALUE
    )

    then:
    result.isStatus204WithoutEntity()
  }

  def "Test multipart form data request body"() {
    when:
    def result = apiClient.returningResult().multipartRequestBody(
        "allParametersPresent",
        "a&1",
        42L,
        new CMultipartRequestBodyRequestBodyMultipartFormDataObjectProperty(a: "string", b: 23L),
        Attachment.of(new File("src/test/resources/sample.pdf"), "application/pdf"),
        [
            Attachment.of(getSamplePdfAsInputStream().bytes, "sample-bytes.pdf", "application/pdf"),
            Attachment.of(getSamplePdfAsInputStream(), "sample-is.pdf", "application/pdf"),
        ]
    )

    then:
    result.isStatus204WithoutEntity()
  }

  def "Test multipart form data request body with optional parameters not supplied"() {
    when:
    def result = apiClient.returningResult().multipartRequestBody(
        "optionalParametersMissing",
        null,
        null,
        null,
        null,
        []
    )

    then:
    result.isStatus204WithoutEntity()
  }

  static private InputStream getSamplePdfAsInputStream() {
    MultipartRequestBodyTest.getResourceAsStream("/sample.pdf")
  }

  /**
   * JAX-RS resource implementation used in this test.
   */
  @Path("")
  static class EmbeddedServerResource implements MultipartRequestBodyApi {

    @Override
    FormEncodedRequestBodyResponse formEncodedRequestBody(
        String stringProperty,
        Long integerProperty,
        String enumProperty
    ) {
      assert stringProperty == "a&1"
      assert integerProperty == 42
      assert enumProperty == SFormEncodedRequestBodyRequestBodyApplicationXWwwFormUrlencodedEnumProperty.SECOND_VALUE.toString()

      return FormEncodedRequestBodyResponse.with204()
    }

    @Override
    MultipartRequestBodyResponse multipartRequestBody(
        String testSelector,
        String stringProperty,
        Long integerProperty,
        EntityPart objectProperty,
        EntityPart firstBinary,
        List<EntityPart> additionalBinaries
    ) {
      if (testSelector == "optionalParametersMissing") {
        assert stringProperty == null
        assert integerProperty == null
        assert objectProperty.getContent(String) == "null"
        assert firstBinary == null
        assert additionalBinaries == null
      } else {
        assert stringProperty == "a&1"
        assert integerProperty == 42L
        assert objectProperty.getContent(SMultipartRequestBodyRequestBodyMultipartFormDataObjectProperty) == new SMultipartRequestBodyRequestBodyMultipartFormDataObjectProperty(a: "string", b: 23L)

        verifyBodyPartIsPdf(firstBinary, "firstBinary", "sample.pdf")

        assert additionalBinaries.size() == 2

        verifyBodyPartIsPdf(additionalBinaries[0], "additionalBinaries", "sample-bytes.pdf")
        verifyBodyPartIsPdf(additionalBinaries[1], "additionalBinaries", "sample-is.pdf")
      }

      return MultipartRequestBodyResponse.with204()
    }

    private static void verifyBodyPartIsPdf(EntityPart bodyPart, String name, String fileName) {
      assert bodyPart.name == name
      assert bodyPart.fileName.get() == fileName
      assert bodyPart.mediaType == new MediaType("application", "pdf")
      assert bodyPart.getContent(byte[]) == getSamplePdfAsInputStream().bytes
    }
  }
}
