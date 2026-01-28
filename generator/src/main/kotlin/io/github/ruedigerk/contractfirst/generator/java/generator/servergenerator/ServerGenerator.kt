package io.github.ruedigerk.contractfirst.generator.java.generator.servergenerator

import com.squareup.javapoet.*
import io.github.ruedigerk.contractfirst.generator.configuration.GeneratorVariant
import io.github.ruedigerk.contractfirst.generator.java.Identifiers.capitalize
import io.github.ruedigerk.contractfirst.generator.java.Identifiers.mediaTypeToJavaIdentifier
import io.github.ruedigerk.contractfirst.generator.java.JavaConfiguration
import io.github.ruedigerk.contractfirst.generator.java.generator.Annotations
import io.github.ruedigerk.contractfirst.generator.java.generator.Annotations.NOT_NULL_ANNOTATION
import io.github.ruedigerk.contractfirst.generator.java.generator.JavaParameters
import io.github.ruedigerk.contractfirst.generator.java.generator.JavapoetExtensions.doIf
import io.github.ruedigerk.contractfirst.generator.java.generator.JavapoetExtensions.doIfNotNull
import io.github.ruedigerk.contractfirst.generator.java.generator.TypeNames.toClassName
import io.github.ruedigerk.contractfirst.generator.java.generator.TypeNames.toTypeName
import io.github.ruedigerk.contractfirst.generator.java.model.*
import io.github.ruedigerk.contractfirst.generator.openapi.DefaultStatusCode
import io.github.ruedigerk.contractfirst.generator.openapi.StatusCode
import java.io.File
import javax.lang.model.element.Modifier.*

/**
 * Generates the code for server stubs/interfaces.
 */
