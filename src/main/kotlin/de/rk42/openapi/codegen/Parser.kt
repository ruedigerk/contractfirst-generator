package de.rk42.openapi.codegen

import de.rk42.openapi.codegen.model.ParameterLocation
import de.rk42.openapi.codegen.model.contract.CtrOperation
import de.rk42.openapi.codegen.model.contract.CtrParameter
import de.rk42.openapi.codegen.model.contract.CtrPathItem
import de.rk42.openapi.codegen.model.contract.CtrPrimitiveType
import de.rk42.openapi.codegen.model.contract.CtrResponse
import de.rk42.openapi.codegen.model.contract.CtrResponseContent
import de.rk42.openapi.codegen.model.contract.CtrSchema
import de.rk42.openapi.codegen.model.contract.CtrSchemaArray
import de.rk42.openapi.codegen.model.contract.CtrSchemaEnum
import de.rk42.openapi.codegen.model.contract.CtrSchemaObject
import de.rk42.openapi.codegen.model.contract.CtrSchemaPrimitive
import de.rk42.openapi.codegen.model.contract.CtrSchemaRef
import de.rk42.openapi.codegen.model.contract.CtrSpecification
import de.rk42.openapi.codegen.model.contract.DefaultStatusCode
import de.rk42.openapi.codegen.model.contract.ResponseStatusCode
import de.rk42.openapi.codegen.model.contract.StatusCode
import io.swagger.parser.OpenAPIParser
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.media.ArraySchema
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.parameters.Parameter
import io.swagger.v3.oas.models.responses.ApiResponses
import io.swagger.v3.parser.core.models.ParseOptions
import io.swagger.v3.parser.core.models.SwaggerParseResult

/**
 * Parser implementation based on swagger-parser.
 */
object Parser {

  fun parse(specFilePath: String): CtrSpecification {
    val parseResult = runSwaggerParser(specFilePath)

    if (parseResult.messages.isNotEmpty()) {
      throw ParserException(parseResult.messages)
    }

    return toContract(parseResult.openAPI!!)
  }

  private fun runSwaggerParser(specFile: String): SwaggerParseResult {
    val parseOptions = ParseOptions()
    parseOptions.isFlatten = true

    return OpenAPIParser().readLocation(specFile, null, parseOptions)
  }

  private fun toContract(openApi: OpenAPI): CtrSpecification {
    return CtrSpecification(
        toPaths(openApi.paths),
        toSchemas(openApi.components.schemas)
    )
  }

  private fun toPaths(pathItemMap: Map<String, PathItem>): List<CtrPathItem> {
    return pathItemMap.entries.map { (path, pathItem) -> toPath(path, pathItem) }
  }

  private fun toPath(path: String, pathItem: PathItem): CtrPathItem {
    return CtrPathItem(path, pathItem.summary ?: "", pathItem.description ?: "", toOperations(pathItem))
  }

  private fun toOperations(pathItem: PathItem): List<CtrOperation> {
    val commonParameters = (pathItem.parameters ?: emptyList()).map(::toParameter)

    val operationsAsMap = mapOf(
        "get" to pathItem.get,
        "put" to pathItem.put,
        "post" to pathItem.post,
        "delete" to pathItem.delete,
        "options" to pathItem.options,
        "head" to pathItem.head,
        "patch" to pathItem.patch,
        "trace" to pathItem.trace
    )

    return operationsAsMap
        .filterValues { it != null }
        .map { (method, operation) -> toOperation(method, operation, commonParameters) }
  }

  private fun toOperation(method: String, operation: Operation, commonParameters: List<CtrParameter>): CtrOperation = CtrOperation(
      method,
      operation.tags.nullToEmpty(),
      operation.summary.nullToEmpty(),
      operation.description.nullToEmpty(),
      operation.operationId,
      joinParameters(operation.parameters.nullToEmpty(), commonParameters),
      toResponses(operation.responses)
  )

