package io.github.ruedigerk.contractfirst.generator.parser

import io.github.ruedigerk.contractfirst.generator.NotSupportedException
import io.github.ruedigerk.contractfirst.generator.logging.Log
import io.github.ruedigerk.contractfirst.generator.model.*
import io.github.ruedigerk.contractfirst.generator.parser.ParserHelper.normalize
import io.github.ruedigerk.contractfirst.generator.parser.ParserHelper.nullToEmpty
import io.swagger.parser.OpenAPIParser
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.responses.ApiResponses
import io.swagger.v3.parser.core.models.ParseOptions
import io.swagger.v3.oas.models.Operation as SwaggerOperation
import io.swagger.v3.oas.models.media.Content as SwaggerContent
import io.swagger.v3.oas.models.parameters.Parameter as SwaggerParameter
import io.swagger.v3.oas.models.parameters.RequestBody as SwaggerRequestBody

/**
 * Parser implementation based on swagger-parser.
 */
class Parser(private val log: Log) {

  private lateinit var schemaResolver: SchemaResolver

  fun parse(specFilePath: String): Specification {
    val openApiSpecification = runSwaggerParser(specFilePath)
    return toContract(openApiSpecification)
  }

  private fun runSwaggerParser(specFile: String): OpenAPI {
    val parseOptions = ParseOptions().apply {
      // Replace remote/relative references with a local references, e.g. "#/components/schemas/NameOfRemoteSchema".
      isResolve = true
    }

    val result = OpenAPIParser().readLocation(specFile, null, parseOptions)

    if (result.messages.isNotEmpty()) {
      throw ParserException(result.messages)
    }

    return result.openAPI!!
  }

  private fun toContract(openApi: OpenAPI): Specification {
    schemaResolver = SchemaResolver(log, openApi.components.schemas)

    val operations = toOperations(openApi.paths)
    val schemas = schemaResolver.findAllUsedSchemas()

    return Specification(operations, schemas.allSchemas, schemas.topLevelSchemas, openApi)
  }

  private fun toOperations(pathItemMap: Map<String, PathItem>): List<Operation> {
    return pathItemMap.entries.flatMap { (path, pathItem) -> toOperations(path, pathItem) }
  }

  private fun toOperations(path: String, pathItem: PathItem): List<Operation> {
    val nameHint = NameHint(path)

    val commonParameters = pathItem.parameters.nullToEmpty().map { toParameter(it, nameHint) }

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

  private fun toOperation(
      path: String,
      method: String,
      operation: SwaggerOperation,
      commonParameters: List<Parameter>
  ): Operation {
    val nameHint = NameHint(operation.operationId)
    return Operation(
        path,
        method,
        operation.tags.nullToEmpty(),
        operation.summary.normalize(),
        operation.description.normalize(),
        operation.operationId,
        operation.requestBody?.let { toRequestBody(it, nameHint / "RequestBody") },
        joinParameters(operation.parameters.nullToEmpty(), commonParameters, nameHint),
        toResponses(operation.responses, nameHint)
    )
  }

  private fun toRequestBody(requestBody: SwaggerRequestBody, nameHint: NameHint): RequestBody = RequestBody(
      requestBody.description.normalize(),
      requestBody.required ?: false,
      toContents(requestBody.content, nameHint).map { it as? Content ?: throw ParserException("Request body without content, at $nameHint") }
  )

  private fun joinParameters(operationParameters: List<SwaggerParameter>, pathParameters: List<Parameter>, nameHint: NameHint): List<Parameter> {
    val operationParametersAsMap = operationParameters.map { toParameter(it, nameHint) }.associateBy { it.name }
    val pathParametersAsMap = pathParameters.associateBy { it.name }

    return (pathParametersAsMap + operationParametersAsMap).values.toList()
  }

  private fun toParameter(parameter: SwaggerParameter, nameHint: NameHint): Parameter {
    if (parameter.schema == null) {
      throw NotSupportedException("Parameters without schema are not supported: $parameter")
    }
    if (parameter.content != null) {
      throw NotSupportedException("Parameters with content property are not supported: $parameter")
    }

    return Parameter(
        parameter.name,
        toParameterLocation(parameter.`in`),
        parameter.description.normalize(),
        parameter.required ?: false,
        schemaResolver.parseSchema(parameter.schema, nameHint / parameter.name)
    )
  }

  private fun toParameterLocation(location: String?): ParameterLocation {
    return when (location) {
      "query" -> ParameterLocation.QUERY
      "header" -> ParameterLocation.HEADER
      "path" -> ParameterLocation.PATH
      "cookie" -> ParameterLocation.COOKIE
      else -> throw ParserException("parameter.in must be one of 'query', 'header', 'path' or 'cookie', but was '$location'")
    }
  }

  private fun toResponses(responses: ApiResponses, nameHint: NameHint): List<Response> = responses.map { (statusCode, response) ->
    val status = toStatusCode(statusCode)
    Response(status, toResponseContents(response.content, status, nameHint / statusCode))
  }

  private fun toStatusCode(statusCode: String): ResponseStatusCode = when (statusCode) {
    "default" -> DefaultStatusCode
    else -> StatusCode(statusCode.toInt())
  }

  private fun toResponseContents(content: SwaggerContent?, status: ResponseStatusCode, nameHint: NameHint): List<Content> {
    // If status code is 204, ignore content if present, as HTTP does not allow content in a 204 response
    return if (status == StatusCode(204)) {
      listOf()
    } else {
      toContents(content, nameHint)
    }
  }

  private fun toContents(content: SwaggerContent?, nameHint: NameHint): List<Content> = content?.map { (mediaType, content) ->
    Content(mediaType, schemaResolver.parseSchema(content.schema, nameHint))
  } ?: listOf()
}
