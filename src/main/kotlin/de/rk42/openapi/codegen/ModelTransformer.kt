package de.rk42.openapi.codegen

import de.rk42.openapi.codegen.model.*

/**
 * Transforms the parsed model into a representation suitable for code generation. This includes the following transformation steps:
 * - Determine CodeUnits corresponding to operation tags or paths.
 * - Group Operations by their respective CodeUnits.
 */
object ModelTransformer {

  fun transform(contract: ContractModel): List<CodeUnit> {
    return groupOperationsByCodeUnits(contract.paths)
  }

  private fun groupOperationsByCodeUnits(paths: List<PathModel>): List<CodeUnit> = paths
      .flatMap { path -> path.operations.map { operation -> OperationAndPath(operation, path) } }
      .groupBy(::determineCodeUnitName) { toCodeOperation(it) }
      .map { (name, operations) -> CodeUnit(name, operations) }

  private fun toCodeOperation(operationAndPath: OperationAndPath): CodeOperation {
    val operation = operationAndPath.operation

    return CodeOperation(
        operationAndPath.path.path,
        operation.method,
        operation.tags,
        operation.summary,
        operation.description,
        operation.operationId,
        operation.parameters
    )
  }

  private fun determineCodeUnitName(operationAndPath: OperationAndPath): String {
    return operationAndPath.operation.tags.firstOrNull() ?: operationAndPath.path.path
  }

  data class OperationAndPath(val operation: OperationModel, val path: PathModel)
}