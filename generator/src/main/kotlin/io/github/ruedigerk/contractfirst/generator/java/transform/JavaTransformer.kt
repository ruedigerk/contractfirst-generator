package io.github.ruedigerk.contractfirst.generator.java.transform

import io.github.ruedigerk.contractfirst.generator.Configuration
import io.github.ruedigerk.contractfirst.generator.NotSupportedException
import io.github.ruedigerk.contractfirst.generator.java.Identifiers.toJavaIdentifier
import io.github.ruedigerk.contractfirst.generator.java.Identifiers.toJavaTypeIdentifier
import io.github.ruedigerk.contractfirst.generator.java.model.*
import io.github.ruedigerk.contractfirst.generator.logging.Log
import io.github.ruedigerk.contractfirst.generator.model.*

/**
 * Transforms the parsed specification into a Java-specific specification, appropriate for code generation.
 */
class JavaTransformer(private val log: Log, private val configuration: Configuration) {

  private lateinit var typeLookup: JavaTypeLookup

  fun transform(specification: MSpecification): JavaSpecification {
    typeLookup = JavaTypeLookup(log, configuration, specification.schemas)
    val schemaTransformer = SchemaToJavaSourceFileTransformer(typeLookup)

    return JavaSpecification(
        groupOperations(specification.operations),
        schemaTransformer.transformedJavaModelFiles()
    )
  }

  private fun groupOperations(operations: List<MOperation>): List<JavaOperationGroup> = operations
      .groupBy { it.tags.firstOrNull() ?: DEFAULT_GROUP_NAME }
      .mapKeys { it.key.toJavaTypeIdentifier() + GROUP_NAME_SUFFIX }
      .mapValues { it.value.map(::toJavaOperation) }
      .map { (groupJavaIdentifier, operations) -> JavaOperationGroup(groupJavaIdentifier, operations) }

  // TODO add location suffix to parameter name, if parameter name is not unique within operation (name + location must be unique according to spec)
  private fun toJavaOperation(operation: MOperation): JavaOperation {
    val requestBodySchemas = operation.requestBody?.contents?.map { it.schema }?.toSet() ?: emptySet()
    if (requestBodySchemas.size > 1) {
      throw NotSupportedException("Different request body schemas for a single operation are not supported: $operation")
    }

    val requestBodyMediaTypes = operation.requestBody?.contents?.map { it.mediaType }?.toSet() ?: emptySet()
    val bodyParameterAsList = operation.requestBody?.let { listOf(toBodyParameter(it)) } ?: emptyList()
    val parameters = operation.parameters.map(::toJavaParameter) + bodyParameterAsList

    return JavaOperation(
        operation.operationId.toJavaIdentifier(),
        toOperationJavadoc(operation, parameters),
        operation.path,
        operation.method,
        requestBodyMediaTypes.toList(),
        parameters,
        operation.responses.map(::toJavaResponse)
    )
  }

  private fun toOperationJavadoc(operation: MOperation, parameters: List<JavaParameter>): String? {
    val docComment = operation.description ?: operation.summary
    val paramsJavadoc = parameters.filter { it.javadoc != null }.joinToString("\n") { "@param ${it.javaIdentifier} ${it.javadoc}" }

    return when {
      docComment == null && paramsJavadoc.isEmpty() -> null
      docComment == null -> paramsJavadoc
      paramsJavadoc.isEmpty() -> docComment
      else -> docComment + "\n\n" + paramsJavadoc
    }
  }

  private fun toBodyParameter(requestBody: MRequestBody): JavaBodyParameter {
    if (requestBody.contents.isEmpty()) {
      throw NotSupportedException("Empty request body content is not supported: $requestBody")
    }

    // Currently, all body contents must have the same schema. This is enforced by toJavaOperation.
    val bodyContent = requestBody.contents.first()

    return JavaBodyParameter(
        "requestBody",
        requestBody.description ?: JavadocHelper.toJavadoc(bodyContent.schema as MSchemaNonRef),
        requestBody.required,
        typeLookup.lookupJavaTypeFor(bodyContent.schema),
        bodyContent.mediaType
    )
  }

  private fun toJavaParameter(parameter: MParameter): JavaRegularParameter = JavaRegularParameter(
      parameter.name.toJavaIdentifier(),
      parameter.description ?: JavadocHelper.toJavadoc(parameter.schema as MSchemaNonRef),
      parameter.required,
      typeLookup.lookupJavaTypeFor(parameter.schema),
      parameter.location,
      parameter.name,
  )

  private fun toJavaResponse(response: MResponse): JavaResponse = JavaResponse(
      response.statusCode,
      response.contents.map(::toJavaResponseContent)
  )

  private fun toJavaResponseContent(content: MContent): JavaContent = JavaContent(
      content.mediaType,
      typeLookup.lookupJavaTypeFor(content.schema)
  )

  companion object {

    private const val GROUP_NAME_SUFFIX = "Api"
    private const val DEFAULT_GROUP_NAME = "Default"
  }
}
