package de.rk42.openapi.codegen.java.generator.client

import com.squareup.javapoet.*
import de.rk42.openapi.codegen.Configuration
import de.rk42.openapi.codegen.java.generator.GeneratorCommon.toTypeName
import de.rk42.openapi.codegen.java.generator.JavapoetExtensions.doIfNotNull
import de.rk42.openapi.codegen.java.model.*
import de.rk42.openapi.codegen.model.DefaultStatusCode
import de.rk42.openapi.codegen.model.StatusCode
import java.io.File
import javax.lang.model.element.Modifier

class ClientGenerator(private val configuration: Configuration) {

  private val outputDir = File(configuration.outputDir)
  private val apiPackage = "${configuration.sourcePackage}.$API_PACKAGE"

  fun generateCode(specification: JavaSpecification) {
    specification.operationGroups.asSequence()
        .map(::toRestClientClass)
        .forEach { it.writeTo(outputDir) }

    specification.operationGroups
        .flatMap { it.operations }
        .flatMap { it.responseTypesBySuccess().failureTypes }
        .distinct()
        .map { toRestClientEntityException(it) }
        .forEach { it.writeTo(outputDir) }
  }

  private fun toRestClientClass(operationGroup: JavaOperationGroup): JavaFile {
    val methodSpecs = operationGroup.operations.flatMap(::toMethodsForOperation)

    val supportFieldSpec = FieldSpec.builder(SupportTypes.RestClientSupport, "support", Modifier.PRIVATE, Modifier.FINAL).build()

    val constructorSpec = MethodSpec.constructorBuilder()
        .addModifiers(Modifier.PUBLIC)
        .addParameter(SupportTypes.RestClientSupport, "support")
        .addStatement("this.support = support")
        .build()

    val classSpec = TypeSpec.classBuilder(operationGroup.javaIdentifier + CLIENT_CLASS_NAME_SUFFIX)
        .addModifiers(Modifier.PUBLIC)
        .addField(supportFieldSpec)
        .addMethod(constructorSpec)
        .addMethods(methodSpecs)
        .build()

    return JavaFile.builder(apiPackage, classSpec)
        .skipJavaLangImports(true)
        .build()
  }

  private fun toMethodsForOperation(operation: JavaOperation): List<MethodSpec> {
    val methodWithResponse = toMethodWithResponse(operation)

    val (successTypes, failureTypes) = operation.responseTypesBySuccess()

    return if (successTypes.size <= 1 && failureTypes.size <= 1) {
      val simplifiedMethod = toSimplifiedMethod(operation, successTypes.firstOrNull(), failureTypes.firstOrNull())
      listOf(simplifiedMethod, methodWithResponse)
    } else {
      listOf(methodWithResponse)
    }
  }

  private fun toMethodWithResponse(operation: JavaOperation): MethodSpec {
    val parameters = operation.parameters.map(::toParameterSpec)
    val code = createMethodWithResponseCode(operation)

    return MethodSpec.methodBuilder(operation.javaIdentifier + "WithResponse")
        .doIfNotNull(operation.javadoc) { addJavadoc(it) }
        .addModifiers(Modifier.PUBLIC)
        .returns(SupportTypes.GenericResponse)
        .addParameters(parameters)
        .addException(SupportTypes.RestClientIoException)
        .addException(SupportTypes.RestClientValidationException)
        .addCode(code)
        .build()
  }

  private fun createMethodWithResponseCode(operation: JavaOperation): CodeBlock {
    val codeBuilder = CodeBlock.builder()

    codeBuilder.add("\n")
    codeBuilder.addStatement("\$1T builder = new \$1T(\$2S, \$3S)", SupportTypes.OperationBuilder, operation.path, operation.method.uppercase())
    codeBuilder.add("\n")

    // Add parameters (and request body) to operation builder.
    operation.parameters.forEach { param ->
      when (param) {
        is JavaRegularParameter ->
          codeBuilder.addStatement(
              "builder.parameter(\$S, \$T.\$L, \$L, \$N)",
              param.name,
              SupportTypes.ParameterLocation,
              param.location.name,
              param.required,
              param.javaIdentifier
          )
        is JavaBodyParameter ->
          codeBuilder.addStatement(
              "builder.requestBody(\$S, \$L, \$N)",
              param.mediaType,
              param.required,
              param.javaIdentifier
          )
      }
    }

    if (operation.parameters.isNotEmpty()) {
      codeBuilder.add("\n")
    }

    // Add response definitions to operation builder.
    operation.responses.forEach { response ->
      val statusCodeExpression = when (val statusCode = response.statusCode) {
        is StatusCode -> CodeBlock.of("\$T.of(\$L)", SupportTypes.StatusCode, statusCode.code)
        is DefaultStatusCode -> CodeBlock.of("\$T.DEFAULT", SupportTypes.StatusCode)
      }

      if (response.contents.isEmpty()) {
        codeBuilder.addStatement("builder.response(\$L)", statusCodeExpression)
      } else {
        response.contents.forEach { content ->
          codeBuilder.addStatement("builder.response(\$L, \$S, \$L)", statusCodeExpression, content.mediaType, toTypeTokenExpression(content.javaType))
        }
      }
    }

    if (operation.responses.isNotEmpty()) {
      codeBuilder.add("\n")
    }

    codeBuilder.addStatement("return support.executeRequest(builder.build())")

    return codeBuilder.build()
  }

