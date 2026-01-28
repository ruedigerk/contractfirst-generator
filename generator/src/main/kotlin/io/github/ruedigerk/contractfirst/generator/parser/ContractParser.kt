package io.github.ruedigerk.contractfirst.generator.parser

import io.github.ruedigerk.contractfirst.generator.NotSupportedException
import io.github.ruedigerk.contractfirst.generator.ParserContentException
import io.github.ruedigerk.contractfirst.generator.logging.Log
import io.github.ruedigerk.contractfirst.generator.openapi.*
import io.github.ruedigerk.contractfirst.generator.parser.Strings.normalize

/**
 * Parser implementation based on swagger-parser.
 *
 * Parses an OpenAPI 3.0 specification.
 *
 * For now, no all-in-one contract will get generated. The goal is only to parse the contract for code generation.
 *
 * 1. Parse the operations in the contract.
 * 1.1. All referenced components (path items, etc.) are dereferenced and parsed immediately.
 * 1.2. All encountered schemas are remembered but are not parsed yet. References to schemas are resolved and the references schemas remembered.
 *      Schemas are identified by their positions.
 * 2. All previously remembered schemas are parsed.
 */
class ContractParser(private val log: Log) {

  private val parseableCache = ParseableCache()
  private val encounteredSchemas: MutableMap<Position, Parseable> = mutableMapOf()

  fun toSpecification(path: String): Specification {
    return toSpecification(parseableCache.get(path))
  }

  private fun toSpecification(contract: Parseable): Specification {
    val operations = toOperations(contract.requiredField("paths").requireObject())
    val resolvingSchemaParser = ResolvingSchemaParser(log, parseableCache)
    val schemas = resolvingSchemaParser.parseAndResolveAll(encounteredSchemas.values)

    return Specification(operations, schemas, contract)
  }

  private fun toOperations(parseable: Parseable): List<Operation> {
    return parseable.properties().flatMap { (path, pathItem) -> toOperations(path, pathItem) }
  }

  private fun toOperations(path: String, pathItemOrReference: Parseable): List<Operation> {
    // path item references can point to anywhere according to the spec, there is no components key designated for these. 
    // This changes with OpenApi 3.1, where there is a "pathItems" key designated in components.
    val pathItem = parseableCache.resolveWhileReference(pathItemOrReference)

    val commonParameters = pathItem.optionalField("parameters")
        .requireArray()
        .elements()
        .map { toParameter(it) }

    return pathItem.properties().mapNotNull { (methodName, operation) ->
      HttpMethod.of(methodName)?.let { method -> toOperation(path, method, operation.requireObject(), commonParameters) }
    }
  }

  private fun toOperation(
      path: String,
      method: HttpMethod,
      operation: Parseable,
      commonParameters: List<Parameter>
  ): Operation {
    val parameters = operation.optionalField("parameters").elements().map(::toParameter)
    val requestBodyField = operation.optionalField("requestBody")

    return Operation(
        path,
        method,
        operation.optionalField("tags").stringElements(),
        operation.optionalField("summary").string().normalize(),
        operation.optionalField("description").string().normalize(),
        operation.optionalField("operationId").string().normalize(),
        if (requestBodyField.isPresent()) toRequestBody(requestBodyField) else null,
        joinParameters(commonParameters, parameters),
        toResponses(operation.requiredField("responses")),
        operation.position
    )
  }

  private fun toRequestBody(requestBodyOrReference: Parseable): RequestBody {
    val requestBody = parseableCache.resolveWhileReference(requestBodyOrReference)
    val contents = toContents(requestBody.requiredField("content"))

    if (contents.isEmpty()) {
      throw ParserContentException("Content of requestBodyObject must not be empty at ${requestBody.position}")
    }

    return RequestBody(
        requestBody.optionalField("description").string().normalize(),
        requestBody.optionalField("required").boolean() ?: false,
        contents
    )
  }

  private fun joinParameters(commonParameters: List<Parameter>, operationParameters: List<Parameter>): List<Parameter> {
    data class NameAndLocation(val name: String, val location: ParameterLocation)

    fun Parameter.nameAndLocation() = NameAndLocation(this.name, this.location)

    val commonParametersAsMap = commonParameters.associateBy { it.nameAndLocation() }
    val operationParametersAsMap = operationParameters.associateBy { it.nameAndLocation() }

    // Operation parameters are overriding common parameters.
    return (commonParametersAsMap + operationParametersAsMap).values.toList()
  }

  private fun toParameter(parameterUnnamedOrReference: Parseable): Parameter {
    val parameterUnnamed = parseableCache.resolveWhileReference(parameterUnnamedOrReference)

    val name = parameterUnnamed.requiredField("name").string()!!
    val parameter = parameterUnnamed.withPositionHint(name)

    if (!parameter.hasField("schema")) {
      throw NotSupportedException("Parameters without schema are not supported at ${parameter.position}")
    }
    if (parameter.hasField("content")) {
      throw NotSupportedException("Parameters with content property are not supported at ${parameter.position}")
    }

    val location = toParameterLocation(parameter.requiredField("in"))

    val requiredField = parameter.optionalField("required")

    // Path parameters are always required, ignore definition in contract
    val required: Boolean = (location == ParameterLocation.PATH) || requiredField.boolean() ?: false

    return Parameter(
        name,
        location,
        parameter.optionalField("description").string().normalize(),
        required,
        dereferenceAndRememberSchema(parameter.requiredField("schema"))
    )
  }

  private fun dereferenceAndRememberSchema(schemaOrReference: Parseable): SchemaId {
    val schema = parseableCache.resolveWhileReference(schemaOrReference)
    encounteredSchemas[schema.position] = schema
    return SchemaId(schema)
  }

  private fun toParameterLocation(parseable: Parseable): ParameterLocation {
    return when (val location = parseable.string()) {
      "query" -> ParameterLocation.QUERY
      "header" -> ParameterLocation.HEADER
      "path" -> ParameterLocation.PATH
      "cookie" -> ParameterLocation.COOKIE
      else -> throw ParserContentException("parameter.in must be one of 'query', 'header', 'path' or 'cookie', but was '$location' at ${parseable.position}")
    }
  }

  private fun toResponses(responses: Parseable): List<Response> = responses.properties().map { (statusCode, response) ->
    val status = toStatusCode(statusCode, response.position)
    Response(status, toResponseContents(status, response.requireObject()))
  }

  private fun toStatusCode(statusCode: String, position: Position): ResponseStatusCode = when (statusCode) {
    "default" -> DefaultStatusCode
    else -> try {
      StatusCode(statusCode.toInt())
    } catch (_: NumberFormatException) {
      throw ParserContentException("Status code must be a number, but was '$statusCode' at $position")
    }
  }

  private fun toResponseContents(status: ResponseStatusCode, responseOrReference: Parseable): List<Content> {
    // If status code is 204, ignore content if present, as HTTP does not allow content in a 204 response
    return if (status == StatusCode(204)) {
      emptyList()
    } else {
      val response = parseableCache.resolveWhileReference(responseOrReference)
      return if (!response.hasField("content")) emptyList() else toContents(response.requiredField("content"))
    }
  }

  private fun toContents(content: Parseable): List<Content> = content.properties().mapNotNull { (mediaType, content) ->
    content.takeIf { it.hasField("schema") }?.let {
      Content(mediaType, dereferenceAndRememberSchema(content.requiredField("schema")))
    }
  }
}
