package io.github.ruedigerk.contractfirst.generator.java.transform

import io.github.ruedigerk.contractfirst.generator.NotSupportedException
import io.github.ruedigerk.contractfirst.generator.ParserContentException
import io.github.ruedigerk.contractfirst.generator.java.Identifiers.capitalize
import io.github.ruedigerk.contractfirst.generator.java.Identifiers.toJavaIdentifier
import io.github.ruedigerk.contractfirst.generator.java.Identifiers.toJavaTypeIdentifier
import io.github.ruedigerk.contractfirst.generator.java.model.*
import io.github.ruedigerk.contractfirst.generator.model.*

/**
 * Transforms the parsed specification into a Java-specific specification, appropriate for code generation.
 */
class JavaOperationTransformer(
    private val schemas: Map<SchemaId, Schema>,
    private val types: Map<SchemaId, JavaAnyType>,
    private val effectiveOperationIds: Map<List<String>, String>
) {

  fun transform(operations: List<Operation>): List<JavaOperationGroup> = groupOperations(operations)

  private fun groupOperations(operations: List<Operation>): List<JavaOperationGroup> = operations
      .groupBy { it.tags.firstOrNull() ?: DEFAULT_TAG_NAME }
      .mapValues { it.value.map(::toJavaOperation) }
      .map { (tag, operations) -> JavaOperationGroup(tag.toJavaTypeIdentifier() + GROUP_NAME_SUFFIX, operations, tag) }

  // TODO: add location suffix to parameter name, if parameter name is not unique within operation (name + location must be unique according to spec)
  private fun toJavaOperation(operation: Operation): JavaOperation {
    val requestBodyContents = operation.requestBody?.contents
    if (requestBodyContents != null && requestBodyContents.size != 1) {
      throw NotSupportedException("Only operations with a single request body content definition are supported: $operation")
    }

    val requestBodyContent = requestBodyContents?.first()
    val bodyParameters = operation.requestBody?.let { requestBodyToParameters(operation, it, requestBodyContent!!) } ?: emptyList()
    val parameters = toParametersWithUniqueName(operation.parameters.map(::toJavaParameter) + bodyParameters)

    return JavaOperation(
        deriveMethodName(operation),
        toOperationJavadoc(operation, parameters),
        operation.path,
        operation.method,
        requestBodyContent?.mediaType,
        parameters,
        operation.responses.map(::toJavaResponse),
    )
  }

  private fun deriveMethodName(operation: Operation) = effectiveOperationIds[operation.position.path] ?: throw IllegalStateException("No operation found with path ${operation.position.path} in $effectiveOperationIds for $operation")

  private fun toOperationJavadoc(operation: Operation, parameters: List<JavaParameter>): String? {
    val docComment = operation.description ?: operation.summary
    val paramsJavadoc = parameters.filter { it.javadoc != null }.joinToString("\n") { "@param ${it.javaParameterName} ${it.javadoc}" }

    return when {
      docComment == null && paramsJavadoc.isEmpty() -> null
      docComment == null -> paramsJavadoc
      paramsJavadoc.isEmpty() -> docComment
      else -> docComment + "\n\n" + paramsJavadoc
    }
  }

  /**
   * The request body can be transformed to multiple parameters, when the Content-Type is multipart or application/x-www-form-urlencoded.
   */
  private fun requestBodyToParameters(operation: Operation, requestBody: RequestBody, content: Content): List<JavaParameter> {
    return if (content.mediaType == "application/x-www-form-urlencoded" || content.mediaType.startsWith("multipart/")) {
      val actualSchema = schemaFor(content.schemaId)
      formBodyToRequestParameters(operation, actualSchema, content.mediaType)
    } else {
      listOf(toBodyParameter(requestBody))
    }
  }

  private fun formBodyToRequestParameters(operation: Operation, schema: Schema, mediaType: String): List<JavaMultipartBodyParameter> {
    if (schema !is ObjectSchema) {
      throw ParserContentException("For request bodies of media type $mediaType the schema must be an object schema, but was ${schema::class.simpleName} in operation at ${operation.position}")
    }

    return schema.properties.map { toJavaMultipartBodyParameter(it) }
  }

  private fun toJavaMultipartBodyParameter(property: SchemaProperty): JavaMultipartBodyParameter {
    return JavaMultipartBodyParameter(
        property.name.toJavaIdentifier(),
        schemaFor(property.schema).description,
        property.required,
        typeFor(property.schema),
        property.name,
    )
  }

  private fun toBodyParameter(requestBody: RequestBody): JavaBodyParameter {
    if (requestBody.contents.isEmpty()) {
      throw NotSupportedException("Empty request body content is not supported: $requestBody")
    }

    // Currently, all body contents must have the same schema. This is enforced by toJavaOperation.
    val bodyContent = requestBody.contents.first()

    return JavaBodyParameter(
        "requestBody",
        requestBody.description ?: JavadocHelper.toJavadoc(schemaFor(bodyContent.schemaId)),
        requestBody.required,
        typeFor(bodyContent.schemaId),
        bodyContent.mediaType
    )
  }

  private fun toJavaParameter(parameter: Parameter): JavaRegularParameter = JavaRegularParameter(
      parameter.name.toJavaIdentifier(),
      parameter.description ?: JavadocHelper.toJavadoc(schemaFor(parameter.schema)),
      parameter.required,
      typeFor(parameter.schema),
      parameter.location,
      parameter.name,
  )

  /**
   * Makes duplicated parameter names unique by adding the location to the names.
   */
  private fun toParametersWithUniqueName(parameters: List<JavaParameter>): List<JavaParameter> {
    val countByName = parameters.groupBy { it.javaParameterName }.mapValues { it.value.size }

    return parameters.map { parameter ->
      if (countByName[parameter.javaParameterName]!! > 1) {
        when (parameter) {
          is JavaBodyParameter -> parameter.copy(javaParameterName = parameter.javaParameterName + "Entity")
          is JavaMultipartBodyParameter -> parameter.copy(javaParameterName = parameter.javaParameterName + "InBody")
          is JavaRegularParameter -> parameter.copy(javaParameterName = parameter.javaParameterName + "In" + parameter.location.name.lowercase().capitalize())
        }
      } else {
        parameter
      }
    }
  }

  private fun toJavaResponse(response: Response): JavaResponse = JavaResponse(
      response.statusCode,
      response.contents.map(::toJavaResponseContent)
  )

  private fun toJavaResponseContent(content: Content): JavaContent = JavaContent(
      content.mediaType,
      typeFor(content.schemaId)
  )

  private fun typeFor(schemaId: SchemaId) = types[schemaId]!!

  private fun schemaFor(schemaId: SchemaId) = schemas[schemaId]!!

  companion object {

    private const val GROUP_NAME_SUFFIX = "Api"
    private const val DEFAULT_TAG_NAME = "Default"
  }
}

