package io.github.ruedigerk.contractfirst.generator.java.transform

import io.github.ruedigerk.contractfirst.generator.NotSupportedException
import io.github.ruedigerk.contractfirst.generator.ParserContentException
import io.github.ruedigerk.contractfirst.generator.java.Identifiers.capitalize
import io.github.ruedigerk.contractfirst.generator.java.Identifiers.toJavaIdentifier
import io.github.ruedigerk.contractfirst.generator.java.Identifiers.toJavaTypeIdentifier
import io.github.ruedigerk.contractfirst.generator.java.model.JavaAnyType
import io.github.ruedigerk.contractfirst.generator.java.model.JavaBodyParameter
import io.github.ruedigerk.contractfirst.generator.java.model.JavaContent
import io.github.ruedigerk.contractfirst.generator.java.model.JavaMultipartBodyParameter
import io.github.ruedigerk.contractfirst.generator.java.model.JavaMultipartBodyParameter.BodyPartType
import io.github.ruedigerk.contractfirst.generator.java.model.JavaMultipartBodyParameter.BodyPartType.ATTACHMENT
import io.github.ruedigerk.contractfirst.generator.java.model.JavaMultipartBodyParameter.BodyPartType.COMPLEX
import io.github.ruedigerk.contractfirst.generator.java.model.JavaMultipartBodyParameter.BodyPartType.PRIMITIVE
import io.github.ruedigerk.contractfirst.generator.java.model.JavaOperation
import io.github.ruedigerk.contractfirst.generator.java.model.JavaOperationGroup
import io.github.ruedigerk.contractfirst.generator.java.model.JavaParameter
import io.github.ruedigerk.contractfirst.generator.java.model.JavaRegularParameter
import io.github.ruedigerk.contractfirst.generator.java.model.JavaResponse
import io.github.ruedigerk.contractfirst.generator.java.transform.OperationNaming.getJavaMethodName
import io.github.ruedigerk.contractfirst.generator.openapi.ArraySchema
import io.github.ruedigerk.contractfirst.generator.openapi.Content
import io.github.ruedigerk.contractfirst.generator.openapi.DataType.BINARY
import io.github.ruedigerk.contractfirst.generator.openapi.EnumSchema
import io.github.ruedigerk.contractfirst.generator.openapi.MapSchema
import io.github.ruedigerk.contractfirst.generator.openapi.ObjectSchema
import io.github.ruedigerk.contractfirst.generator.openapi.Operation
import io.github.ruedigerk.contractfirst.generator.openapi.Parameter
import io.github.ruedigerk.contractfirst.generator.openapi.PrimitiveSchema
import io.github.ruedigerk.contractfirst.generator.openapi.RequestBody
import io.github.ruedigerk.contractfirst.generator.openapi.Response
import io.github.ruedigerk.contractfirst.generator.openapi.Schema
import io.github.ruedigerk.contractfirst.generator.openapi.SchemaId
import io.github.ruedigerk.contractfirst.generator.openapi.SchemaProperty

/**
 * Transforms the parsed specification into a Java-specific specification, appropriate for code generation.
 */