class ServerGenerator(
    private val configuration: JavaConfiguration
) : (JavaSpecification) -> Unit {

  private val outputDir = File(configuration.outputDir)
  private val apiPackage = configuration.apiPackage
  private val supportPackage = configuration.supportPackage
  private val variant = selectVariant(configuration.generatorVariant)

  private fun selectVariant(generatorVariant: GeneratorVariant) = when (generatorVariant) {
    GeneratorVariant.SERVER_JAX_RS -> JaxRsServerGeneratorVariant()
    GeneratorVariant.SERVER_SPRING_WEB -> SpringWebServerGeneratorVariant()
    else -> error("Unsupported server generator variant $generatorVariant")
  }

  override operator fun invoke(specification: JavaSpecification) {
    specification.operationGroups.asSequence()
        .map(::toJavaInterface)
        .forEach { it.writeTo(outputDir) }

    writeResponseWrapperClass()
  }

  private fun toJavaInterface(operationGroup: JavaOperationGroup): JavaFile {
    val operationsToTypesafeResponseClass = operationGroup.operations.associateWith(::toTypesafeResponseClass)
    val methodSpecs = operationsToTypesafeResponseClass.mapNotNull { (operation, typesafeClass) -> toOperationMethod(operation, typesafeClass) }

    val interfaceSpec = TypeSpec.interfaceBuilder(operationGroup.javaIdentifier)
        .also { variant.addAnnotationsToJavaInterface(it) }
        .addModifiers(PUBLIC)
        .addMethods(methodSpecs)
        .addTypes(operationsToTypesafeResponseClass.values)
        .build()

    return JavaFile.builder(apiPackage, interfaceSpec)
        .skipJavaLangImports(true)
        .build()
  }

  private fun toOperationMethod(operation: JavaOperation, typesafeResponseClass: TypeSpec): MethodSpec? {
    val parameters = operation.parameters.map(::toParameterSpec)

    return MethodSpec.methodBuilder(operation.javaMethodName)
        .also { variant.addAnnotationsToOperationMethod(it, operation) }
        .doIfNotNull(operation.javadoc) { addJavadoc("\$L", it) }
        .addModifiers(PUBLIC, ABSTRACT)
        .returns(typesafeResponseClass.name.toClassName())
        .addParameters(parameters)
        .build()
  }

  private fun toParameterSpec(parameter: JavaParameter): ParameterSpec {
    val parameterType = JavaParameters.determineParameterType(parameter, variant.attachmentTypeName)
    val typeValidationAnnotations = parameter.javaType.validations.map(Annotations::toAnnotation)

    return ParameterSpec.builder(parameterType.toTypeName(), parameter.javaParameterName)
        .also { variant.addAnnotationsToMethodParameter(it, parameter) }
        .doIf(parameter.required) { addAnnotation(NOT_NULL_ANNOTATION) }
        .addAnnotations(typeValidationAnnotations)
        .build()
  }

  private fun toTypesafeResponseClass(operation: JavaOperation): TypeSpec {
    val frameworkResponseClass = variant.responseClassName.toClassName()
    val typesafeResponseClass = (operation.javaMethodName.capitalize() + "Response").toClassName()

    val responseMethodsWithStatusCode = operation.responses
        .filter { it.statusCode is StatusCode }
        .flatMap { response ->
          if (response.contents.isEmpty()) {
            listOf(toTypesafeEmptyResponseMethod(response, frameworkResponseClass, typesafeResponseClass))
          } else {
            response.contents.map { content -> toTypesafeResponseMethod(response, content, frameworkResponseClass, typesafeResponseClass) }
          }
        }

    val defaultResponseMethods = operation.responses
        .filter { it.statusCode is DefaultStatusCode }
        .flatMap { response -> response.contents.map { content -> toTypesafeArbitraryResponseMethod(content, frameworkResponseClass, typesafeResponseClass) } }

    val customResponseMethod = MethodSpec.methodBuilder("withCustomResponse")
        .addModifiers(PUBLIC, STATIC)
        .returns(typesafeResponseClass)
        .addParameter(frameworkResponseClass, "response")
        .addStatement("return new \$T(response)", typesafeResponseClass)
        .build()

    val constructor = MethodSpec.constructorBuilder()
        .addModifiers(PRIVATE)
        .addParameter(frameworkResponseClass, "delegate")
        .addStatement("super(delegate)")
        .build()

    return TypeSpec.classBuilder(typesafeResponseClass)
        .addModifiers(PUBLIC, STATIC)
        .superclass(ClassName.get(supportPackage, RESPONSE_WRAPPER_CLASS_NAME))
        .addMethod(constructor)
        .addMethods(responseMethodsWithStatusCode)
        .addMethods(defaultResponseMethods)
        .addMethod(customResponseMethod)
        .build()
  }

  private fun toTypesafeResponseMethod(
      response: JavaResponse,
      content: JavaContent,
      frameworkResponseClass: ClassName,
      typesafeResponseClass: ClassName
  ): MethodSpec {
    val statusCode = (response.statusCode as StatusCode).code
    val mediaTypeAsIdentifier = content.mediaType.mediaTypeToJavaIdentifier()
    val methodName = "with$statusCode$mediaTypeAsIdentifier"
    val bodyParameterType = variant.rewriteResponseBodyType(content.javaType)

    return MethodSpec.methodBuilder(methodName)
        .addModifiers(PUBLIC, STATIC)
        .returns(typesafeResponseClass)
        .addParameter(bodyParameterType.toTypeName(), "entity")
        .addStatement(
            "return new \$T(\$T.status(\$L).header(\"Content-Type\", \$S)${variant.buildResponseWithEntity()})",
            typesafeResponseClass,
            frameworkResponseClass,
            statusCode,
            content.mediaType
        )
        .build()
  }

  private fun toTypesafeEmptyResponseMethod(response: JavaResponse, frameworkResponseClass: ClassName, typesafeResponseClassame: ClassName): MethodSpec {
    val statusCode = (response.statusCode as StatusCode).code
    val methodName = "with$statusCode"

    return MethodSpec.methodBuilder(methodName)
        .addModifiers(PUBLIC, STATIC)
        .returns(typesafeResponseClassame)
        .addStatement("return new \$T(\$T.status(\$L).build())", typesafeResponseClassame, frameworkResponseClass, statusCode)
        .build()
  }

  /**
   * This method allows the caller to arbitrarily choose the response status. This is for supporting the "default" status in OpenAPI contracts.
   */
  private fun toTypesafeArbitraryResponseMethod(content: JavaContent, frameworkResponseClass: ClassName, typesafeResponseClass: ClassName): MethodSpec {
    val mediaTypeAsIdentifier = content.mediaType.mediaTypeToJavaIdentifier()
    val methodName = "with$mediaTypeAsIdentifier"

    return MethodSpec.methodBuilder(methodName)
        .addModifiers(PUBLIC, STATIC)
        .returns(typesafeResponseClass)
        .addParameter(Integer.TYPE, "status")
        .addParameter(content.javaType.toTypeName(), "entity")
        .addStatement(
            "return new \$T(\$T.status(status).header(\"Content-Type\", \$S)${variant.buildResponseWithEntity()})",
            typesafeResponseClass,
            frameworkResponseClass,
            content.mediaType
        )
        .build()
  }

  private fun writeResponseWrapperClass() {
    TemplateFileWriter(configuration).writeTemplateFile(supportPackage, variant.templateDirectory, "$RESPONSE_WRAPPER_CLASS_NAME.java")
  }

  companion object {

    const val RESPONSE_WRAPPER_CLASS_NAME = "ResponseWrapper"
  }
}
