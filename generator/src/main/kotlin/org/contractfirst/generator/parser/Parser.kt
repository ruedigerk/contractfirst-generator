package org.contractfirst.generator.parser

import io.swagger.parser.OpenAPIParser
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.parameters.Parameter
import io.swagger.v3.oas.models.parameters.RequestBody
import io.swagger.v3.oas.models.responses.ApiResponses
import io.swagger.v3.parser.core.models.ParseOptions
import org.contractfirst.generator.NotSupportedException
import org.contractfirst.generator.logging.Log
import org.contractfirst.generator.model.*
import org.contractfirst.generator.parser.ParserHelper.normalize
import org.contractfirst.generator.parser.ParserHelper.nullToEmpty

/**
 * Parser implementation based on swagger-parser.
 */
class Parser(private val log: Log) {

  private lateinit var schemaResolver: SchemaResolver

  fun parse(specFilePath: String): MSpecification {
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

  private fun toContract(openApi: OpenAPI): MSpecification {
    schemaResolver = SchemaResolver(log, openApi.components.schemas)

    val operations = toOperations(openApi.paths)
    val referencedSchemas = schemaResolver.determineAndResolveReferencedSchemas()

    return MSpecification(operations, referencedSchemas, openApi)
  }

  private fun toOperations(pathItemMap: Map<String, PathItem>): List<MOperation> {
    return pathItemMap.entries.flatMap { (path, pathItem) -> toOperations(path, pathItem) }
  }

  private fun toOperations(path: String, pathItem: PathItem): List<MOperation> {
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
      operation: Operation,
      commonParameters: List<MParameter>
  ): MOperation {
    val nameHint = NameHint(operation.operationId)
    return MOperation(
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

  private fun toRequestBody(requestBody: RequestBody, nameHint: NameHint): MRequestBody = MRequestBody(
      requestBody.description.normalize(),
      requestBody.required ?: false,
      toContents(requestBody.content, nameHint).map { it as? MContent ?: throw ParserException("Request body without content, at $nameHint") }
  )

  private fun joinParameters(operationParameters: List<Parameter>, pathParameters: List<MParameter>, nameHint: NameHint): List<MParameter> {
    val operationParametersAsMap = operationParameters.map { toParameter(it, nameHint) }.associateBy { it.name }
    val pathParametersAsMap = pathParameters.associateBy { it.name }

    return (pathParametersAsMap + operationParametersAsMap).values.toList()
  }

  private fun toParameter(parameter: Parameter, nameHint: NameHint): MParameter {
    if (parameter.schema == null) {
      throw NotSupportedException("Parameters without schema are not supported: $parameter")
    }
    if (parameter.content != null) {
      throw NotSupportedException("Parameters with content property are not supported: $parameter")
    }

    return MParameter(
        parameter.name,
        toParameterLocation(parameter.`in`),
        parameter.description.normalize(),
        parameter.required ?: false,
        schemaResolver.resolveSchema(parameter.schema, nameHint / parameter.name)
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

  private fun toResponses(responses: ApiResponses, nameHint: NameHint): List<MResponse> = responses.map { (statusCode, response) ->
    MResponse(toStatusCode(statusCode), toContents(response.content, nameHint / statusCode))
  }

  private fun toStatusCode(statusCode: String): ResponseStatusCode = when (statusCode) {
    "default" -> DefaultStatusCode
    else -> StatusCode(statusCode.toInt())
  }

  private fun toContents(content: Content?, nameHint: NameHint): List<MContent> = content?.map { (mediaType, content) ->
    MContent(mediaType, schemaResolver.resolveSchema(content.schema, nameHint))
  } ?: listOf()
}
