package de.rk42.openapi.codegen.java.transform

import de.rk42.openapi.codegen.CliConfiguration
import de.rk42.openapi.codegen.NotSupportedException
import de.rk42.openapi.codegen.java.Identifiers.toJavaIdentifier
import de.rk42.openapi.codegen.java.Identifiers.toJavaTypeIdentifier
import de.rk42.openapi.codegen.java.model.JavaBodyParameter
import de.rk42.openapi.codegen.java.model.JavaContent
import de.rk42.openapi.codegen.java.model.JavaOperation
import de.rk42.openapi.codegen.java.model.JavaOperationGroup
import de.rk42.openapi.codegen.java.model.JavaParameter
import de.rk42.openapi.codegen.java.model.JavaRegularParameterLocation
import de.rk42.openapi.codegen.java.model.JavaResponse
import de.rk42.openapi.codegen.java.model.JavaSpecification
import de.rk42.openapi.codegen.model.CtrContent
import de.rk42.openapi.codegen.model.CtrOperation
import de.rk42.openapi.codegen.model.CtrParameter
import de.rk42.openapi.codegen.model.CtrRequestBody
import de.rk42.openapi.codegen.model.CtrResponse
import de.rk42.openapi.codegen.model.CtrSchemaNonRef
import de.rk42.openapi.codegen.model.CtrSpecification

/**
 * Transforms the parsed specification into a Java-specific specification, appropriate for code generation.
 */
class JavaTransformer(private val configuration: CliConfiguration) {

  private lateinit var typeLookup: JavaTypeLookup

  fun transform(specification: CtrSpecification): JavaSpecification {
    typeLookup = JavaTypeLookup(configuration, specification.schemas)
    val schemaTransformer = JavaSchemaTransformer(typeLookup)

    return JavaSpecification(
        groupOperations(specification.operations),
        schemaTransformer.transformedJavaModelFiles()
    )
  }

  private fun groupOperations(operations: List<CtrOperation>): List<JavaOperationGroup> = operations
      .groupBy { it.tags.firstOrNull() ?: DEFAULT_GROUP_NAME }
      .mapKeys { it.key.toJavaTypeIdentifier() + GROUP_NAME_SUFFIX }
      .mapValues { it.value.map(::toJavaOperation) }
      .map { (groupJavaIdentifier, operations) -> JavaOperationGroup(groupJavaIdentifier, operations) }

  private fun toJavaOperation(operation: CtrOperation): JavaOperation {
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

  private fun toOperationJavadoc(operation: CtrOperation, parameters: List<JavaParameter>): String? {
    val docComment = operation.description ?: operation.summary
    val paramsJavadoc = parameters.filter { it.javadoc != null }.joinToString("\n") { "@param ${it.javaIdentifier} ${it.javadoc}" }

    return when {
      docComment == null && paramsJavadoc.isEmpty() -> null
      docComment == null -> paramsJavadoc
      paramsJavadoc.isEmpty() -> docComment
      else -> docComment + "\n\n" + paramsJavadoc
    }
  }

  private fun toBodyParameter(requestBody: CtrRequestBody): JavaParameter {
    if (requestBody.contents.isEmpty()) {
      throw NotSupportedException("Empty request body content is not supported: $requestBody")
    }

    // Currently, all body contents must have the same schema. This is enforced by toJavaOperation.
    val schema = requestBody.contents.first().schema

    return JavaParameter(
        "requestBody",
        requestBody.description ?: TransformerHelper.toJavadoc(schema as CtrSchemaNonRef),
        JavaBodyParameter,
        requestBody.required,
        typeLookup.lookupJavaTypeFor(schema)
    )
  }

  private fun toJavaParameter(parameter: CtrParameter): JavaParameter = JavaParameter(
      parameter.name.toJavaIdentifier(),
      parameter.description ?: TransformerHelper.toJavadoc(parameter.schema as CtrSchemaNonRef),
      JavaRegularParameterLocation(parameter.name, parameter.location),
      parameter.required,
      typeLookup.lookupJavaTypeFor(parameter.schema)
  )

  private fun toJavaResponse(response: CtrResponse): JavaResponse = JavaResponse(
      response.statusCode,
      response.contents.map(::toJavaResponseContent)
  )

  private fun toJavaResponseContent(content: CtrContent): JavaContent = JavaContent(
      content.mediaType,
      typeLookup.lookupJavaTypeFor(content.schema)
  )

  companion object {

    private const val GROUP_NAME_SUFFIX = "Api"
    private const val DEFAULT_GROUP_NAME = "Default"
  }
}

