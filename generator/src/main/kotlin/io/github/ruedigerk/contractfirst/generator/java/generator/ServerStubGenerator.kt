package io.github.ruedigerk.contractfirst.generator.java.generator

import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeSpec
import io.github.ruedigerk.contractfirst.generator.configuration.GeneratorVariant
import io.github.ruedigerk.contractfirst.generator.java.JavaConfiguration
import io.github.ruedigerk.contractfirst.generator.java.generator.Annotations.NOT_NULL_ANNOTATION
import io.github.ruedigerk.contractfirst.generator.java.generator.JavapoetExtensions.doIf
import io.github.ruedigerk.contractfirst.generator.java.generator.JavapoetExtensions.doIfNotNull
import io.github.ruedigerk.contractfirst.generator.java.generator.TypeNames.toClassName
import io.github.ruedigerk.contractfirst.generator.java.generator.TypeNames.toTypeName
import io.github.ruedigerk.contractfirst.generator.java.model.JavaOperation
import io.github.ruedigerk.contractfirst.generator.java.model.JavaOperationGroup
import io.github.ruedigerk.contractfirst.generator.java.model.JavaParameter
import io.github.ruedigerk.contractfirst.generator.java.model.JavaSpecification
import io.github.ruedigerk.contractfirst.generator.logging.Log
import java.io.File
import javax.lang.model.element.Modifier.ABSTRACT
import javax.lang.model.element.Modifier.PUBLIC

/**
 * Generates the code for the server stubs.
 */
class ServerStubGenerator(
    private val configuration: JavaConfiguration,
    private val log: Log
) : (JavaSpecification) -> Unit {

  private val outputDir = File(configuration.outputDir)
  private val apiPackage = configuration.apiPackage
  private val supportPackage = configuration.supportPackage
  private val variant = selectVariant(configuration)

  private fun selectVariant(configuration: JavaConfiguration) = when (configuration.generatorVariant) {
    GeneratorVariant.SERVER_JAX_RS -> JaxRsServerVariant(supportPackage)
    GeneratorVariant.SERVER_SPRING_WEB -> TODO()
    else -> error("Unsupported generator variant ${configuration.generatorVariant}")
  }

  override operator fun invoke(specification: JavaSpecification) {
    specification.operationGroups.asSequence()
        .map(::toJavaInterface)
        .forEach { it.writeTo(outputDir) }

    writeResponseWrapperClass()
  }

  private fun toJavaInterface(operationGroup: JavaOperationGroup): JavaFile {
    val operationsToTypesafeResponseClass = operationGroup.operations.associateWith(variant::toTypesafeResponseClass)
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
    // TODO: Multipart is supported since JAX-RS 3.1: https://jakarta.ee/specifications/restful-ws/3.1/jakarta-restful-ws-spec-3.1.html#consuming_multipart_formdata
    if (operation.requestBodyMediaType?.startsWith("multipart/") == true) {
      log.warn {
        "Request body media type ${operation.requestBodyMediaType} is not supported in the server generator for operation " +
            "'${operation.httpMethod} ${operation.path}'. No method will be generated."
      }
      return null
    }

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
    val typeValidationAnnotations = parameter.javaType.validations.map(Annotations::toAnnotation)

    return ParameterSpec.builder(parameter.javaType.toTypeName(), parameter.javaParameterName)
        .also { variant.addAnnotationsToMethodParameter(it, parameter) }
        .doIf(parameter.required) { addAnnotation(NOT_NULL_ANNOTATION) }
        .addAnnotations(typeValidationAnnotations)
        .build()
  }

  private fun writeResponseWrapperClass() {
    TemplateFileWriter(configuration).writeTemplateFile(supportPackage, "$RESPONSE_WRAPPER_CLASS_NAME.java")
  }

  companion object {

    const val RESPONSE_WRAPPER_CLASS_NAME = "ResponseWrapper"
  }
}
