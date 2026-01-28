package io.github.ruedigerk.contractfirst.generator.java.generator.servergenerator

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeSpec
import io.github.ruedigerk.contractfirst.generator.java.generator.Annotations.toAnnotation
import io.github.ruedigerk.contractfirst.generator.java.generator.doIfNotNull
import io.github.ruedigerk.contractfirst.generator.java.model.JavaAnyType
import io.github.ruedigerk.contractfirst.generator.java.model.JavaMultipartBodyParameter
import io.github.ruedigerk.contractfirst.generator.java.model.JavaOperation
import io.github.ruedigerk.contractfirst.generator.java.model.JavaParameter
import io.github.ruedigerk.contractfirst.generator.java.model.JavaRegularParameter
import io.github.ruedigerk.contractfirst.generator.java.model.JavaResponse
import io.github.ruedigerk.contractfirst.generator.java.model.JavaTypeName
import io.github.ruedigerk.contractfirst.generator.openapi.HttpMethod
import io.github.ruedigerk.contractfirst.generator.openapi.ParameterLocation.COOKIE
import io.github.ruedigerk.contractfirst.generator.openapi.ParameterLocation.HEADER
import io.github.ruedigerk.contractfirst.generator.openapi.ParameterLocation.PATH
import io.github.ruedigerk.contractfirst.generator.openapi.ParameterLocation.QUERY

/**
 * JAX-RS-specific implementation of the ServerGeneratorVariant.
 */
class JaxRsServerGeneratorVariant : ServerGeneratorVariant {

  override val responseClassName: String
    get() = "jakarta.ws.rs.core.Response"

  override val templateDirectory: String
    get() = "server_jax_rs"

  override val attachmentTypeName: JavaTypeName
    get() = JavaTypeName("jakarta.ws.rs.core", "EntityPart")

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
      doIfNotNull(parameter as? JavaRegularParameter) { addAnnotation(paramAnnotation(it)) }
      doIfNotNull(parameter as? JavaMultipartBodyParameter) { addAnnotation(multipartAnnotation(it)) }
    }
  }

  private fun paramAnnotation(parameter: JavaRegularParameter): AnnotationSpec {
    val annotationName =
      when (parameter.location) {
        QUERY -> "QueryParam"
        HEADER -> "HeaderParam"
        PATH -> "PathParam"
        COOKIE -> "CookieParam"
      }

    return toAnnotation("jakarta.ws.rs.$annotationName", parameter.originalName)
  }

  private fun multipartAnnotation(parameter: JavaMultipartBodyParameter): AnnotationSpec = toAnnotation("jakarta.ws.rs.FormParam", parameter.originalName)

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

  override fun rewriteResponseBodyType(javaType: JavaAnyType): JavaAnyType = javaType
}
