package io.github.ruedigerk.contractfirst.generator.java.generator.servergenerator

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeSpec
import io.github.ruedigerk.contractfirst.generator.java.generator.Annotations.toAnnotation
import io.github.ruedigerk.contractfirst.generator.java.generator.JavapoetExtensions.doIfNotNull
import io.github.ruedigerk.contractfirst.generator.java.generator.TypeNames.toClassName
import io.github.ruedigerk.contractfirst.generator.java.model.*
import io.github.ruedigerk.contractfirst.generator.openapi.ParameterLocation.*

/**
 * Spring Web MVC-specific implementation of the ServerGeneratorVariant.
 */
class SpringWebServerGeneratorVariant : ServerGeneratorVariant {

  override val responseClassName: String
    get() = "org.springframework.http.ResponseEntity"

  override val templateDirectory: String
    get() = "server_spring"

  override val attachmentTypeName: JavaTypeName
    get() = JavaTypeName("org.springframework.web.multipart", "MultipartFile")

  override fun addAnnotationsToJavaInterface(builder: TypeSpec.Builder) {
    builder.addAnnotation(toAnnotation("org.springframework.validation.annotation.Validated"))
  }

  override fun addAnnotationsToOperationMethod(builder: MethodSpec.Builder, operation: JavaOperation) {
    val annotationBuilder = AnnotationSpec.builder("org.springframework.web.bind.annotation.RequestMapping".toClassName())
        .addMember("method", "\$T.\$L", "org.springframework.web.bind.annotation.RequestMethod".toClassName(), operation.httpMethod.name)
        .addMember("value", "\$S", operation.path)
        .doIfNotNull(operation.requestBodyMediaType) { addMember("consumes", "\$S", operation.requestBodyMediaType) }

    val producedMediaTypes = operation.responses.flatMap { response -> response.contents.map { it.mediaType } }
        .sorted()
        .distinct()
    producedMediaTypes.forEach { annotationBuilder.addMember("produces", "\$S", it) }

    builder.addAnnotation(annotationBuilder.build())
  }

  override fun addAnnotationsToMethodParameter(builder: ParameterSpec.Builder, parameter: JavaParameter) {
    when (parameter) {
      is JavaRegularParameter -> builder.addAnnotation(paramAnnotation(parameter))
      is JavaMultipartBodyParameter -> builder.addAnnotation(multipartAnnotation(parameter))
      is JavaBodyParameter -> builder.addAnnotation("org.springframework.web.bind.annotation.RequestBody".toClassName())
    }
  }

  private fun paramAnnotation(parameter: JavaRegularParameter): AnnotationSpec {
    val annotationName = when (parameter.location) {
      QUERY -> "RequestParam"
      HEADER -> "RequestHeader"
      PATH -> "PathVariable"
      COOKIE -> "CookieValue"
    }

    return toAnnotation("org.springframework.web.bind.annotation.$annotationName", parameter.originalName)
  }

  private fun multipartAnnotation(parameter: JavaMultipartBodyParameter): AnnotationSpec {
    val builder = toAnnotation("org.springframework.web.bind.annotation.RequestPart", parameter.originalName).toBuilder()
    builder.addMember("required", "\$L", parameter.required)

    return builder.build()
  }

  override fun buildResponseWithEntity(): String = ".body(entity)"

  override fun rewriteResponseBodyType(javaType: JavaAnyType): JavaAnyType = javaType.rewriteSimpleType(JavaTypeName.INPUT_STREAM, BINARY_RESPONSE_BODY_TYPE)

  private companion object {

    private val BINARY_RESPONSE_BODY_TYPE = JavaTypeName("org.springframework.core.io", "Resource")
  }
}
