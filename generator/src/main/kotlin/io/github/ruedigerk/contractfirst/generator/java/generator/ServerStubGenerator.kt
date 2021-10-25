package io.github.ruedigerk.contractfirst.generator.java.generator

import com.squareup.javapoet.*
import io.github.ruedigerk.contractfirst.generator.Configuration
import io.github.ruedigerk.contractfirst.generator.java.Identifiers.capitalize
import io.github.ruedigerk.contractfirst.generator.java.Identifiers.mediaTypeToJavaIdentifier
import io.github.ruedigerk.contractfirst.generator.java.generator.GeneratorCommon.NOT_NULL_ANNOTATION
import io.github.ruedigerk.contractfirst.generator.java.generator.GeneratorCommon.toAnnotation
import io.github.ruedigerk.contractfirst.generator.java.generator.GeneratorCommon.toTypeName
import io.github.ruedigerk.contractfirst.generator.java.generator.JavapoetExtensions.doIf
import io.github.ruedigerk.contractfirst.generator.java.generator.JavapoetExtensions.doIfNotNull
import io.github.ruedigerk.contractfirst.generator.java.model.*
import io.github.ruedigerk.contractfirst.generator.model.DefaultStatusCode
import io.github.ruedigerk.contractfirst.generator.model.ParameterLocation.*
import io.github.ruedigerk.contractfirst.generator.model.StatusCode
import java.io.File
import javax.lang.model.element.Modifier.*

/**
 * Generates the code for the server stubs.
 */
class ServerStubGenerator(private val configuration: Configuration) {

  private val outputDir = File(configuration.outputDir)
  private val apiPackage = "${configuration.outputJavaBasePackage}.$API_PACKAGE"
  private val supportPackage = "$apiPackage.support"

  fun generateCode(specification: JavaSpecification) {
    specification.operationGroups.asSequence()
        .map(::toJavaInterface)
        .forEach { it.writeTo(outputDir) }

    writeResponseWrapperClass()
  }

  private fun toJavaInterface(operationGroup: JavaOperationGroup): JavaFile {
    val operationsToTypesafeResponseClass = operationGroup.operations.associateWith(::toTypesafeResponseClass)
    val methodSpecs = operationsToTypesafeResponseClass.map { (operation, typesafeClass) -> toOperationMethod(operation, typesafeClass) }

    val interfaceSpec = TypeSpec.interfaceBuilder(operationGroup.javaIdentifier)
        .addModifiers(PUBLIC)
        .addAnnotation(pathAnnotation(""))
        .addMethods(methodSpecs)
        .addTypes(operationsToTypesafeResponseClass.values)
        .build()

    return JavaFile.builder(apiPackage, interfaceSpec)
        .skipJavaLangImports(true)
        .build()
  }

  private fun toOperationMethod(operation: JavaOperation, typesafeResponseClass: TypeSpec): MethodSpec {
    val parameters = operation.parameters.map(::toParameterSpec)

    return MethodSpec.methodBuilder(operation.javaMethodName)
        .doIfNotNull(operation.javadoc) { addJavadoc("\$L", it) }
        .addAnnotation(httpMethodAnnotation(operation.httpMethod))
        .addAnnotation(pathAnnotation(operation.path))
        .doIf(operation.requestBodyMediaTypes.isNotEmpty()) { addAnnotation(consumesAnnotation(operation.requestBodyMediaTypes)) }
        .addAnnotation(producesAnnotation(operation.responses))
        .addModifiers(PUBLIC, ABSTRACT)
        .returns(typesafeResponseClass.name.toTypeName())
        .addParameters(parameters)
        .build()
  }

  private fun toParameterSpec(parameter: JavaParameter): ParameterSpec {
    val typeValidationAnnotations = parameter.javaType.validations.map(GeneratorCommon::toAnnotation)

    return ParameterSpec.builder(parameter.javaType.toTypeName(), parameter.javaParameterName)
        .doIfNotNull(parameter as? JavaRegularParameter) { addAnnotation(paramAnnotation(it)) }
        .doIf(parameter.required) { addAnnotation(NOT_NULL_ANNOTATION) }
        .addAnnotations(typeValidationAnnotations)
        .build()
  }

  private fun paramAnnotation(parameter: JavaRegularParameter): AnnotationSpec {
    val annotationName = when (parameter.location) {
      QUERY -> "QueryParam"
      HEADER -> "HeaderParam"
      PATH -> "PathParam"
      COOKIE -> "CookieParam"
    }

    return toAnnotation("javax.ws.rs.$annotationName", parameter.name)
  }

  private fun pathAnnotation(path: String) = toAnnotation("javax.ws.rs.Path", path)

  private fun httpMethodAnnotation(method: String) = toAnnotation("javax.ws.rs.${method.uppercase()}")

  private fun producesAnnotation(responses: List<JavaResponse>): AnnotationSpec {
    val mediaTypes = responses.flatMap { response -> response.contents.map { it.mediaType } }
        .sorted()
        .distinct()

    return toAnnotation("javax.ws.rs.Produces", mediaTypes)
  }

  private fun consumesAnnotation(mediaTypes: List<String>): AnnotationSpec {
    return toAnnotation("javax.ws.rs.Consumes", mediaTypes.sorted())
  }

  private fun toTypesafeResponseClass(operation: JavaOperation): TypeSpec {
    val jaxRsResponseTypeName = "javax.ws.rs.core.Response".toTypeName()
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
        .returns(className.toTypeName())
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
        .returns(className.toTypeName())
        .addParameter(content.javaType.toTypeName(), "entity")
        .addStatement("return new \$N(Response.status(\$L).header(\"Content-Type\", \$S).entity(entity).build())", className, statusCode, content.mediaType)
        .build()
  }

  private fun toTypesafeEmptyResponseMethod(response: JavaResponse, className: String): MethodSpec {
    val statusCode = (response.statusCode as StatusCode).code
    val methodName = "with$statusCode"

    return MethodSpec.methodBuilder(methodName)
        .addModifiers(PUBLIC, STATIC)
        .returns(className.toTypeName())
        .addStatement("return new \$N(Response.status(\$L).build())", className, statusCode)
        .build()
  }

  private fun toTypesafeDefaultResponseMethod(content: JavaContent, className: String): MethodSpec {
    val mediaTypeAsIdentifier = content.mediaType.mediaTypeToJavaIdentifier()
    val methodName = "with$mediaTypeAsIdentifier"

    return MethodSpec.methodBuilder(methodName)
        .addModifiers(PUBLIC, STATIC)
        .returns(className.toTypeName())
        .addParameter(Integer.TYPE, "status")
        .addParameter(content.javaType.toTypeName(), "entity")
        .addStatement("return new \$N(Response.status(status).header(\"Content-Type\", \$S).entity(entity).build())", className, content.mediaType)
        .build()
  }

  private fun writeResponseWrapperClass() {
    TemplateFileWriter(configuration).writeTemplateFile(supportPackage, "$RESPONSE_WRAPPER_CLASS_NAME.java")
  }

  companion object {

    const val API_PACKAGE = "resources"
    const val RESPONSE_WRAPPER_CLASS_NAME = "ResponseWrapper"
  }
}
