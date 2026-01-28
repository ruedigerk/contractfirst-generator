package io.github.ruedigerk.contractfirst.generator.java.generator.clientgenerator

import com.squareup.javapoet.*
import io.github.ruedigerk.contractfirst.generator.java.Identifiers.toJavaConstant
import io.github.ruedigerk.contractfirst.generator.java.Identifiers.toJavaTypeIdentifier
import io.github.ruedigerk.contractfirst.generator.java.JavaConfiguration
import io.github.ruedigerk.contractfirst.generator.java.generator.Annotations.toAnnotation
import io.github.ruedigerk.contractfirst.generator.java.generator.JavaParameters
import io.github.ruedigerk.contractfirst.generator.java.generator.JavapoetExtensions.doIf
import io.github.ruedigerk.contractfirst.generator.java.generator.JavapoetExtensions.doIfNotNull
import io.github.ruedigerk.contractfirst.generator.java.generator.MethodsFromObject
import io.github.ruedigerk.contractfirst.generator.java.generator.TypeNames.toClassName
import io.github.ruedigerk.contractfirst.generator.java.generator.TypeNames.toTypeName
import io.github.ruedigerk.contractfirst.generator.java.model.*
import io.github.ruedigerk.contractfirst.generator.openapi.DefaultStatusCode
import io.github.ruedigerk.contractfirst.generator.openapi.StatusCode
import java.io.File
import java.util.*
import javax.lang.model.element.Modifier

/**
 * Generates the contract-specific code for an API client in Java.
 */
class ClientGenerator(configuration: JavaConfiguration) : (JavaSpecification) -> Unit {

  private val outputDir = File(configuration.outputDir)
  private val apiPackage = configuration.apiPackage

  override operator fun invoke(specification: JavaSpecification) {
    // The client does not generate cookie parameters. They have to be supplied by cookies in the HTTP client itself.
    val specWithoutCookieParameters = removeAllCookieParameters(specification)

    generateApiClientClasses(specWithoutCookieParameters)
    generateErrorWithEntityExceptionClasses(specWithoutCookieParameters)
  }

  private fun removeAllCookieParameters(specification: JavaSpecification): JavaSpecification =
      specification.copy(operationGroups = specification.operationGroups.map { group ->
        group.copy(operations = group.operations.map { operation ->
          operation.copy(parameters = operation.parameters.filterNot { it.isCookieParameter() })
        })
      })

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
    val genericTypeConstants = generateTypeTokenConstants(operationGroup)

    val requestExecutorFieldSpec = FieldSpec.builder(SupportTypes.ApiRequestExecutor, "requestExecutor", Modifier.PRIVATE, Modifier.FINAL).build()
    val returningResultFieldSpec = FieldSpec.builder("ReturningResult".toClassName(), "returningResult", Modifier.PRIVATE, Modifier.FINAL).build()

    val constructorSpec = MethodSpec.constructorBuilder()
        .addModifiers(Modifier.PUBLIC)
        .addParameter(SupportTypes.ApiRequestExecutor, "requestExecutor")
        .addStatement("this.requestExecutor = requestExecutor")
        .addStatement("this.returningResult = new ReturningResult()")
        .build()

    val returningSuccessfulResultGetterMethodSpec = MethodSpec.methodBuilder("returningResult")
        .addJavadoc("Returns an API client with methods that return operation specific result classes, allowing inspection of the operations' responses.")
        .addModifiers(Modifier.PUBLIC)
        .returns("ReturningResult".toClassName())
        .addStatement("return returningResult")
        .build()

    val methodSpecs = operationGroup.operations.map(::createSimplifiedMethod)

    val returningResultSubclass = createClassReturningResult(operationGroup)
    val operationSpecificResultClasses = operationGroup.operations.map(::createClassForOperationSpecificResult)

    val classSpec = TypeSpec.classBuilder(operationGroup.javaIdentifier + CLIENT_CLASS_NAME_SUFFIX)
        .addJavadoc("Contains methods for all API operations tagged \"${operationGroup.originalTag}\".")
        .addModifiers(Modifier.PUBLIC)
        .addFields(genericTypeConstants)
        .addField(requestExecutorFieldSpec)
        .addField(returningResultFieldSpec)
        .addMethod(constructorSpec)
        .addMethod(returningSuccessfulResultGetterMethodSpec)
        .addMethods(methodSpecs)
        .addType(returningResultSubclass)
        .addTypes(operationSpecificResultClasses)
        .build()

