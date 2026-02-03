package io.github.ruedigerk.contractfirst.generator.java.generator.servergenerator

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeSpec
import io.github.ruedigerk.contractfirst.generator.java.generator.Annotations.toAnnotation
import io.github.ruedigerk.contractfirst.generator.java.generator.JavaSpecRewriter
import io.github.ruedigerk.contractfirst.generator.java.generator.JavaSpecRewriter.Companion.Rewriter
import io.github.ruedigerk.contractfirst.generator.java.generator.JavaSpecRewriter.Companion.rewriteBinaryTypeTo
import io.github.ruedigerk.contractfirst.generator.java.generator.JavaSpecRewriter.Companion.rewriteDissectedBodyParameter
import io.github.ruedigerk.contractfirst.generator.java.generator.JavaSpecRewriter.Companion.rewriteDissectedBodyParameterType
import io.github.ruedigerk.contractfirst.generator.java.generator.JavaSpecRewriter.Companion.rewriteFormUrlEncodedBodyParameters
import io.github.ruedigerk.contractfirst.generator.java.generator.doIfNotNull
import io.github.ruedigerk.contractfirst.generator.java.model.JavaDissectedBodyParameter
import io.github.ruedigerk.contractfirst.generator.java.model.JavaDissectedBodyParameter.BodyPartType.COMPLEX
import io.github.ruedigerk.contractfirst.generator.java.model.JavaDissectedBodyParameter.BodyPartType.PRIMITIVE
import io.github.ruedigerk.contractfirst.generator.java.model.JavaDissectedBodyParameter.DissectedMediaTypeFamily.MULTIPART
import io.github.ruedigerk.contractfirst.generator.java.model.JavaOperation
import io.github.ruedigerk.contractfirst.generator.java.model.JavaParameter
import io.github.ruedigerk.contractfirst.generator.java.model.JavaRegularParameter
import io.github.ruedigerk.contractfirst.generator.java.model.JavaResponse
import io.github.ruedigerk.contractfirst.generator.java.model.JavaSpecification
import io.github.ruedigerk.contractfirst.generator.java.model.JavaType
import io.github.ruedigerk.contractfirst.generator.java.model.JavaTypeName
import io.github.ruedigerk.contractfirst.generator.openapi.HttpMethod
import io.github.ruedigerk.contractfirst.generator.openapi.ParameterLocation.COOKIE
import io.github.ruedigerk.contractfirst.generator.openapi.ParameterLocation.HEADER
import io.github.ruedigerk.contractfirst.generator.openapi.ParameterLocation.PATH
import io.github.ruedigerk.contractfirst.generator.openapi.ParameterLocation.QUERY

/**
 * JAX-RS-specific implementation of the ServerGeneratorVariant.
 */
object JaxRsServerGeneratorVariant : ServerGeneratorVariant {

  override val responseClassName = "jakarta.ws.rs.core.Response"
  override val templateDirectory = "server_jax_rs"

  override fun specificationRewriter(): (JavaSpecification) -> JavaSpecification = JavaSpecRewriter(
    parameterRewriter = listOf(
      rewriteDissectedBodyParameterType(rewriteBinaryTypeTo(TypeName.EntityPart)),
      rewriteFormUrlEncodedBodyParameters,
      rewriteDissectedBodyParameters,
    ),
  )

  /**
   * JAX-RS server only supports the types jakarta.ws.rs.core.EntityPart, java.io.InputStream, or String for method parameters representing parts of a
   * dissected body. This rewrites all generated "JSON" types to either EntityPart or String.
   *
   * See:https://jakarta.ee/specifications/restful-ws/3.1/jakarta-restful-ws-spec-3.1.html#consuming_multipart_formdata
   */
  private val rewriteDissectedBodyParameters: Rewriter<JavaParameter> = rewriteDissectedBodyParameter(MULTIPART) {
    when (bodyPartType) {
      COMPLEX -> copy(javaType = JavaType(TypeName.EntityPart))
      PRIMITIVE if (javaType.isGenerated()) -> copy(javaType = JavaType(JavaTypeName.STRING))
      else -> this
    }
  }

  override fun addAnnotationsToJavaInterface(builder: TypeSpec.Builder) {
    builder.addAnnotation(pathAnnotation(""))
  }

  override fun addAnnotationsToOperationMethod(builder: MethodSpec.Builder, operation: JavaOperation) {
    with(builder) {
      addAnnotation(httpMethodAnnotation(operation.httpMethod))
      addAnnotation(pathAnnotation(operation.path))
      doIfNotNull(operation.requestBodyMediaType) { addAnnotation(consumesAnnotation(it)) }
      addAnnotation(producesAnnotation(operation.responses))
    }
  }

  override fun addAnnotationsToMethodParameter(builder: ParameterSpec.Builder, parameter: JavaParameter) {
    with(builder) {
      doIfNotNull(parameter as? JavaRegularParameter) { addAnnotation(regularParameterAnnotation(it)) }
      doIfNotNull(parameter as? JavaDissectedBodyParameter) { addAnnotation(dissectedBodyParameterAnnotation(it)) }
    }
  }

  private fun regularParameterAnnotation(parameter: JavaRegularParameter): AnnotationSpec {
    val annotationName =
      when (parameter.location) {
        QUERY -> "QueryParam"
        HEADER -> "HeaderParam"
        PATH -> "PathParam"
        COOKIE -> "CookieParam"
      }

    return toAnnotation("jakarta.ws.rs.$annotationName", parameter.originalName)
  }

  private fun dissectedBodyParameterAnnotation(parameter: JavaDissectedBodyParameter): AnnotationSpec =
    toAnnotation("jakarta.ws.rs.FormParam", parameter.originalName)

  private fun pathAnnotation(path: String) = toAnnotation("jakarta.ws.rs.Path", path)

  private fun httpMethodAnnotation(method: HttpMethod) = toAnnotation("jakarta.ws.rs.${method.name.uppercase()}")

  private fun producesAnnotation(responses: List<JavaResponse>): AnnotationSpec {
    val mediaTypes = responses.flatMap { response -> response.contents.map { it.mediaType } }
      .sorted()
      .distinct()

    return toAnnotation("jakarta.ws.rs.Produces", mediaTypes)
  }

  private fun consumesAnnotation(mediaType: String): AnnotationSpec = toAnnotation("jakarta.ws.rs.Consumes", mediaType)

  override fun buildResponseWithEntity(): String = ".entity(entity).build()"

  private object TypeName {

    val EntityPart = JavaTypeName("jakarta.ws.rs.core", "EntityPart")
  }
}
