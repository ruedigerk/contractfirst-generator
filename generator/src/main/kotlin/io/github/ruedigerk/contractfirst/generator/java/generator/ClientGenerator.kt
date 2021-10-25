package io.github.ruedigerk.contractfirst.generator.java.generator

import com.squareup.javapoet.*
import io.github.ruedigerk.contractfirst.generator.Configuration
import io.github.ruedigerk.contractfirst.generator.java.Identifiers.toJavaConstant
import io.github.ruedigerk.contractfirst.generator.java.Identifiers.toJavaTypeIdentifier
import io.github.ruedigerk.contractfirst.generator.java.generator.GeneratorCommon.toAnnotation
import io.github.ruedigerk.contractfirst.generator.java.generator.GeneratorCommon.toTypeName
import io.github.ruedigerk.contractfirst.generator.java.generator.JavapoetExtensions.doIf
import io.github.ruedigerk.contractfirst.generator.java.generator.JavapoetExtensions.doIfNotNull
import io.github.ruedigerk.contractfirst.generator.java.model.*
import io.github.ruedigerk.contractfirst.generator.model.DefaultStatusCode
import io.github.ruedigerk.contractfirst.generator.model.StatusCode
import java.io.File
import java.util.*
import javax.lang.model.element.Modifier

/**
 * Generates the contract-specific code for an API client in Java.
 */
class ClientGenerator(configuration: Configuration) {

  private val outputDir = File(configuration.outputDir)
  private val apiPackage = "${configuration.outputJavaBasePackage}.$API_PACKAGE"

  fun generateCode(specification: JavaSpecification) {
    generateApiClientClasses(specification)
    generateErrorWithEntityExceptionClasses(specification)
  }

  private fun generateApiClientClasses(specification: JavaSpecification) {
    specification.operationGroups.asSequence()
        .map(::createApiClientClass)
        .forEach { it.writeTo(outputDir) }
  }

  private fun generateErrorWithEntityExceptionClasses(specification: JavaSpecification) {
    specification.operationGroups
        .flatMap { it.operations }
        .flatMap { it.failureTypes }
        .distinct()
        .map { createClassForErrorWithEntityException(it) }
        .forEach { it.writeTo(outputDir) }
  }

  private fun createApiClientClass(operationGroup: JavaOperationGroup): JavaFile {
    val genericTypeConstants = operationGroup.operations.flatMap { it.allReturnTypes }.filter { it.isGenericType }.map(::generateTypeTokenConstant)

    val supportFieldSpec = FieldSpec.builder(SupportTypes.RequestExecutor, "requestExecutor", Modifier.PRIVATE, Modifier.FINAL).build()
    val returningAnyResponseFieldSpec = FieldSpec.builder("ReturningAnyResponse".toTypeName(), "returningAnyResponse", Modifier.PRIVATE, Modifier.FINAL).build()
    val returningSuccessfulResponseFieldSpec = FieldSpec.builder(
        "ReturningSuccessfulResponse".toTypeName(),
        "returningSuccessfulResponse",
        Modifier.PRIVATE,
        Modifier.FINAL
    ).build()

    val constructorSpec = MethodSpec.constructorBuilder()
        .addModifiers(Modifier.PUBLIC)
        .addParameter(SupportTypes.RequestExecutor, "requestExecutor")
        .addStatement("this.requestExecutor = requestExecutor")
        .addStatement("this.returningAnyResponse = new ReturningAnyResponse()")
        .addStatement("this.returningSuccessfulResponse = new ReturningSuccessfulResponse()")
        .build()

    val returningAnyResponseGetterMethodSpec = MethodSpec.methodBuilder("returningAnyResponse")
        .addJavadoc("Selects methods returning instances of ApiResponse and not throwing exceptions for unsuccessful status codes.")
        .addModifiers(Modifier.PUBLIC)
        .returns("ReturningAnyResponse".toTypeName())
        .addStatement("return returningAnyResponse")
        .build()
    val returningSuccessfulResponseGetterMethodSpec = MethodSpec.methodBuilder("returningSuccessfulResponse")
        .addJavadoc("Selects methods returning instances of operation specific success classes and throwing exceptions for unsuccessful status codes.")
        .addModifiers(Modifier.PUBLIC)
        .returns("ReturningSuccessfulResponse".toTypeName())
        .addStatement("return returningSuccessfulResponse")
        .build()

    val methodSpecs = operationGroup.operations.map(::createSimplifiedMethod)

    val returningSuccessfulResponseSubclass = createClassReturningSuccessfulResponse(operationGroup)
    val returningAnyResponseSubclass = createClassReturningAnyResponse(operationGroup)
    val successfulResponseClasses = operationGroup.operations.map(::createClassForOperationSpecificSuccessfulResponse)

    val classSpec = TypeSpec.classBuilder(operationGroup.javaIdentifier + CLIENT_CLASS_NAME_SUFFIX)
        .addJavadoc("Contains methods for all API operations tagged \"${operationGroup.originalTag}\".")
        .addModifiers(Modifier.PUBLIC)
        .addFields(genericTypeConstants)
        .addField(supportFieldSpec)
        .addField(returningAnyResponseFieldSpec)
        .addField(returningSuccessfulResponseFieldSpec)
        .addMethod(constructorSpec)
        .addMethod(returningAnyResponseGetterMethodSpec)
        .addMethod(returningSuccessfulResponseGetterMethodSpec)
        .addMethods(methodSpecs)
        .addType(returningSuccessfulResponseSubclass)
        .addType(returningAnyResponseSubclass)
        .addTypes(successfulResponseClasses)
        .build()

    return JavaFile.builder(apiPackage, classSpec)
        .skipJavaLangImports(true)
        .build()
  }

