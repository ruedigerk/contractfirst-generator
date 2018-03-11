package de.rk42.openapi.codegen

import de.rk42.openapi.codegen.model.CodeUnitModel
import de.rk42.openapi.codegen.model.ContractModel
import de.rk42.openapi.codegen.model.OperationModel
import de.rk42.openapi.codegen.model.PathModel

/**
 * Transforms the parsed model into a representation suitable for code generation. This includes the following transformation steps:
 * - Determine CodeUnits corresponding to operation tags or paths.
 * - Group Operations by their respective CodeUnits.
 */
object ModelTransformer {

  fun transform(contract: ContractModel): List<CodeUnitModel> {
    return groupOperationsByCodeUnits(contract.paths)
  }

  private fun groupOperationsByCodeUnits(paths: List<PathModel>): List<CodeUnitModel> = paths
      .flatMap { path -> path.operations.map { operation -> OperationAndPath(operation, path) } }
      .groupBy(::determineCodeUnitName) { it.operation }
      .map { (name, operations) -> CodeUnitModel(name, operations) }

  private fun determineCodeUnitName(operationAndPath: OperationAndPath): String {
    return operationAndPath.operation.tags.firstOrNull() ?: operationAndPath.path.path
  }

  data class OperationAndPath(val operation: OperationModel, val path: PathModel)
}