  private fun joinParameters(operationParameters: List<Parameter>, pathParameters: List<CtrParameter>): List<CtrParameter> {
    val operationParametersAsMap = operationParameters.map(::toParameter).associateBy { it.name }
    val pathParametersAsMap = pathParameters.associateBy { it.name }

    return pathParametersAsMap.plus(operationParametersAsMap).values.toList()
  }

  private fun toParameter(parameter: Parameter): CtrParameter =
      CtrParameter(parameter.name, toParameterLocation(parameter.`in`), parameter.description, parameter.required ?: false)

  private fun toParameterLocation(location: String?): ParameterLocation {
    return when (location) {
      "query" -> ParameterLocation.QUERY
      "header" -> ParameterLocation.HEADER
      "path" -> ParameterLocation.PATH
      "cookie" -> ParameterLocation.COOKIE
      else -> throw InvalidContractException("parameter.in must be one of query, header, path, cookie, but was $location")
    }
  }

  private fun toResponses(responses: ApiResponses): List<CtrResponse> = responses.map { (statusCode, response) ->
    CtrResponse(toStatusCode(statusCode), toResponseContents(response.content ?: Content()))
  }

  private fun toStatusCode(statusCode: String): ResponseStatusCode = when (statusCode) {
    "default" -> DefaultStatusCode
    else -> StatusCode(statusCode.toInt())
  }

  private fun toResponseContents(content: Content): List<CtrResponseContent> = content.map { (mediaType, content) ->
    // As swagger-parser is run with option "flatten", we know that all schemas are referenced at this point and not inlined
    CtrResponseContent(mediaType, CtrSchemaRef(content.schema.`$ref`))
  }

  private fun toSchemas(schemas: Map<String, Schema<Any>>): Map<CtrSchemaRef, CtrSchema> = schemas
      .mapKeys { CtrSchemaRef("#/components/schemas/${it.key}") }
      .mapValues { toSchema(it.value) }

  private fun toSchema(schema: Schema<Any>): CtrSchema {
    if (schema.`$ref` != null) {
      return CtrSchemaRef(schema.`$ref`)
    }
    if (schema.enum != null && schema.enum.isNotEmpty()) {
      return toEnumSchema(schema)
    }

    return when (schema.type) {
      "array" -> toArraySchema(schema as ArraySchema)
      "boolean", "integer", "number", "string" -> toPrimitiveSchema(schema)
      "null" -> throw ParserException("Schema type 'null' is not supported")
      else -> toObjectSchema(schema)
    }
  }

  private fun toEnumSchema(schema: Schema<Any>): CtrSchemaEnum {
    if (schema.type != "string") {
      throw ParserException("Currently only enum schemas of type 'string' are supported, type is '${schema.type}'")
    }

    return CtrSchemaEnum(schema.title.nullToEmpty(), schema.enum.map { it.toString() })
  }

  @Suppress("UNCHECKED_CAST")
  private fun toArraySchema(schema: ArraySchema): CtrSchemaArray {
    return CtrSchemaArray(schema.title.nullToEmpty(), toSchema(schema.items as Schema<Any>))
  }

  private fun toPrimitiveSchema(schema: Schema<Any>): CtrSchemaPrimitive {
    val type = when (schema.type) {
      "boolean" -> CtrPrimitiveType.BOOLEAN
      "integer" -> CtrPrimitiveType.INTEGER
      "number" -> CtrPrimitiveType.NUMBER
      "string" -> CtrPrimitiveType.STRING
      else -> throw IllegalArgumentException("Program error, unexpected type ${schema.type} in toPrimitiveSchema")
    }

    return CtrSchemaPrimitive(type, schema.title.nullToEmpty())
  }

  private fun toObjectSchema(schema: Schema<Any>): CtrSchemaObject {
    return CtrSchemaObject(
        schema.title.nullToEmpty(),
        schema.properties.mapValues { toSchema(it.value) },
        schema.required.toSet()
    )
  }

  private fun <T> List<T>?.nullToEmpty(): List<T> = this ?: emptyList()

  private fun String?.nullToEmpty(): String = this ?: ""

  class ParserException(val messages: List<String>) : RuntimeException() {

    constructor(msg: String) : this(listOf(msg))
  }
}