class JavaOperationTransformer(
  private val schemas: Map<SchemaId, Schema>,
  private val types: Map<SchemaId, JavaAnyType>,
  private val operationMethodNames: Map<Operation.PathAndMethod, String>,
) {

  /**
   * During the transformation, the IDs of all schemas that are used as form bodies of operations are collected here. For these schemas, no Java source
   * files need to be generated as form body schemas are generated as additional method parameters.
   */
  private val formBodySchemaIds = mutableSetOf<SchemaId>()

  fun transform(operations: List<Operation>): Result {
    val operationGroups = groupOperations(operations)

    // Do not generate model files for form body schemas.
    val schemasToGenerateAsFiles = schemas - formBodySchemaIds

    return Result(operationGroups, schemasToGenerateAsFiles)
  }

  private fun groupOperations(operations: List<Operation>): List<JavaOperationGroup> = operations
    .groupBy { it.tags.firstOrNull() ?: DEFAULT_TAG_NAME }
    .mapValues { it.value.map(::toJavaOperation) }
    .map { (tag, operations) -> JavaOperationGroup(tag.toJavaTypeIdentifier() + GROUP_NAME_SUFFIX, operations, tag) }

  private fun toJavaOperation(operation: Operation): JavaOperation {
    val bodyParameters = operation.requestBody?.let { requestBodyToParameters(operation, it) } ?: emptyList()
    val parameters = toParametersWithUniqueName(operation.parameters.map(::toJavaParameter) + bodyParameters)

    return JavaOperation(
      operationMethodNames.getJavaMethodName(operation.pathAndMethod),
      toOperationJavadoc(operation, parameters),
      operation.path,
      operation.method,
      operation.requestBody?.requireSingleContent(operation)?.mediaType,
      parameters,
      operation.responses.map(::toJavaResponse),
    )
  }

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
  private fun requestBodyToParameters(operation: Operation, requestBody: RequestBody): List<JavaParameter> {
    val content = requestBody.requireSingleContent(operation)

    return if (content.mediaType == "application/x-www-form-urlencoded" || content.mediaType.startsWith("multipart/")) {
      formBodySchemaIds.add(content.schemaId)
      val schema = schemaFor(content.schemaId)
      multipartBodyToRequestParameters(operation, schema, content.mediaType)
    } else {
      listOf(toBodyParameter(operation, requestBody))
    }
  }

  private fun multipartBodyToRequestParameters(operation: Operation, schema: Schema, mediaType: String): List<JavaMultipartBodyParameter> {
    if (schema !is ObjectSchema) {
      throw ParserContentException(
        "For request bodies of media type $mediaType the schema must be an object schema, but was ${schema::class.simpleName} in operation at ${operation.position}",
      )
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
      determineBodyPartType(property),
    )
  }

  /**
   * Determines the type of the body part parameter, i.e., one of: primitive, complex or attachment.
   *
   * See: https://spec.openapis.org/oas/v3.0.3#special-considerations-for-multipart-content
   * See: https://swagger.io/docs/specification/describing-request-body/multipart-requests/
   */
  private fun determineBodyPartType(property: SchemaProperty): BodyPartType {
    return when (val schema = schemaFor(property.schema)) {
      is PrimitiveSchema -> if (schema.dataType == BINARY) ATTACHMENT else PRIMITIVE

      is EnumSchema -> PRIMITIVE

      is ObjectSchema -> COMPLEX

      is MapSchema -> COMPLEX

      is ArraySchema -> when (val itemSchema = schemaFor(schema.itemSchema)) {
        is PrimitiveSchema -> if (itemSchema.dataType == BINARY) ATTACHMENT else PRIMITIVE
        is EnumSchema -> PRIMITIVE
        is ObjectSchema -> COMPLEX
        is MapSchema -> COMPLEX
        is ArraySchema -> COMPLEX // TODO: Arrays of primitives should be transported as text/plain (but with what formatting?)
      }
    }
  }

  private fun toBodyParameter(operation: Operation, requestBody: RequestBody): JavaBodyParameter {
    val content = requestBody.requireSingleContent(operation)

    return JavaBodyParameter(
      "requestBody",
      requestBody.description ?: JavadocHelper.toJavadoc(schemaFor(content.schemaId)),
      requestBody.required,
      typeFor(content.schemaId),
      content.mediaType,
    )
  }

  private fun RequestBody.requireSingleContent(operation: Operation): Content {
    if (contents.size != 1) {
      throw NotSupportedException("Only operations with a single request body content definition are supported: $operation")
    }
    return contents.first()
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

          is JavaRegularParameter -> parameter.copy(
            javaParameterName =
            parameter.javaParameterName + "In" + parameter.location.name.lowercase().capitalize(),
          )
        }
      } else {
        parameter
      }
    }
  }

  private fun toJavaResponse(response: Response): JavaResponse = JavaResponse(
    response.statusCode,
    response.contents.map(::toJavaResponseContent),
  )

  private fun toJavaResponseContent(content: Content): JavaContent = JavaContent(
    content.mediaType,
    typeFor(content.schemaId),
  )

  private fun typeFor(schemaId: SchemaId): JavaAnyType = types[schemaId] ?: error("Unknown schema ID: $schemaId")

  private fun schemaFor(schemaId: SchemaId): Schema = schemas[schemaId] ?: error("Unknown schema ID: $schemaId")

  /**
   * The result of the Java operation transformation.
   */
  data class Result(
    val operationGroups: List<JavaOperationGroup>,
    val schemasToGenerateModelFilesFor: Map<SchemaId, Schema>,
  )

  private companion object {

    private const val GROUP_NAME_SUFFIX = "Api"
    private const val DEFAULT_TAG_NAME = "Default"
  }
}
