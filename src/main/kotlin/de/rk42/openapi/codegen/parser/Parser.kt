package de.rk42.openapi.codegen.parser

import de.rk42.openapi.codegen.NotSupportedException
import de.rk42.openapi.codegen.model.CtrContent
import de.rk42.openapi.codegen.model.CtrOperation
import de.rk42.openapi.codegen.model.CtrParameter
import de.rk42.openapi.codegen.model.CtrRequestBody
import de.rk42.openapi.codegen.model.CtrResponse
import de.rk42.openapi.codegen.model.CtrSpecification
import de.rk42.openapi.codegen.model.DefaultStatusCode
import de.rk42.openapi.codegen.model.NameHint
import de.rk42.openapi.codegen.model.ParameterLocation
import de.rk42.openapi.codegen.model.ResponseStatusCode
import de.rk42.openapi.codegen.model.StatusCode
import de.rk42.openapi.codegen.parser.ParserHelper.normalize
import de.rk42.openapi.codegen.parser.ParserHelper.nullToEmpty
import io.swagger.parser.OpenAPIParser
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.parameters.Parameter
import io.swagger.v3.oas.models.parameters.RequestBody
import io.swagger.v3.oas.models.responses.ApiResponses
import io.swagger.v3.parser.core.models.ParseOptions

/**
 * Parser implementation based on swagger-parser.
 */
class Parser {

  private lateinit var schemaResolver: SchemaResolver

  fun parse(specFilePath: String): CtrSpecification {
    val openApiSpecification = runSwaggerParser(specFilePath)
    return toContract(openApiSpecification)
  }

  private fun runSwaggerParser(specFile: String): OpenAPI {
    val parseOptions = ParseOptions().apply {
      // Replace remote/relative references with a local references, e.g. "#/components/schemas/NameOfRemoteSchema".
      isResolve = true
      // Move all inline schemas (e.g. a response schema) to the components/schemas section and replace them with a reference.
      isFlatten = true
      // Not really clear what this does, as it is undocumented. TODO: Remove?
      isFlattenComposedSchemas = true
    }

    val result = OpenAPIParser().readLocation(specFile, null, parseOptions)

    if (result.messages.isNotEmpty()) {
      throw ParserException(result.messages)
    }

    return result.openAPI!!
  }

  private fun toContract(openApi: OpenAPI): CtrSpecification {
    schemaResolver = SchemaResolver(openApi.components.schemas)

    val operations = toOperations(openApi.paths)
    val referencedSchemas = schemaResolver.determineAndResolveReferencedSchemas()

    return CtrSpecification(operations, referencedSchemas)
  }

  private fun toOperations(pathItemMap: Map<String, PathItem>): List<CtrOperation> {
    return pathItemMap.entries.flatMap { (path, pathItem) -> toOperations(path, pathItem) }
  }

  private fun toOperations(path: String, pathItem: PathItem): List<CtrOperation> {
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
      commonParameters: List<CtrParameter>
  ): CtrOperation {
    val nameHint = NameHint(operation.operationId)
    return CtrOperation(
        path,
        method,
        operation.tags.nullToEmpty(),
        operation.summary.normalize(),
        operation.description.normalize(),
        operation.operationId,
        operation.requestBody?.let { toRequestBody(it, nameHint / "Request") },
        joinParameters(operation.parameters.nullToEmpty(), commonParameters, nameHint),
        toResponses(operation.responses, nameHint)
    )
  }

  private fun toRequestBody(requestBody: RequestBody, nameHint: NameHint): CtrRequestBody = CtrRequestBody(
      requestBody.description.normalize(),
      requestBody.required ?: false,
      toContents(requestBody.content, nameHint)
  )

  private fun joinParameters(operationParameters: List<Parameter>, pathParameters: List<CtrParameter>, nameHint: NameHint): List<CtrParameter> {
    val operationParametersAsMap = operationParameters.map { toParameter(it, nameHint) }.associateBy { it.name }
    val pathParametersAsMap = pathParameters.associateBy { it.name }

    return pathParametersAsMap.plus(operationParametersAsMap).values.toList()
  }

  private fun toParameter(parameter: Parameter, nameHint: NameHint): CtrParameter {
    if (parameter.schema == null) {
      throw NotSupportedException("Parameters without schema are not supported: $parameter")
    }
    if (parameter.content != null) {
      throw NotSupportedException("Parameters with content property are not supported: $parameter")
    }

    return CtrParameter(
        parameter.name,
        toParameterLocation(parameter.`in`),
        parameter.description.normalize(),
        parameter.required ?: false,
        schemaResolver.resolveSchema(parameter.schema, nameHint / parameter.name)
    )
  }

  private fun toParameterLocation(location: String?): ParameterLocation {
    return when (location) {
      "query"  -> ParameterLocation.QUERY
      "header" -> ParameterLocation.HEADER
      "path"   -> ParameterLocation.PATH
      "cookie" -> ParameterLocation.COOKIE
      else     -> throw ParserException("parameter.in must be one of 'query', 'header', 'path' or 'cookie', but was '$location'")
    }
  }

  private fun toResponses(responses: ApiResponses, nameHint: NameHint): List<CtrResponse> = responses.map { (statusCode, response) ->
    CtrResponse(toStatusCode(statusCode), toContents(response.content ?: Content(), nameHint / statusCode))
  }

  private fun toStatusCode(statusCode: String): ResponseStatusCode = when (statusCode) {
    "default" -> DefaultStatusCode
    else      -> StatusCode(statusCode.toInt())
  }

  private fun toContents(content: Content, nameHint: NameHint): List<CtrContent> = content.map { (mediaType, content) ->
    CtrContent(mediaType, schemaResolver.resolveSchema(content.schema, nameHint))
  }
}
