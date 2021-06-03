package de.rk42.openapi.codegen

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeSpec
import de.rk42.openapi.codegen.model.ParameterLocation.COOKIE
import de.rk42.openapi.codegen.model.ParameterLocation.HEADER
import de.rk42.openapi.codegen.model.ParameterLocation.PATH
import de.rk42.openapi.codegen.model.ParameterLocation.QUERY
import de.rk42.openapi.codegen.model.java.JavaOperation
import de.rk42.openapi.codegen.model.java.JavaOperationGroup
import de.rk42.openapi.codegen.model.java.JavaParameter
import de.rk42.openapi.codegen.model.java.JavaSpecification
import java.io.File
import javax.lang.model.element.Modifier

/**
 * Generates the code for the server stubs.
 */
class ServerStubGenerator(private val configuration: CliConfiguration) {

  fun generateCode(specification: JavaSpecification) {
    val javaFiles = specification.operationGroups.map(::toJavaInterface)
    writeFiles(javaFiles)
  }

  private fun writeFiles(files: List<JavaFile>) {
    val outputDir = File(configuration.outputDir)
    outputDir.mkdirs()

    files.forEach {
      it.writeTo(outputDir)
    }
  }

  private fun toJavaInterface(operationGroup: JavaOperationGroup): JavaFile {
    val methodSpecs = operationGroup.operations.map(::toOperationMethod)

    val interfaceSpec = TypeSpec.interfaceBuilder(operationGroup.javaIdentifier)
        .addModifiers(Modifier.PUBLIC)
        .addAnnotation(pathAnnotation(""))
        .addMethods(methodSpecs)
        .build()

    return JavaFile.builder(apiPackage(), interfaceSpec)
        .skipJavaLangImports(true)
        .build()
  }

  private fun toOperationMethod(operation: JavaOperation): MethodSpec {
    val parameters = operation.parameters.map(::toParameterSpec)
    
    return MethodSpec.methodBuilder(operation.javaIdentifier)
        .addAnnotation(httpMethodAnnotation(operation.method))
        .addAnnotation(pathAnnotation(operation.path))
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
        .returns(ClassName.get("javax.ws.rs.core", "Response"))
        .addParameters(parameters)
        .build()
  }

  private fun toParameterSpec(parameter: JavaParameter): ParameterSpec {
    return ParameterSpec.builder(ClassName.bestGuess(parameter.javaType.typeName), parameter.javaIdentifier)
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

  private fun pathAnnotation(path: String) = AnnotationSpec.builder(ClassName.get("javax.ws.rs", "Path"))
      .addMember("value", "\$S", path)
      .build()

  private fun httpMethodAnnotation(method: String) = AnnotationSpec.builder(ClassName.get("javax.ws.rs", method.uppercase())).build()

  private fun apiPackage() = "${configuration.sourcePackage}.$API_PACKAGE"

  companion object {

    const val API_PACKAGE = "api"
  }
}