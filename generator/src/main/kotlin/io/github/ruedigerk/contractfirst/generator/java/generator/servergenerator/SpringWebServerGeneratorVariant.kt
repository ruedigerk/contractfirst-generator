package io.github.ruedigerk.contractfirst.generator.java.generator.servergenerator

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeSpec
import io.github.ruedigerk.contractfirst.generator.java.generator.JavaSpecRewriter
import io.github.ruedigerk.contractfirst.generator.java.generator.JavaSpecRewriter.Companion.rewriteBinaryTypeTo
import io.github.ruedigerk.contractfirst.generator.java.generator.JavaSpecRewriter.Companion.rewriteBodyParameterType
import io.github.ruedigerk.contractfirst.generator.java.generator.JavaSpecRewriter.Companion.rewriteDissectedBodyParameterType
import io.github.ruedigerk.contractfirst.generator.java.generator.JavaSpecRewriter.Companion.rewriteFormUrlEncodedBodyParameters
import io.github.ruedigerk.contractfirst.generator.java.generator.JavaSpecRewriter.Companion.rewriteResponseContentType
import io.github.ruedigerk.contractfirst.generator.java.generator.TypeNames.toClassName
import io.github.ruedigerk.contractfirst.generator.java.generator.doIfNotNull
import io.github.ruedigerk.contractfirst.generator.java.model.JavaBodyParameter
import io.github.ruedigerk.contractfirst.generator.java.model.JavaDissectedBodyParameter
import io.github.ruedigerk.contractfirst.generator.java.model.JavaDissectedBodyParameter.BodyPartType
import io.github.ruedigerk.contractfirst.generator.java.model.JavaNamedParameter
import io.github.ruedigerk.contractfirst.generator.java.model.JavaOperation
import io.github.ruedigerk.contractfirst.generator.java.model.JavaParameter
import io.github.ruedigerk.contractfirst.generator.java.model.JavaRegularParameter
import io.github.ruedigerk.contractfirst.generator.java.model.JavaSpecification
import io.github.ruedigerk.contractfirst.generator.java.model.JavaTypeName
import io.github.ruedigerk.contractfirst.generator.openapi.ParameterLocation.COOKIE
import io.github.ruedigerk.contractfirst.generator.openapi.ParameterLocation.HEADER
import io.github.ruedigerk.contractfirst.generator.openapi.ParameterLocation.PATH
import io.github.ruedigerk.contractfirst.generator.openapi.ParameterLocation.QUERY

/**
 * Spring Web MVC-specific implementation of the ServerGeneratorVariant.
 */
object SpringWebServerGeneratorVariant : ServerGeneratorVariant {

  override val responseClassName = "org.springframework.http.ResponseEntity"
  override val templateDirectory = "server_spring"

  override fun specificationRewriter(): (JavaSpecification) -> JavaSpecification = JavaSpecRewriter(
    parameterRewriter = listOf(
      rewriteBodyParameterType(rewriteBinaryTypeTo(TypeNames.Resource)),
      rewriteDissectedBodyParameterType(rewriteBinaryTypeTo(TypeNames.MultipartFile)),
      rewriteFormUrlEncodedBodyParameters,
    ),
    responseContentRewriter = listOf(
      rewriteResponseContentType(rewriteBinaryTypeTo(TypeNames.Resource)),
    ),
  )

  override fun addAnnotationsToJavaInterface(builder: TypeSpec.Builder) {
    builder.addAnnotation(Annotations.Validated)
  }

  override fun addAnnotationsToOperationMethod(builder: MethodSpec.Builder, operation: JavaOperation) {
    val annotationBuilder = AnnotationSpec.builder(Annotations.RequestMapping)
      .addMember("method", "\$T.\$L", Annotations.RequestMethod, operation.httpMethod.name)
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
      is JavaRegularParameter -> builder.addAnnotation(regularParameterAnnotation(parameter))
      is JavaDissectedBodyParameter -> builder.addAnnotation(dissectedBodyParameterAnnotation(parameter))
      is JavaBodyParameter -> builder.addAnnotation(Annotations.RequestBody)
    }
  }

  private fun regularParameterAnnotation(parameter: JavaRegularParameter): AnnotationSpec {
    val annotation = when (parameter.location) {
      QUERY -> Annotations.RequestParam
      HEADER -> Annotations.RequestHeader
      PATH -> Annotations.PathVariable
      COOKIE -> Annotations.CookieValue
    }

    return bindParameterAnnotation(annotation, parameter)
  }

  private fun dissectedBodyParameterAnnotation(parameter: JavaDissectedBodyParameter): AnnotationSpec {
    val annotation = when (parameter.bodyPartType) {
      BodyPartType.PRIMITIVE -> Annotations.RequestParam
      BodyPartType.COMPLEX -> Annotations.RequestPart
      BodyPartType.ATTACHMENT -> Annotations.RequestPart
    }

    return bindParameterAnnotation(annotation, parameter)
  }

  private fun bindParameterAnnotation(annotationClassName: ClassName, parameter: JavaNamedParameter): AnnotationSpec {
    return AnnotationSpec.builder(annotationClassName)
      .addMember("name", "\$S", parameter.originalName)
      .addMember("required", "\$L", parameter.required)
      .build()
  }

  override fun buildResponseWithEntity(): String = ".body(entity)"

  private object Annotations {

    val CookieValue = "org.springframework.web.bind.annotation.CookieValue".toClassName()
    val PathVariable = "org.springframework.web.bind.annotation.PathVariable".toClassName()
    val RequestBody = "org.springframework.web.bind.annotation.RequestBody".toClassName()
    val RequestHeader = "org.springframework.web.bind.annotation.RequestHeader".toClassName()
    val RequestMapping = "org.springframework.web.bind.annotation.RequestMapping".toClassName()
    val RequestMethod = "org.springframework.web.bind.annotation.RequestMethod".toClassName()
    val RequestParam = "org.springframework.web.bind.annotation.RequestParam".toClassName()
    val RequestPart = "org.springframework.web.bind.annotation.RequestPart".toClassName()
    val Validated = "org.springframework.validation.annotation.Validated".toClassName()
  }

  private object TypeNames {

    val Resource = JavaTypeName("org.springframework.core.io", "Resource")
    val MultipartFile = JavaTypeName("org.springframework.web.multipart", "MultipartFile")
  }
}
