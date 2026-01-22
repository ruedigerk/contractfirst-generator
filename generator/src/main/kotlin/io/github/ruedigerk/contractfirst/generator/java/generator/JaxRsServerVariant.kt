package io.github.ruedigerk.contractfirst.generator.java.generator

import com.squareup.javapoet.*
import io.github.ruedigerk.contractfirst.generator.java.Identifiers.capitalize
import io.github.ruedigerk.contractfirst.generator.java.Identifiers.mediaTypeToJavaIdentifier
import io.github.ruedigerk.contractfirst.generator.java.generator.Annotations.toAnnotation
import io.github.ruedigerk.contractfirst.generator.java.generator.JavapoetExtensions.doIfNotNull
import io.github.ruedigerk.contractfirst.generator.java.generator.ServerStubGenerator.Companion.RESPONSE_WRAPPER_CLASS_NAME
import io.github.ruedigerk.contractfirst.generator.java.generator.TypeNames.toClassName
import io.github.ruedigerk.contractfirst.generator.java.generator.TypeNames.toTypeName
import io.github.ruedigerk.contractfirst.generator.java.model.*
import io.github.ruedigerk.contractfirst.generator.model.DefaultStatusCode
import io.github.ruedigerk.contractfirst.generator.model.HttpMethod
import io.github.ruedigerk.contractfirst.generator.model.ParameterLocation.*
import io.github.ruedigerk.contractfirst.generator.model.StatusCode
import javax.lang.model.element.Modifier.*

/**
 * Generates the code for the server stubs.
 */
class JaxRsServerVariant(
    private val supportPackage: String,
) : ServerVariant {

  override fun addAnnotationsToJavaInterface(builder: TypeSpec.Builder) {
    with(builder) {
      builder.addAnnotation(pathAnnotation(""))
    }
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
      doIfNotNull(parameter as? JavaMultipartBodyParameter) { addAnnotation(paramAnnotation(it)) }
    }
  }

  private fun paramAnnotation(parameter: JavaRegularParameter): AnnotationSpec {
    val annotationName = when (parameter.location) {
      QUERY -> "QueryParam"
      HEADER -> "HeaderParam"
      PATH -> "PathParam"
      COOKIE -> "CookieParam"
    }

    return toAnnotation("jakarta.ws.rs.$annotationName", parameter.originalName)
  }

  private fun paramAnnotation(parameter: JavaMultipartBodyParameter): AnnotationSpec = toAnnotation("jakarta.ws.rs.FormParam", parameter.originalName)

  private fun pathAnnotation(path: String) = toAnnotation("jakarta.ws.rs.Path", path)

  private fun httpMethodAnnotation(method: HttpMethod) = toAnnotation("jakarta.ws.rs.${method.name.uppercase()}")

  private fun producesAnnotation(responses: List<JavaResponse>): AnnotationSpec {
    val mediaTypes = responses.flatMap { response -> response.contents.map { it.mediaType } }
        .sorted()
        .distinct()

    return toAnnotation("jakarta.ws.rs.Produces", mediaTypes)
  }

  private fun consumesAnnotation(mediaType: String): AnnotationSpec {
    return toAnnotation("jakarta.ws.rs.Consumes", mediaType)
  }

  override fun toTypesafeResponseClass(operation: JavaOperation): TypeSpec {
    val jaxRsResponseTypeName = "jakarta.ws.rs.core.Response".toClassName()
    val className = operation.javaMethodName.capitalize() + "Response"

    val responseMethodsWithStatusCode = operation.responses
        .filter { it.statusCode is StatusCode }
        .flatMap { response ->
          if (response.contents.isEmpty()) {
            listOf(toTypesafeEmptyResponseMethod(response, className))
          } else {
            response.contents.map { content -> toTypesafeResponseMethod(response, content, className) }
          }
        }

    val defaultResponseMethods = operation.responses
        .filter { it.statusCode is DefaultStatusCode }
        .flatMap { response -> response.contents.map { content -> toTypesafeDefaultResponseMethod(content, className) } }

    val customResponseMethod = MethodSpec.methodBuilder("withCustomResponse")
        .addModifiers(PUBLIC, STATIC)
        .returns(className.toClassName())
        .addParameter(jaxRsResponseTypeName, "response")
        .addStatement("return new \$N(response)", className)
        .build()

    val constructor = MethodSpec.constructorBuilder()
        .addModifiers(PRIVATE)
        .addParameter(jaxRsResponseTypeName, "delegate")
        .addStatement("super(delegate)")
        .build()

    return TypeSpec.classBuilder(className)
        .addModifiers(PUBLIC, STATIC)
        .superclass(ClassName.get(supportPackage, RESPONSE_WRAPPER_CLASS_NAME))
        .addMethod(constructor)
        .addMethods(responseMethodsWithStatusCode)
        .addMethods(defaultResponseMethods)
        .addMethod(customResponseMethod)
        .build()
  }

  private fun toTypesafeResponseMethod(response: JavaResponse, content: JavaContent, className: String): MethodSpec {
    val statusCode = (response.statusCode as StatusCode).code
    val mediaTypeAsIdentifier = content.mediaType.mediaTypeToJavaIdentifier()
    val methodName = "with$statusCode$mediaTypeAsIdentifier"

    return MethodSpec.methodBuilder(methodName)
        .addModifiers(PUBLIC, STATIC)
        .returns(className.toClassName())
        .addParameter(content.javaType.toTypeName(), "entity")
        .addStatement("return new \$N(Response.status(\$L).header(\"Content-Type\", \$S).entity(entity).build())", className, statusCode, content.mediaType)
        .build()
  }

  private fun toTypesafeEmptyResponseMethod(response: JavaResponse, className: String): MethodSpec {
    val statusCode = (response.statusCode as StatusCode).code
    val methodName = "with$statusCode"

    return MethodSpec.methodBuilder(methodName)
        .addModifiers(PUBLIC, STATIC)
        .returns(className.toClassName())
        .addStatement("return new \$N(Response.status(\$L).build())", className, statusCode)
        .build()
  }

  private fun toTypesafeDefaultResponseMethod(content: JavaContent, className: String): MethodSpec {
    val mediaTypeAsIdentifier = content.mediaType.mediaTypeToJavaIdentifier()
    val methodName = "with$mediaTypeAsIdentifier"

    return MethodSpec.methodBuilder(methodName)
        .addModifiers(PUBLIC, STATIC)
        .returns(className.toClassName())
        .addParameter(Integer.TYPE, "status")
        .addParameter(content.javaType.toTypeName(), "entity")
        .addStatement("return new \$N(Response.status(status).header(\"Content-Type\", \$S).entity(entity).build())", className, content.mediaType)
        .build()
  }
}
