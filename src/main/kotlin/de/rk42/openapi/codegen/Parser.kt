package de.rk42.openapi.codegen

import de.rk42.openapi.codegen.model.ParameterLocation
import de.rk42.openapi.codegen.model.contract.CtrOperation
import de.rk42.openapi.codegen.model.contract.CtrParameter
import de.rk42.openapi.codegen.model.contract.CtrPathItem
import de.rk42.openapi.codegen.model.contract.CtrSpecification
import io.swagger.parser.OpenAPIParser
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.parameters.Parameter
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
    return CtrSpecification(toPaths(openApi.paths))
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

  private fun toOperation(method: String, operation: Operation, commonParameters: List<CtrParameter>): CtrOperation {
    return CtrOperation(
        method,
        operation.tags.nullToEmpty(),
        operation.summary.nullToEmpty(),
        operation.description.nullToEmpty(),
        operation.operationId,
        joinParameters(operation.parameters.nullToEmpty(), commonParameters)
    )
  }

  private fun joinParameters(operationParameters: List<Parameter>, pathParameters: List<CtrParameter>): List<CtrParameter> {
    val operationParametersAsMap = operationParameters.map(::toParameter).associateBy { it.name }
    val pathParametersAsMap = pathParameters.associateBy { it.name }

    return pathParametersAsMap.plus(operationParametersAsMap).values.toList()
  }

  private fun toParameter(parameter: Parameter): CtrParameter {
    return CtrParameter(parameter.name, toParameterLocation(parameter.`in`), parameter.description, parameter.required ?: false)
  }

  private fun toParameterLocation(location: String?): ParameterLocation {
    return when (location) {
      "query" -> ParameterLocation.QUERY
      "header" -> ParameterLocation.HEADER
      "path" -> ParameterLocation.PATH
      "cookie" -> ParameterLocation.COOKIE
      else -> throw InvalidContractException("parameter.in must be one of query, header, path, cookie, but was $location")
    }
  }

  private fun <T> List<T>?.nullToEmpty(): List<T> = this ?: emptyList()

  private fun String?.nullToEmpty(): String = this ?: ""

  class ParserException(val messages: List<String>) : RuntimeException()
}