    return JavaFile.builder(apiPackage, classSpec)
        .skipJavaLangImports(true)
        .build()
  }

  private fun generateTypeTokenConstants(operationGroup: JavaOperationGroup): Set<FieldSpec> {
    return operationGroup.operations
        .flatMap { it.allReturnTypes }
        .filter { it.isGenericType }
        .map(::generateTypeTokenConstant)
        .toSet()
  }

  private fun generateTypeTokenConstant(type: JavaAnyType): FieldSpec {
    val initializer = CodeBlock.of(
        "new \$T<\$T>(){}.getType()",
        "com.google.gson.reflect.TypeToken".toClassName(),
        type.toTypeName()
    )
    return FieldSpec.builder("java.lang.reflect.Type".toClassName(), constantsNameForGenericType(type), Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
        .initializer(initializer)
        .build()
  }

  private fun constantsNameForGenericType(type: JavaAnyType): String = when (type) {
    is JavaCollectionType -> type.name.simpleName.toJavaConstant() + "_OF_" + constantsNameForGenericType(type.elementType)
    is JavaMapType -> type.name.simpleName.toJavaConstant() + "_OF_" + constantsNameForGenericType(type.valuesType)
    is JavaType -> type.name.simpleName.toJavaConstant()
  }

  private fun createSimplifiedMethod(operation: JavaOperation): MethodSpec {
    val code = createCodeOfSimplifiedMethod(operation)
    val exceptions = getAllErrorWithEntityExceptionsFor(operation)

    val returnType = when {
      // There are multiple success entity types, so return the successful response object.
      operation.successTypes.size > 1 -> typeNameOfResultClass(operation)
      // There is a single entity type that all successful responses use or no entity at all.
      else -> operation.successTypes.firstOrNull()?.toTypeName()
    }

    return createMethodForOperation(operation, returnType, code, exceptions)
  }

  private fun createClassReturningResult(operationGroup: JavaOperationGroup): TypeSpec {
    val methodSpecs = operationGroup.operations.map {
      val code = createCodeOfMethodReturningResult(it)
      createMethodForOperation(it, typeNameOfResultClass(it), code)
    }

    return TypeSpec.classBuilder("ReturningResult")
        .addJavadoc("Contains methods returning operation specific result classes, allowing inspection of the operations' responses.")
        .addModifiers(Modifier.PUBLIC)
        .addMethods(methodSpecs)
        .build()
  }

  private fun createCodeOfMethodReturningResult(operation: JavaOperation): CodeBlock {
    val codeBuilder = CodeBlock.builder()

    codeBuilder.add("\n")
    codeBuilder.addStatement("\$1T builder = new \$1T(\$2S, \$3S)", SupportTypes.OperationBuilder, operation.path, operation.httpMethod)
    codeBuilder.add("\n")

    // Add all parameters to the operation builder.
    operation.parameters.forEach { parameter ->
      when (parameter) {
        is JavaRegularParameter -> codeBuilder.addStatement(
            "builder.parameter(\$S, \$T.\$L, \$L, \$N)",
            parameter.originalName,
            SupportTypes.ParameterLocation,
            parameter.location.name,
            parameter.required,
            parameter.javaParameterName
        )

        is JavaBodyParameter -> codeBuilder.addStatement(
            "builder.requestBody(\$S, \$L, \$N)",
            parameter.mediaType,
            parameter.required,
            parameter.javaParameterName
        )

        is JavaMultipartBodyParameter -> codeBuilder.addStatement(
            "builder.requestBodyPart(\$T.\$L, \$S, \$N)",
            SupportTypes.BodyPartType,
            parameter.bodyPartType.name,
            parameter.originalName,
            parameter.javaParameterName
        )
      }
    }

    if (operation.parameters.any { it is JavaMultipartBodyParameter }) {
      codeBuilder.addStatement("builder.multipartRequestBody(\$S)", operation.requestBodyMediaType)
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

    codeBuilder.addStatement("\$T response = requestExecutor.executeRequest(builder.build())", SupportTypes.ApiResponse)
    codeBuilder.add("\n")
    codeBuilder.addStatement("return new \$T(response)", typeNameOfResultClass(operation))

    return codeBuilder.build()
  }

  private fun typeNameOfResultClass(operation: JavaOperation): ClassName {
    return (operation.javaMethodName.toJavaTypeIdentifier() + "Result").toClassName()
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

  private fun toTypeExpression(javaType: JavaAnyType): CodeBlock = when {
    javaType.isGenericType -> CodeBlock.of("\$L", constantsNameForGenericType(javaType))
    else -> CodeBlock.of("\$T.class", javaType.toTypeName())
  }

  private fun toParameterSpec(parameter: JavaParameter): ParameterSpec {
    val parameterType = JavaParameters.determineParameterType(parameter, AttachmentJavaTypeName)
    return ParameterSpec.builder(parameterType.toTypeName(), parameter.javaParameterName).build()
  }

  private fun createCodeOfSimplifiedMethod(operation: JavaOperation): CodeBlock {
    val codeBuilder = CodeBlock.builder()
    codeBuilder.add("\n")

    codeBuilder.addStatement(
        "\$T result = returningResult.\$N(\$L)",
        typeNameOfResultClass(operation),
        operation.javaMethodName,
        operation.parameters.joinToString(", ") { it.javaParameterName }
    )

    if (operation.failureTypes.isNotEmpty()) {
      codeBuilder.add("\n")
      codeBuilder.add(createCodeForThrowingErrorWithEntityExceptions(operation.failureTypes.toList()))
    }

    when {
      operation.successTypes.size > 1 -> {
        // There are multiple success entity types, so return the result instance itself.
        codeBuilder.add("\n")
        codeBuilder.addStatement("return result")
      }

      operation.successTypes.size == 1 -> when (operation.allReturnTypes.size) {
        // Return the one entity type that all successful responses use.
        1 -> {
          // There are no failure entity types, use getEntity method.
          codeBuilder.add("\n")
          codeBuilder.addStatement("return result.getEntity()")
        }

        else -> {
          // There are success and failure types, so use type-specific entity accessor method.
          codeBuilder.add("\n")
          codeBuilder.addStatement("return result.\$N()", nameForMethodGetEntityAs(operation.successTypes.first()))
        }
      }

      else -> {
        // No operation success types -> successful responses all have no content, so method returns nothing.
      }
    }

    return codeBuilder.build()
  }

  private fun createCodeForThrowingErrorWithEntityExceptions(failureTypes: List<JavaAnyType>): CodeBlock {
    val codeBuilder = CodeBlock.builder()

    fun addThrowsStatement(failureType: JavaAnyType) {
      codeBuilder.addStatement("throw new \$T(result.getResponse())", errorWithEntityExceptionClassName(failureType))
    }

    codeBuilder.beginControlFlow("if (!result.isSuccessful())")

    if (failureTypes.size > 1) {
      failureTypes.dropLast(1).forEach { failureType ->
        codeBuilder.beginControlFlow("if (result.getResponse().getEntityType() == \$L)", toTypeExpression(failureType))
        addThrowsStatement(failureType)
        codeBuilder.endControlFlow()
      }
    }

    addThrowsStatement(failureTypes.last())
    codeBuilder.endControlFlow()

    return codeBuilder.build()
  }

  private fun createClassForErrorWithEntityException(entityType: JavaAnyType): JavaFile {
    val constructorSpec = MethodSpec.constructorBuilder()
        .addModifiers(Modifier.PUBLIC)
        .addParameter(SupportTypes.ApiResponse, "response")
        .addStatement("super(response)")
        .build()

    val methodSpecGetEntity = MethodSpec.methodBuilder("getEntity")
        .addAnnotation("java.lang.Override".toClassName())
        .addModifiers(Modifier.PUBLIC)
        .returns(entityType.toTypeName())
        .addStatement("return (\$T) super.getEntity()", entityType.toTypeName())
        .build()

    val classSpec = TypeSpec.classBuilder(errorWithEntityExceptionClassName(entityType))
        .addJavadoc("Exception for errors where the API returned an entity of type {@code ${entityType.name.simpleName}}.")
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
      "ApiClientErrorWith${entityType.name.simpleName}EntityException"
  )

  private fun createClassForOperationSpecificResult(operation: JavaOperation): TypeSpec {
    val className = typeNameOfResultClass(operation)
    val responseFieldSpec = FieldSpec.builder(SupportTypes.ApiResponse, "response", Modifier.PRIVATE, Modifier.FINAL).build()

    val constructorSpec = MethodSpec.constructorBuilder()
        .addModifiers(Modifier.PUBLIC)
        .addParameter(SupportTypes.ApiResponse, "response")
        .addStatement("this.response = response")
        .build()

    val getResponseMethodSpec = MethodSpec.methodBuilder("getResponse")
        .addJavadoc("Returns the ApiResponse instance with the details of the operation's HTTP response.")
        .addModifiers(Modifier.PUBLIC)
        .returns(SupportTypes.ApiResponse)
        .addStatement("return response")
        .build()

    val getStatusMethodSpec = MethodSpec.methodBuilder("getStatus")
        .addJavadoc("Returns the HTTP status code of the operation's response.")
        .addModifiers(Modifier.PUBLIC)
        .returns(TypeName.INT)
        .addStatement("return response.getStatusCode()")
        .build()

    val isSuccessfulMethodSpec = MethodSpec.methodBuilder("isSuccessful")
        .addJavadoc("Returns whether the response has a status code in the range 200 to 299.")
        .addModifiers(Modifier.PUBLIC)
        .returns(TypeName.BOOLEAN)
        .addStatement("return response.isSuccessful()")
        .build()

    val statusQueryMethodSpecs = createStatusQueryMethods(operation)
    val entityGetterMethodSpecs = createEntityGetterMethods(operation)
    val equalsHashCodeAndToStringMethods = MethodsFromObject.generateEqualsHashCodeAndToString(className, listOf(responseFieldSpec))

    return TypeSpec.classBuilder(className)
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .addJavadoc("Represents the result of calling operation \$L.", operation.javaMethodName)
        .addField(responseFieldSpec)
        .addMethod(constructorSpec)
        .addMethod(getResponseMethodSpec)
        .addMethod(getStatusMethodSpec)
        .addMethod(isSuccessfulMethodSpec)
        .addMethods(statusQueryMethodSpecs)
        .addMethods(entityGetterMethodSpecs)
        .addMethods(equalsHashCodeAndToStringMethods)
        .build()
  }

  private fun createStatusQueryMethods(operation: JavaOperation): List<MethodSpec> {
    return operation.responses.flatMap { response ->
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
      is JavaCollectionType -> "${type.name.simpleName}Of${type.elementType.name.simpleName}"
      is JavaMapType -> "MapOfStringTo${type.valuesType.name.simpleName}"
      is JavaType -> type.name.simpleName
    }
  }

  private fun createEntityGetterMethods(operation: JavaOperation): List<MethodSpec> {
    val returnTypes = operation.allReturnTypes
    return when {
      returnTypes.isEmpty() -> emptyList()
      returnTypes.size == 1 -> listOf(createUnnamedEntityGetterMethod(returnTypes.first()))
      else -> returnTypes.flatMap(::createNamedEntityGetterMethods)
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

  private object SupportTypes {

    val ApiClientErrorWithEntityException = "$SUPPORT_PACKAGE.ApiClientErrorWithEntityException".toClassName()
    val ApiClientIncompatibleResponseException = "$SUPPORT_PACKAGE.ApiClientIncompatibleResponseException".toClassName()
    val ApiClientIoException = "$SUPPORT_PACKAGE.ApiClientIoException".toClassName()
    val ApiClientValidationException = "$SUPPORT_PACKAGE.ApiClientValidationException".toClassName()
    val ApiRequestExecutor = "$SUPPORT_PACKAGE.ApiRequestExecutor".toClassName()
    val ApiResponse = "$SUPPORT_PACKAGE.ApiResponse".toClassName()
    val BodyPartType = "$SUPPORT_PACKAGE.internal.BodyPart.Type".toClassName()
    val OperationBuilder = "$SUPPORT_PACKAGE.internal.Operation.Builder".toClassName()
    val ParameterLocation = "$SUPPORT_PACKAGE.internal.ParameterLocation".toClassName()
    val StatusCode = "$SUPPORT_PACKAGE.internal.StatusCode".toClassName()
  }

  companion object {

    const val SUPPORT_PACKAGE = "io.github.ruedigerk.contractfirst.generator.client"
    const val CLIENT_CLASS_NAME_SUFFIX = "Client"

    /**
     * The Attachment class is used for file/binary body parts of multipart bodies. It contains the content, file name and media type of the body part.
     */
    val AttachmentJavaTypeName = JavaTypeName(SUPPORT_PACKAGE, "Attachment")
  }
}
