package de.rk42.openapi.codegen

import de.rk42.openapi.codegen.model.ContractModel
import de.rk42.openapi.codegen.model.OperationModel
import de.rk42.openapi.codegen.model.ParameterModel
import de.rk42.openapi.codegen.model.PathModel
import io.swagger.parser.OpenAPIParser
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.parameters.Parameter
import io.swagger.v3.parser.core.models.ParseOptions
import io.swagger.v3.parser.core.models.SwaggerParseResult

/**
 * New Parser Implementation based on swagger-parser.
 */
object Parser {

  fun parse(specFile: String): ContractModel {
    val parseResult = runSwaggerParser(specFile)

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

  private fun toContract(openApi: OpenAPI): ContractModel {
    return ContractModel(toPaths(openApi.paths))
  }

  private fun toPaths(pathItemMap: Map<String, PathItem>): List<PathModel> {
    return pathItemMap.entries.map { (path, pathItem) -> toPath(path, pathItem) }
  }

  private fun toPath(path: String, pathItem: PathItem): PathModel {
    return PathModel(path, pathItem.summary ?: "", pathItem.description ?: "", toOperations(pathItem))
  }

  private fun toOperations(pathItem: PathItem): List<OperationModel> {
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

  private fun toOperation(method: String, operation: Operation, commonParameters: List<ParameterModel>): OperationModel {
    return OperationModel(
        method,
        operation.tags.nullToEmpty(),
        operation.summary.nullToEmpty(),
        operation.description.nullToEmpty(),
        operation.operationId,
        joinParameters(operation.parameters.nullToEmpty(), commonParameters)
    )
  }

  private fun joinParameters(operationParameters: List<Parameter>, pathParameters: List<ParameterModel>): List<ParameterModel> {
    val operationParametersAsMap = operationParameters.map(::toParameter).associateBy { it.name }
    val pathParametersAsMap = pathParameters.associateBy { it.name }

    return pathParametersAsMap.plus(operationParametersAsMap).values.toList()
  }

  private fun toParameter(parameter: Parameter): ParameterModel {
    return ParameterModel(parameter.name, parameter.`in`, parameter.description, parameter.required ?: false)
  }

  private fun <T> List<T>?.nullToEmpty(): List<T> = this ?: emptyList()
  
  private fun String?.nullToEmpty(): String = this ?: ""

  class ParserException(val messages: List<String>) : RuntimeException()
}