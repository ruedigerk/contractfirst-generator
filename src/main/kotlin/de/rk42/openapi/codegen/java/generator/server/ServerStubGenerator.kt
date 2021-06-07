package de.rk42.openapi.codegen.java.generator.server

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeSpec
import de.rk42.openapi.codegen.CliConfiguration
import de.rk42.openapi.codegen.java.Identifiers.capitalize
import de.rk42.openapi.codegen.java.Identifiers.mediaTypeToJavaIdentifier
import de.rk42.openapi.codegen.java.JavaTypes.toTypeName
import de.rk42.openapi.codegen.java.JavapoetHelper.doIf
import de.rk42.openapi.codegen.java.JavapoetHelper.doIfNotNull
import de.rk42.openapi.codegen.java.JavapoetHelper.toAnnotation
import de.rk42.openapi.codegen.java.model.JavaContent
import de.rk42.openapi.codegen.java.model.JavaOperation
import de.rk42.openapi.codegen.java.model.JavaOperationGroup
import de.rk42.openapi.codegen.java.model.JavaParameter
import de.rk42.openapi.codegen.java.model.JavaRegularParameterLocation
import de.rk42.openapi.codegen.java.model.JavaResponse
import de.rk42.openapi.codegen.java.model.JavaSpecification
import de.rk42.openapi.codegen.model.DefaultStatusCode
import de.rk42.openapi.codegen.model.ParameterLocation.COOKIE
import de.rk42.openapi.codegen.model.ParameterLocation.HEADER
import de.rk42.openapi.codegen.model.ParameterLocation.PATH
import de.rk42.openapi.codegen.model.ParameterLocation.QUERY
import de.rk42.openapi.codegen.model.StatusCode
import java.io.File
import java.io.InputStream
import javax.lang.model.element.Modifier.ABSTRACT
import javax.lang.model.element.Modifier.PRIVATE
import javax.lang.model.element.Modifier.PUBLIC
import javax.lang.model.element.Modifier.STATIC

/**
 * Generates the code for the server stubs.
 */
class ServerStubGenerator(private val configuration: CliConfiguration) {

  private val outputDir = File(configuration.outputDir)
  private val apiPackage = "${configuration.sourcePackage}.$API_PACKAGE"
  private val supportPackage = "$apiPackage.support"

  fun generateCode(specification: JavaSpecification) {
    specification.operationGroups.asSequence()
        .map(::toJavaInterface)
        .forEach { it.writeTo(outputDir) }

    writeResponseWrapperFile()
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

    return MethodSpec.methodBuilder(operation.javaIdentifier)
        .doIfNotNull(operation.javadoc) { addJavadoc(it) }
        .addAnnotation(httpMethodAnnotation(operation.method))
        .addAnnotation(pathAnnotation(operation.path))
        .doIf(operation.requestBodyMediaTypes.isNotEmpty()) { addAnnotation(consumesAnnotation(operation.requestBodyMediaTypes)) }
        .addAnnotation(producesAnnotation(operation.responses))
        .addModifiers(PUBLIC, ABSTRACT)
        .returns(typesafeResponseClass.name.toTypeName())
        .addParameters(parameters).build()
  }

  private fun toParameterSpec(parameter: JavaParameter): ParameterSpec {
    return ParameterSpec.builder(parameter.javaType.toTypeName(), parameter.javaIdentifier)
        .doIfNotNull(parameter.location as? JavaRegularParameterLocation) { addAnnotation(paramAnnotation(it)) }
        .doIf(parameter.required) { addAnnotation(toAnnotation("javax.validation.constraints.NotNull")) }
        .doIf(parameter.javaType.isValidated) { addAnnotation(toAnnotation("javax.validation.Valid")) }.build()
  }

  private fun paramAnnotation(parameter: JavaRegularParameterLocation): AnnotationSpec {
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
    val className = operation.javaIdentifier.capitalize() + "Response"

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

  private fun writeResponseWrapperFile() {
    val outputDirectory = File(configuration.outputDir)
    val supportDirectory = File(outputDirectory, supportPackage.replace('.', '/'))

    supportDirectory.mkdirs()

    File(supportDirectory, "$RESPONSE_WRAPPER_CLASS_NAME.java").outputStream().buffered().use { outputStream ->
      with(outputStream.writer()) {
        write("package $supportPackage;\n\n")
        flush()
      }

      loadResource("/de/rk42/openapi/codegen/templates/$RESPONSE_WRAPPER_CLASS_NAME.java").use { it.transferTo(outputStream) }
    }
  }

  private fun loadResource(location: String): InputStream = javaClass.getResourceAsStream(location)
      ?: throw IllegalStateException("Resource file $location not found")

  companion object {

    const val API_PACKAGE = "resources"
    const val RESPONSE_WRAPPER_CLASS_NAME = "ResponseWrapper"
  }
}
