package io.github.ruedigerk.contractfirst.generator.java.transform

import io.github.ruedigerk.contractfirst.generator.NotSupportedException
import io.github.ruedigerk.contractfirst.generator.ParserContentException
import io.github.ruedigerk.contractfirst.generator.java.Identifiers.capitalize
import io.github.ruedigerk.contractfirst.generator.java.Identifiers.toJavaIdentifier
import io.github.ruedigerk.contractfirst.generator.java.Identifiers.toJavaTypeIdentifier
import io.github.ruedigerk.contractfirst.generator.java.model.JavaAnyType
import io.github.ruedigerk.contractfirst.generator.java.model.JavaBodyParameter
import io.github.ruedigerk.contractfirst.generator.java.model.JavaContent
import io.github.ruedigerk.contractfirst.generator.java.model.JavaDissectedBodyParameter
import io.github.ruedigerk.contractfirst.generator.java.model.JavaDissectedBodyParameter.BodyPartType
import io.github.ruedigerk.contractfirst.generator.java.model.JavaDissectedBodyParameter.BodyPartType.ATTACHMENT
import io.github.ruedigerk.contractfirst.generator.java.model.JavaDissectedBodyParameter.BodyPartType.COMPLEX
import io.github.ruedigerk.contractfirst.generator.java.model.JavaDissectedBodyParameter.BodyPartType.PRIMITIVE
import io.github.ruedigerk.contractfirst.generator.java.model.JavaDissectedBodyParameter.DissectedMediaTypeFamily
import io.github.ruedigerk.contractfirst.generator.java.model.JavaDissectedBodyParameter.DissectedMediaTypeFamily.FORM_URL_ENCODED
import io.github.ruedigerk.contractfirst.generator.java.model.JavaDissectedBodyParameter.DissectedMediaTypeFamily.MULTIPART
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
   * The request body can be dissected into multiple parameters, when the Content-Type is multipart or application/x-www-form-urlencoded.
   */
  private fun requestBodyToParameters(operation: Operation, requestBody: RequestBody): List<JavaParameter> {
    val content = requestBody.requireSingleContent(operation)
    val dissectedMediaType = determineDissectedMediaTypeFamily(content.mediaType)

    return if (dissectedMediaType != null) {
      formBodySchemaIds.add(content.schemaId)
      val schema = schemaFor(content.schemaId)
      dissectedBodyToRequestParameters(operation, schema, content.mediaType, dissectedMediaType)
    } else {
      listOf(toBodyParameter(operation, requestBody))
    }
  }

  /**
   * Determines whether the supplied media type belongs to a family resulting in the body being handled as dissected parts, and returns the family the
   * media type belongs to. Returns null when the supplied media type does not belong to a dissected family of media types.
   */
  private fun determineDissectedMediaTypeFamily(mediaType: String): DissectedMediaTypeFamily? = when {
    mediaType.startsWith("application/x-www-form-urlencoded") -> FORM_URL_ENCODED
    mediaType.startsWith("multipart/") -> MULTIPART
    else -> null
  }

  private fun dissectedBodyToRequestParameters(
    operation: Operation,
    schema: Schema,
    mediaType: String,
    dissectedMediaTypeFamily: DissectedMediaTypeFamily,
  ): List<JavaDissectedBodyParameter> {
    if (schema !is ObjectSchema) {
      throw ParserContentException(
        "For request bodies of media type $mediaType the schema must be an object schema, but was ${schema::class.simpleName} in operation at ${operation.position}",
      )
    }

    return schema.properties.map { toJavaDissectedBodyParameter(it, dissectedMediaTypeFamily) }
  }

  private fun toJavaDissectedBodyParameter(property: SchemaProperty, dissectedMediaTypeFamily: DissectedMediaTypeFamily) = JavaDissectedBodyParameter(
    property.name.toJavaIdentifier(),
    schemaFor(property.schema).description,
    property.required,
    typeFor(property.schema),
    property.name,
    determineDissectedBodyPartType(property),
    dissectedMediaTypeFamily,
  )

  /**
   * Determines the type of the body part parameter, i.e., one of: primitive, complex or attachment.
   *
   * See: https://spec.openapis.org/oas/v3.0.3#support-for-x-www-form-urlencoded-request-bodies
   * See: https://spec.openapis.org/oas/v3.0.3#special-considerations-for-multipart-content
   * See: https://swagger.io/docs/specification/describing-request-body/multipart-requests/
   */
  private fun determineDissectedBodyPartType(property: SchemaProperty): BodyPartType {
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
        is ArraySchema -> COMPLEX
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
    parameter.name,
    parameter.location,
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

          is JavaDissectedBodyParameter -> parameter.copy(javaParameterName = parameter.javaParameterName + "InBody")

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
