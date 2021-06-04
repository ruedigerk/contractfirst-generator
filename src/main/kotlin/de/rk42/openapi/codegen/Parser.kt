package de.rk42.openapi.codegen

import de.rk42.openapi.codegen.model.ParameterLocation
import de.rk42.openapi.codegen.model.contract.CtrOperation
import de.rk42.openapi.codegen.model.contract.CtrParameter
import de.rk42.openapi.codegen.model.contract.CtrPrimitiveType
import de.rk42.openapi.codegen.model.contract.CtrResponse
import de.rk42.openapi.codegen.model.contract.CtrResponseContent
import de.rk42.openapi.codegen.model.contract.CtrSchema
import de.rk42.openapi.codegen.model.contract.CtrSchemaArray
import de.rk42.openapi.codegen.model.contract.CtrSchemaEnum
import de.rk42.openapi.codegen.model.contract.CtrSchemaNonRef
import de.rk42.openapi.codegen.model.contract.CtrSchemaObject
import de.rk42.openapi.codegen.model.contract.CtrSchemaPrimitive
import de.rk42.openapi.codegen.model.contract.CtrSchemaProperty
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
class Parser {

  private lateinit var schemaParser: SchemaParser

  fun parse(specFilePath: String): CtrSpecification {
    val swaggerParseResult = runSwaggerParser(specFilePath)

    if (swaggerParseResult.messages.isNotEmpty()) {
      throw ParserException(swaggerParseResult.messages)
    }

    return toContract(swaggerParseResult.openAPI!!)
  }

  private fun runSwaggerParser(specFile: String): SwaggerParseResult {
    val parseOptions = ParseOptions().apply {
      isResolve = true
      isFlatten = true
      isFlattenComposedSchemas = true
    }

    return OpenAPIParser().readLocation(specFile, null, parseOptions)
  }

  private fun toContract(openApi: OpenAPI): CtrSpecification {
    schemaParser = SchemaParser(openApi.components.schemas)

    val operations = toOperations(openApi.paths)
    val referencedSchemas = schemaParser.determineAndResolveReferencedSchemas()

    return CtrSpecification(operations, referencedSchemas)
  }

  private fun toOperations(pathItemMap: Map<String, PathItem>): List<CtrOperation> {
    return pathItemMap.entries.flatMap { (path, pathItem) -> toOperations(path, pathItem) }
  }

  private fun toOperations(path: String, pathItem: PathItem): List<CtrOperation> {
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
        .map { (method, operation) -> toOperation(path, method, operation, commonParameters) }
  }

