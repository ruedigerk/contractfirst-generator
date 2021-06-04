package de.rk42.openapi.codegen

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeSpec
import de.rk42.openapi.codegen.JavaTypes.toTypeName
import de.rk42.openapi.codegen.Names.capitalize
import de.rk42.openapi.codegen.model.ParameterLocation.COOKIE
import de.rk42.openapi.codegen.model.ParameterLocation.HEADER
import de.rk42.openapi.codegen.model.ParameterLocation.PATH
import de.rk42.openapi.codegen.model.ParameterLocation.QUERY
import de.rk42.openapi.codegen.model.contract.StatusCode
import de.rk42.openapi.codegen.model.java.JavaOperation
import de.rk42.openapi.codegen.model.java.JavaOperationGroup
import de.rk42.openapi.codegen.model.java.JavaParameter
import de.rk42.openapi.codegen.model.java.JavaResponse
import de.rk42.openapi.codegen.model.java.JavaResponseContent
import de.rk42.openapi.codegen.model.java.JavaSpecification
import java.io.File
import java.io.InputStream
import javax.lang.model.element.Modifier
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
    outputDir.mkdirs()
    
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
        .addAnnotation(httpMethodAnnotation(operation.method))
        .addAnnotation(pathAnnotation(operation.path))
        .addAnnotation(producesAnnotation(operation.responses))
        .addModifiers(PUBLIC, Modifier.ABSTRACT)
        .returns(typesafeResponseClass.name.toTypeName())
        .addParameters(parameters)
        .build()
  }

  private fun toParameterSpec(parameter: JavaParameter): ParameterSpec {
    return ParameterSpec.builder(parameter.javaType.toTypeName(), parameter.javaIdentifier)
        .addAnnotation(paramAnnotation(parameter))
        .build()
  }

  private fun paramAnnotation(parameter: JavaParameter): AnnotationSpec {
    val annotationName = when (parameter.location) {
      QUERY -> "QueryParam"
      HEADER -> "HeaderParam"
      PATH -> "PathParam"
      COOKIE -> "CookieParam"
    }

    return AnnotationSpec.builder(ClassName.get("javax.ws.rs", annotationName))
        .addMember("value", "\$S", parameter.name)
        .build()
  }

  private fun pathAnnotation(path: String) = AnnotationSpec.builder("javax.ws.rs.Path".toTypeName())
      .addMember("value", "\$S", path)
      .build()

  private fun httpMethodAnnotation(method: String) = AnnotationSpec.builder(ClassName.get("javax.ws.rs", method.uppercase())).build()

  private fun producesAnnotation(responses: List<JavaResponse>): AnnotationSpec {
    val mediaTypes = responses.flatMap { response -> response.contents.map { it.mediaType } }
        .sorted()
        .distinct()

    val builder = AnnotationSpec.builder("javax.ws.rs.Produces".toTypeName())
    mediaTypes.forEach { builder.addMember("value", "\$S", it) }
    return builder.build()
  }

  private fun toTypesafeResponseClass(operation: JavaOperation): TypeSpec {
    val jaxRsResponseTypeName = "javax.ws.rs.core.Response".toTypeName()
    val className = operation.javaIdentifier.capitalize() + "Response"

    val nonEmptyResponseMethods = operation.responses
        .filter { it.statusCode is StatusCode }
        .flatMap { response -> response.contents.map { content -> toTypesafeResponseMethod(response, content, className) } }

    val emptyResponseMethods = operation.responses
        .filter { it.contents.isEmpty() }
        .map { toTypesafeEmptyResponseMethod(it, className) }

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
        .addMethods(nonEmptyResponseMethods)
        .addMethods(emptyResponseMethods)
        .addMethod(customResponseMethod)
        .build()
  }

  private fun toTypesafeResponseMethod(response: JavaResponse, content: JavaResponseContent, className: String): MethodSpec {
    val statusCode = (response.statusCode as StatusCode).code
    val mediaTypeAsIdentifier = Names.mediaTypeToJavaIdentifier(content.mediaType)
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

    const val API_PACKAGE = "api"
    const val RESPONSE_WRAPPER_CLASS_NAME = "ResponseWrapper"
  }
}