  private fun toTypeTokenExpression(javaType: JavaAnyType): CodeBlock = when {
    javaType.isGenericType -> CodeBlock.of(
        "new \$T<\$T>(){}.getType()",
        "com.google.gson.reflect.TypeToken".toTypeName(),
        javaType.toTypeName()
    )
    else -> CodeBlock.of("\$T.class", javaType.toTypeName())
  }

  private fun toParameterSpec(parameter: JavaParameter): ParameterSpec {
    return ParameterSpec.builder(parameter.javaType.toTypeName(), parameter.javaIdentifier).build()
  }

  private fun toSimplifiedMethod(operation: JavaOperation, successType: JavaAnyType?, failureType: JavaAnyType?): MethodSpec {
    val parameters = operation.parameters.map(::toParameterSpec)
    val code = createSimplifiedMethodCode(operation, successType, failureType)

    return MethodSpec.methodBuilder(operation.javaIdentifier)
        .doIfNotNull(operation.javadoc) { addJavadoc(it) }
        .addModifiers(Modifier.PUBLIC)
        .doIfNotNull(successType) { returns(it.toTypeName()) }
        .addParameters(parameters)
        .addException(SupportTypes.RestClientIoException)
        .addException(SupportTypes.RestClientValidationException)
        .addException(SupportTypes.RestClientUndefinedResponseException)
        .addCode(code)
        .build()
  }

  private fun createSimplifiedMethodCode(operation: JavaOperation, successType: JavaAnyType?, failureType: JavaAnyType?): CodeBlock {
    val codeBuilder = CodeBlock.builder()

    codeBuilder.add("\n")
    codeBuilder.addStatement(
        "\$T genericResponse = \$N(\$L)",
        SupportTypes.GenericResponse,
        "${operation.javaIdentifier}WithResponse",
        operation.parameters.joinToString(", ") { it.javaIdentifier }
    )
    codeBuilder.addStatement("\$T response = genericResponse.asExpectedResponse()", SupportTypes.DefinedResponse)
    codeBuilder.add("\n")

    if (failureType != null) {
      codeBuilder.beginControlFlow("if (!response.isSuccessful())")
      codeBuilder.addStatement(
          "throw new \$T(response.getStatusCode(), (\$T) response.getEntity())",
          entityExceptionTypeName(failureType),
          failureType.toTypeName()
      )
      codeBuilder.endControlFlow()
    }

    if (successType != null) {
      codeBuilder.add("\n")
      codeBuilder.addStatement("return (\$T) response.getEntity()", successType.toTypeName())
    }

    return codeBuilder.build()
  }

  private fun toRestClientEntityException(entityType: JavaAnyType): JavaFile {
    val constructorSpec = MethodSpec.constructorBuilder()
        .addModifiers(Modifier.PUBLIC)
        .addParameter(Integer.TYPE, "httpStatusCode")
        .addParameter(entityType.toTypeName(), "entity")
        .addStatement("super(httpStatusCode, entity)")
        .build()

    val methodSpecGetEntity = MethodSpec.methodBuilder("getEntity")
        .addAnnotation("java.lang.Override".toTypeName())
        .addModifiers(Modifier.PUBLIC)
        .returns(entityType.toTypeName())
        .addStatement("return (\$T) super.getEntity()", entityType.toTypeName())
        .build()

    val classSpec = TypeSpec.classBuilder(entityExceptionTypeName(entityType))
        .addJavadoc("Exception for the error entity of type ${entityType.name}.")
        .addModifiers(Modifier.PUBLIC)
        .superclass(SupportTypes.RestClientDefinedErrorException)
        .addMethod(constructorSpec)
        .addMethod(methodSpecGetEntity)
        .build()

    return JavaFile.builder(apiPackage, classSpec)
        .skipJavaLangImports(true)
        .build()
  }

  private fun entityExceptionTypeName(entityType: JavaAnyType): ClassName = ClassName.get(apiPackage, "RestClient${entityType.name}EntityException")

  object SupportTypes {

    val RestClientSupport = "$SUPPORT_PACKAGE.RestClientSupport".toTypeName()
    val StatusCode = "$SUPPORT_PACKAGE.model.StatusCode".toTypeName()
    val OperationBuilder = "$SUPPORT_PACKAGE.model.Operation.Builder".toTypeName()
    val ParameterLocation = "$SUPPORT_PACKAGE.model.ParameterLocation".toTypeName()
    val DefinedResponse = "$SUPPORT_PACKAGE.DefinedResponse".toTypeName()
    val GenericResponse = "$SUPPORT_PACKAGE.GenericResponse".toTypeName()
    val RestClientIoException = "$SUPPORT_PACKAGE.RestClientIoException".toTypeName()
    val RestClientValidationException = "$SUPPORT_PACKAGE.RestClientValidationException".toTypeName()
    val RestClientUndefinedResponseException = "$SUPPORT_PACKAGE.RestClientUndefinedResponseException".toTypeName()
    val RestClientDefinedErrorException = "$SUPPORT_PACKAGE.RestClientDefinedErrorException".toTypeName()
  }

  companion object {

    const val SUPPORT_PACKAGE = "de.rk42.openapi.codegen.client"
    const val API_PACKAGE = "resources"
    const val CLIENT_CLASS_NAME_SUFFIX = "RestClient"
  }
}