  private fun toOperation(path: String, method: String, operation: Operation, commonParameters: List<CtrParameter>): CtrOperation = CtrOperation(
      path,
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

  private fun toParameter(parameter: Parameter): CtrParameter = CtrParameter(
      parameter.name,
      toParameterLocation(parameter.`in`),
      parameter.description,
      parameter.required ?: false,
      schemaParser.resolveSchema(parameter.schema)
  )

  private fun toParameterLocation(location: String?): ParameterLocation {
    return when (location) {
      "query" -> ParameterLocation.QUERY
      "header" -> ParameterLocation.HEADER
      "path" -> ParameterLocation.PATH
      "cookie" -> ParameterLocation.COOKIE
      else -> throw InvalidContractException("parameter.in must be one of query, header, path or cookie, but was $location")
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
    CtrResponseContent(mediaType, schemaParser.resolveSchema(content.schema))
  }
}

private class SchemaParser(topLevelSchemas: Map<String, Schema<Any>>) {

  // All schemas actually being used/referenced in the contract.
  private val referencedSchemas: MutableSet<CtrSchemaNonRef> = mutableSetOf()

  // Top level schemas are the schemas of the components section of the contract. Only they can be referenced by a $ref.
  private val topLevelSchemas: Map<CtrSchemaRef, CtrSchemaNonRef> = toTopLevelSchemas(topLevelSchemas)

  private fun toTopLevelSchemas(schemas: Map<String, Schema<Any>>): Map<CtrSchemaRef, CtrSchemaNonRef> = schemas
      .mapKeys { CtrSchemaRef("#/components/schemas/${it.key}") }
      .mapValues { toTopLevelSchema(it.value) }

  private fun toTopLevelSchema(schema: Schema<Any>): CtrSchemaNonRef =
      (parseSchema(schema) as? CtrSchemaNonRef) ?: throw ParserException("Unsupported schema reference in #/components/schemas: $schema")

  private fun parseSchema(schema: Schema<Any>): CtrSchema {
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
    return CtrSchemaArray(schema.title.nullToEmpty(), parseSchema(schema.items as Schema<Any>))
  }

  private fun toPrimitiveSchema(schema: Schema<Any>): CtrSchemaPrimitive {
    val type = when (schema.type) {
      "boolean" -> CtrPrimitiveType.BOOLEAN
      "integer" -> CtrPrimitiveType.INTEGER
      "number" -> CtrPrimitiveType.NUMBER
      "string" -> CtrPrimitiveType.STRING
      else -> throw IllegalArgumentException("Program error, unexpected type ${schema.type} in toPrimitiveSchema")
    }

    return CtrSchemaPrimitive(type, schema.format, schema.title.nullToEmpty())
  }

  private fun toObjectSchema(schema: Schema<Any>): CtrSchemaObject {
    val requiredProperties = schema.required.toSet()

    return CtrSchemaObject(
        schema.title.nullToEmpty(),
        schema.properties.map { (name, schema) -> CtrSchemaProperty(name, requiredProperties.contains(name), parseSchema(schema)) }
    )
  }

  fun resolveSchema(schema: Schema<Any>): CtrSchema = when (val parsed = parseSchema(schema)) {
    is CtrSchemaRef -> lookupSchemaRef(parsed)
    is CtrSchemaNonRef -> {
      referencedSchemas.add(parsed)
      parsed
    }
  }

  private fun lookupSchemaRef(schema: CtrSchemaRef): CtrSchemaNonRef {
    val referencedSchema = topLevelSchemas[schema] ?: throw ParserException("Unresolvable schema reference $schema")
    
    referencedSchema.reference = schema
    referencedSchemas.add(referencedSchema)
    
    return referencedSchema
  }

  /**
   * Return all schemas actually being used in the contract.
   */
  fun determineAndResolveReferencedSchemas(): List<CtrSchemaNonRef> {
    if (referencedSchemas.isEmpty()) {
      throw IllegalStateException("resolveReferencedSchemas must be called after parsing the contract's operations")
    }

    // Iteratively resolve all schemas
    val resolvedSchemas = mutableSetOf<CtrSchemaNonRef>()
    var schemasForResolving = referencedSchemas.toSet()

    do {
      schemasForResolving.forEach(::resolveSchemaComponents)
      resolvedSchemas.addAll(schemasForResolving)
      schemasForResolving = referencedSchemas - resolvedSchemas
    } while (schemasForResolving.isNotEmpty())

    return referencedSchemas.toList()
  }

  private fun resolveSchemaComponents(schema: CtrSchemaNonRef) {
    when (schema) {
      is CtrSchemaObject -> resolveObjectProperties(schema)
      is CtrSchemaArray -> resolveArrayItems(schema)
      else -> {
        // do nothing 
      }
    }
  }

  private fun resolveObjectProperties(schema: CtrSchemaObject) {
    schema.properties.forEach { property ->
      when (val propertySchema = property.schema) {
        is CtrSchemaRef -> property.schema = lookupSchemaRef(propertySchema)
        is CtrSchemaNonRef -> referencedSchemas.add(propertySchema)
      }
    }
  }

  private fun resolveArrayItems(schema: CtrSchemaArray) {
    when (val itemSchema = schema.itemSchema) {
      is CtrSchemaRef -> schema.itemSchema = lookupSchemaRef(itemSchema)
      is CtrSchemaNonRef -> referencedSchemas.add(itemSchema)
    }
  }
}

private fun <T> List<T>?.nullToEmpty(): List<T> = this ?: emptyList()

private fun String?.nullToEmpty(): String = this ?: ""
