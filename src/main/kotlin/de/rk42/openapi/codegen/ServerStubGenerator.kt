package de.rk42.openapi.codegen

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeSpec
import de.rk42.openapi.codegen.model.CodeOperation
import de.rk42.openapi.codegen.model.CodeUnit
import de.rk42.openapi.codegen.model.ParameterModel
import java.io.File
import javax.lang.model.element.Modifier

/**
 * Generates the code for the server stubs.
 */
class ServerStubGenerator(private val configuration: Configuration) {

  fun generateCode(codeUnits: List<CodeUnit>) {
    writeFiles(codeUnits.map(::toJavaFiles))
  }

  private fun writeFiles(files: List<JavaFile>) {
    val outputDir = File(configuration.outputDir)
    outputDir.mkdirs()

    files.forEach {
      it.writeTo(outputDir)
    }
  }

  private fun toJavaFiles(codeUnit: CodeUnit): JavaFile {
    val interfaceName = codeUnit.name.toJavaTypeIdentifier() + INTERFACE_NAME_SUFFIX

    val methodSpecs = codeUnit.operations.map(::toOperationMethod)

    val interfaceSpec = TypeSpec.interfaceBuilder(interfaceName)
        .addModifiers(Modifier.PUBLIC)
        .addAnnotation(pathAnnotation(""))
        .addMethods(methodSpecs)
        .build()

    return JavaFile.builder(apiPackage(), interfaceSpec)
        .build()
  }

  private fun toOperationMethod(operation: CodeOperation): MethodSpec {
//    val parameters = operation.parameters.map(::toParameterSpec)
    return MethodSpec.methodBuilder(operation.operationId)
        .addAnnotation(httpMethodAnnotation(operation.method))
        .addAnnotation(pathAnnotation(operation.path))
        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
        .returns(ClassName.get("javax.ws.rs.core", "Response"))
//        .addParameters(parameters)
        .build()
  }

  private fun toParameterSpec(parameter: ParameterModel): ParameterSpec {
    TODO("Not implemented")
  }

  private fun pathAnnotation(path: String) = AnnotationSpec.builder(ClassName.get("javax.ws.rs", "Path"))
      .addMember("value", "\$S", path)
      .build()

  private fun httpMethodAnnotation(method: String) = AnnotationSpec.builder(ClassName.get("javax.ws.rs", method.uppercase())).build()

  private fun apiPackage() = "${configuration.sourcePackage}.$API_PACKAGE"

  private fun String.toJavaIdentifier(): String = this
      .replace(INVALID_IDENTIFIER_PATTERN, "_")
      .replace(CONSECUTIVE_UNDERSCORES, "_")
      .let { if (it.first().isDigit()) "_$it" else it }

  private fun String.toJavaTypeIdentifier(): String = this
      .toJavaIdentifier()
      .replaceFirstChar(Char::uppercase)

  companion object {

    val INVALID_IDENTIFIER_PATTERN = Regex("[^_a-zA-Z0-9]")
    val CONSECUTIVE_UNDERSCORES = Regex("[_]{2,}")

    val API_PACKAGE = "api"
    val INTERFACE_NAME_SUFFIX = "Api"
  }
}