  private fun generateTypeTokenConstant(type: JavaAnyType): FieldSpec {
    val initializer = CodeBlock.of(
        "new \$T<\$T>(){}.getType()",
        "com.google.gson.reflect.TypeToken".toTypeName(),
        type.toTypeName()
    )
    return FieldSpec.builder("java.lang.reflect.Type".toTypeName(), constantsNameForGenericType(type), Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
        .initializer(initializer)
        .build()
  }

  private fun constantsNameForGenericType(type: JavaAnyType): String = when (type) {
    is JavaCollectionType -> type.name.toJavaConstant() + "_OF_" + constantsNameForGenericType(type.elementType)
    is JavaMapType -> type.name.toJavaConstant() + "_OF_" + constantsNameForGenericType(type.valuesType)
    is JavaType -> type.name.toJavaConstant()
  }

  private fun createSimplifiedMethod(operation: JavaOperation): MethodSpec {
    val code = createCodeOfSimplifiedMethod(operation)
    val exceptions = getAllErrorWithEntityExceptionsFor(operation)

    val returnType = when {
      // There are multiple success entity types, so return the successful response object.  
      operation.successTypes.size > 1 -> typeNameOfSuccessfulResponse(operation)
      // There is a single entity type that all successful responses use or no entity at all.
      else -> operation.successTypes.firstOrNull()?.toTypeName()
    }

    return createMethodForOperation(operation, returnType, code, exceptions)
  }

  private fun createClassReturningAnyResponse(operationGroup: JavaOperationGroup): TypeSpec {
    val methodSpecs = operationGroup.operations.map { operation ->
      val code = createCodeOfMethodReturningAnyResponse(operation)
      createMethodForOperation(operation, SupportTypes.ApiResponse, code)
    }

    return TypeSpec.classBuilder("ReturningAnyResponse")
        .addJavadoc("Contains methods for all operations returning instances of ApiResponse and not throwing exceptions for unsuccessful status codes.")
        .addModifiers(Modifier.PUBLIC)
        .addMethods(methodSpecs)
        .build()
  }

  private fun createCodeOfMethodReturningAnyResponse(operation: JavaOperation): CodeBlock {
    val codeBuilder = CodeBlock.builder()

    codeBuilder.add("\n")
    codeBuilder.addStatement("\$1T builder = new \$1T(\$2S, \$3S)", SupportTypes.OperationBuilder, operation.path, operation.httpMethod.uppercase())
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
              param.javaParameterName
          )
        is JavaBodyParameter ->
          codeBuilder.addStatement(
              "builder.requestBody(\$S, \$L, \$N)",
              param.mediaType,
              param.required,
              param.javaParameterName
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
          codeBuilder.addStatement("builder.response(\$L, \$S, \$L)", statusCodeExpression, content.mediaType, toTypeExpression(content.javaType))
        }
      }
    }

    if (operation.responses.isNotEmpty()) {
      codeBuilder.add("\n")
    }

    codeBuilder.addStatement("return requestExecutor.executeRequest(builder.build())")

    return codeBuilder.build()
  }

  private fun createClassReturningSuccessfulResponse(operationGroup: JavaOperationGroup): TypeSpec {
    val methodSpecs = operationGroup.operations.map {
      val code = createCodeOfMethodReturningSuccessfulResponse(it)
      val exceptions = getAllErrorWithEntityExceptionsFor(it)
      createMethodForOperation(it, typeNameOfSuccessfulResponse(it), code, exceptions)
    }

    return TypeSpec.classBuilder("ReturningSuccessfulResponse")
        .addJavadoc("Contains methods for all operations returning instances of operation specific success classes and throwing exceptions for unsuccessful status codes.")
        .addModifiers(Modifier.PUBLIC)
        .addMethods(methodSpecs)
        .build()
  }

  private fun typeNameOfSuccessfulResponse(operation: JavaOperation): ClassName {
    return (operation.javaMethodName.toJavaTypeIdentifier() + "SuccessfulResponse").toTypeName()
  }

  private fun getAllErrorWithEntityExceptionsFor(operation: JavaOperation): List<TypeName> =
      operation.failureTypes.distinct().map(::errorWithEntityExceptionClassName)

  private fun createMethodForOperation(
      operation: JavaOperation,
      returnType: TypeName?,
      code: CodeBlock,
      additionalExceptions: List<TypeName> = emptyList()
  ): MethodSpec {
    val parameters = operation.parameters.map(::toParameterSpec)

    return MethodSpec.methodBuilder(operation.javaMethodName)
        .doIfNotNull(operation.javadoc) { addJavadoc("\$L", it) }
        .addModifiers(Modifier.PUBLIC)
        .doIfNotNull(returnType) { returns(returnType) }
        .addParameters(parameters)
        .addException(SupportTypes.ApiClientIoException)
        .addException(SupportTypes.ApiClientValidationException)
        .addException(SupportTypes.ApiClientIncompatibleResponseException)
        .addExceptions(additionalExceptions)
        .addCode(code)
        .build()
  }

  private fun createCodeOfMethodReturningSuccessfulResponse(operation: JavaOperation): CodeBlock {
    val codeBuilder = CodeBlock.builder()

    codeBuilder.add("\n")
    codeBuilder.addStatement(
        "\$T response = returningAnyResponse.\$N(\$L)",
        SupportTypes.ApiResponse,
        operation.javaMethodName,
        operation.parameters.joinToString(", ") { it.javaParameterName })
    codeBuilder.add("\n")

    if (operation.failureTypes.isNotEmpty()) {
      codeBuilder.add(createCodeForThrowingErrorWithEntityExceptions(operation.failureTypes.toList()))
    }

    codeBuilder.addStatement("return new \$T(response)", typeNameOfSuccessfulResponse(operation))

    return codeBuilder.build()
  }

  private fun createCodeForThrowingErrorWithEntityExceptions(failureTypes: List<JavaAnyType>): CodeBlock {
    val codeBuilder = CodeBlock.builder()

    fun addThrowsStatement(failureType: JavaAnyType) {
      codeBuilder.addStatement("throw new \$T(response)", errorWithEntityExceptionClassName(failureType))
    }

    codeBuilder.beginControlFlow("if (!response.isSuccessful())")

    if (failureTypes.size > 1) {
      failureTypes.dropLast(1).forEach { failureType ->
        codeBuilder.beginControlFlow("if (response.getEntityType() == \$L)", toTypeExpression(failureType))
        addThrowsStatement(failureType)
        codeBuilder.endControlFlow()
      }
    }

    addThrowsStatement(failureTypes.last())
    codeBuilder.endControlFlow()
    codeBuilder.add("\n")

    return codeBuilder.build()
  }

  private fun toTypeExpression(javaType: JavaAnyType): CodeBlock = when {
    javaType.isGenericType -> CodeBlock.of("\$L", constantsNameForGenericType(javaType))
    else -> CodeBlock.of("\$T.class", javaType.toTypeName())
  }

  private fun toParameterSpec(parameter: JavaParameter): ParameterSpec {
    return ParameterSpec.builder(parameter.javaType.toTypeName(), parameter.javaParameterName).build()
  }

  private fun createCodeOfSimplifiedMethod(operation: JavaOperation): CodeBlock {
    val codeBuilder = CodeBlock.builder()
    codeBuilder.add("\n")

    codeBuilder.addStatement(
        "\$T response = returningSuccessfulResponse.\$N(\$L)",
        typeNameOfSuccessfulResponse(operation),
        operation.javaMethodName,
        operation.parameters.joinToString(", ") { it.javaParameterName }
    )

    when {
      operation.successTypes.size > 1 -> {
        // There are multiple success entity types, so return the whole response object.
        codeBuilder.add("\n")
        codeBuilder.addStatement("return response")
      }
      operation.successTypes.size == 1 -> {
        // Return the one entity type that all successful responses use.
        codeBuilder.add("\n")
        codeBuilder.addStatement("return response.getEntity()")
      }
      else -> {
        // No operation success types -> successful responses all have no content, so method returns nothing.
      }
    }

    return codeBuilder.build()
  }

  private fun createClassForErrorWithEntityException(entityType: JavaAnyType): JavaFile {
    val constructorSpec = MethodSpec.constructorBuilder()
        .addModifiers(Modifier.PUBLIC)
        .addParameter(SupportTypes.ApiResponse, "response")
        .addStatement("super(response)")
        .build()

    val methodSpecGetEntity = MethodSpec.methodBuilder("getEntity")
        .addAnnotation("java.lang.Override".toTypeName())
        .addModifiers(Modifier.PUBLIC)
        .returns(entityType.toTypeName())
        .addStatement("return (\$T) super.getEntity()", entityType.toTypeName())
        .build()

    val classSpec = TypeSpec.classBuilder(errorWithEntityExceptionClassName(entityType))
        .addJavadoc("Exception for errors where the API returned an entity of type {@code ${entityType.name}}.")
        .addModifiers(Modifier.PUBLIC)
        .superclass(SupportTypes.ApiClientErrorWithEntityException)
        .addMethod(constructorSpec)
        .addMethod(methodSpecGetEntity)
        .build()

    return JavaFile.builder(apiPackage, classSpec)
        .skipJavaLangImports(true)
        .build()
  }

  private fun errorWithEntityExceptionClassName(entityType: JavaAnyType): ClassName = ClassName.get(
      apiPackage,
      "ApiClientErrorWith${entityType.name}EntityException"
  )

  private fun createClassForOperationSpecificSuccessfulResponse(operation: JavaOperation): TypeSpec {
    val className = typeNameOfSuccessfulResponse(operation)
    val responseFieldSpec = FieldSpec.builder(SupportTypes.ApiResponse, "response", Modifier.PRIVATE, Modifier.FINAL).build()

    val constructorSpec = MethodSpec.constructorBuilder()
        .addModifiers(Modifier.PUBLIC)
        .addParameter(SupportTypes.ApiResponse, "response")
        .addStatement("this.response = response")
        .build()

    val getApiResponseMethodSpec = MethodSpec.methodBuilder("getApiResponse")
        .addJavadoc("Returns the ApiResponse instance with the details of the operation's HTTP response.")
        .addModifiers(Modifier.PUBLIC)
        .returns(SupportTypes.ApiResponse)
        .addStatement("return response")
        .build()

    val statusQueryMethodSpecs = createStatusQueryMethods(operation)
    val entityGetterMethodSpecs = createEntityGetterMethods(operation)
    val equalsHashCodeAndToStringMethods = MethodsFromObject.generateEqualsHashCodeAndToString(className, listOf(responseFieldSpec))

    return TypeSpec.classBuilder(className)
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .addJavadoc("Represents a successful response of operation \$L, i.e., the status code being in range 200 to 299.", operation.javaMethodName)
        .addField(responseFieldSpec)
        .addMethod(constructorSpec)
        .addMethod(getApiResponseMethodSpec)
        .addMethods(statusQueryMethodSpecs)
        .addMethods(entityGetterMethodSpecs)
        .addMethods(equalsHashCodeAndToStringMethods)
        .build()
  }

  private fun createStatusQueryMethods(operation: JavaOperation): List<MethodSpec> {
    // The assumption is that all success responses are specified in the contract, if any is. Therefore, the default
    // status code can only contain successful responses, when no successful responses are specified in the contract.
    return operation.successResponses.flatMap { response ->
      when (val statusCode = response.statusCode) {
        // In case of the default status, allow querying for the entity types. If there are none, no method is needed and none is generated.
        DefaultStatusCode -> response.contents.map { it.javaType }.distinct().map(::createQueryMethodForDefaultStatus)
        is StatusCode -> if (response.contents.isEmpty()) {
          // If there is no content, allow querying for the status code.
          listOf(createQueryMethodForStatusCodeOnly(statusCode))
        } else {
          // There is at least one entity type, allow querying for the combination of status code and entity type.
          response.contents.map { it.javaType }.distinct().map { createQueryMethodForStatusCodeAndEntity(statusCode, it) }
        }
      }
    }
  }

  private fun createQueryMethodForDefaultStatus(entityType: JavaAnyType): MethodSpec {
    return MethodSpec.methodBuilder("isReturning${methodNameForEntityType(entityType)}")
        .addJavadoc("Returns whether the response's entity is of type {@code \$T}.", entityType.toTypeName())
        .addModifiers(Modifier.PUBLIC)
        .returns(TypeName.BOOLEAN)
        .addStatement("return response.getEntityType() == \$L", toTypeExpression(entityType))
        .build()
  }

  private fun createQueryMethodForStatusCodeAndEntity(statusCode: StatusCode, entityType: JavaAnyType): MethodSpec {
    return MethodSpec.methodBuilder("isStatus${statusCode.code}Returning${methodNameForEntityType(entityType)}")
        .addJavadoc(
            "Returns whether the response's status code is ${statusCode.code}, while the response's entity is of type {@code \$T}.",
            entityType.toTypeName()
        )
        .addModifiers(Modifier.PUBLIC)
        .returns(TypeName.BOOLEAN)
        .addStatement("return response.getStatusCode() == \$L && response.getEntityType() == \$L", statusCode.code, toTypeExpression(entityType))
        .build()
  }

  private fun createQueryMethodForStatusCodeOnly(statusCode: StatusCode): MethodSpec {
    return MethodSpec.methodBuilder("isStatus${statusCode.code}WithoutEntity")
        .addJavadoc("Returns whether the response's status code is ${statusCode.code}, while the response has no entity.")
        .addModifiers(Modifier.PUBLIC)
        .returns(TypeName.BOOLEAN)
        .addStatement("return response.getStatusCode() == \$L", statusCode.code)
        .build()
  }

  private fun methodNameForEntityType(type: JavaAnyType): String {
    return when (type) {
      is JavaCollectionType -> "${type.name}Of${type.elementType.name}"
      is JavaMapType -> "MapOfStringTo${type.valuesType.name}"
      is JavaType -> type.name
    }
  }

  private fun createEntityGetterMethods(operation: JavaOperation): List<MethodSpec> {
    return when {
      operation.successTypes.isEmpty() -> emptyList()
      operation.successTypes.size == 1 -> listOf(createUnnamedEntityGetterMethod(operation.successTypes.first()))
      else -> operation.successTypes.flatMap(::createNamedEntityGetterMethods)
    }
  }

  private fun createUnnamedEntityGetterMethod(type: JavaAnyType): MethodSpec {
    val typeName = type.toTypeName()
    return MethodSpec.methodBuilder("getEntity")
        .addJavadoc("Returns the response's entity of type {@code \$T}.", typeName)
        .doIf(type.isGenericType) { addAnnotation(toAnnotation("java.lang.SuppressWarnings", "unchecked")) }
        .addModifiers(Modifier.PUBLIC)
        .returns(typeName)
        .addStatement("return (\$T) response.getEntity()", typeName)
        .build()
  }

  private fun createNamedEntityGetterMethods(type: JavaAnyType): List<MethodSpec> {
    return listOf(createMethodGetEntityIf(type), createMethodGetEntityAs(type))
  }

  private fun createMethodGetEntityIf(type: JavaAnyType): MethodSpec {
    val typeName = type.toTypeName()
    return MethodSpec.methodBuilder("getEntityIf${methodNameForEntityType(type)}")
        .addJavadoc(
            "Returns the response's entity wrapped in {@code java.lang.Optional.of()} if it is of type {@code \$T}. Otherwise, returns {@code Optional.empty()}.",
            typeName
        )
        .addModifiers(Modifier.PUBLIC)
        .returns(ParameterizedTypeName.get(ClassName.get(Optional::class.java), typeName))
        .addStatement("return Optional.ofNullable(\$N())", nameForMethodGetEntityAs(type))
        .build()
  }

  private fun createMethodGetEntityAs(type: JavaAnyType): MethodSpec {
    val typeName = type.toTypeName()
    return MethodSpec.methodBuilder(nameForMethodGetEntityAs(type))
        .addJavadoc("Returns the response's entity if it is of type {@code \$T}. Otherwise, returns null.", typeName)
        .doIf(type.isGenericType) { addAnnotation(toAnnotation("java.lang.SuppressWarnings", "unchecked")) }
        .addModifiers(Modifier.PUBLIC)
        .returns(typeName)
        .beginControlFlow("if (response.getEntityType() == \$L)", toTypeExpression(type))
        .addStatement("return (\$T) response.getEntity()", typeName)
        .nextControlFlow("else")
        .addStatement("return null")
        .endControlFlow()
        .build()
  }

  private fun nameForMethodGetEntityAs(type: JavaAnyType) = "getEntityAs${methodNameForEntityType(type)}"

  object SupportTypes {

    val ApiResponse = "$SUPPORT_PACKAGE.ApiResponse".toTypeName()
    val OperationBuilder = "$SUPPORT_PACKAGE.internal.Operation.Builder".toTypeName()
    val ParameterLocation = "$SUPPORT_PACKAGE.internal.ParameterLocation".toTypeName()
    val RequestExecutor = "$SUPPORT_PACKAGE.RequestExecutor".toTypeName()
    val StatusCode = "$SUPPORT_PACKAGE.internal.StatusCode".toTypeName()
    val ApiClientErrorWithEntityException = "$SUPPORT_PACKAGE.ApiClientErrorWithEntityException".toTypeName()
    val ApiClientIoException = "$SUPPORT_PACKAGE.ApiClientIoException".toTypeName()
    val ApiClientValidationException = "$SUPPORT_PACKAGE.ApiClientValidationException".toTypeName()
    val ApiClientIncompatibleResponseException = "$SUPPORT_PACKAGE.ApiClientIncompatibleResponseException".toTypeName()
  }

  companion object {

    const val SUPPORT_PACKAGE = "io.github.ruedigerk.contractfirst.generator.client"
    const val API_PACKAGE = "api"
    const val CLIENT_CLASS_NAME_SUFFIX = "Client"
  }
}