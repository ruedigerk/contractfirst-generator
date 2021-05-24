package de.rk42.openapi.codegen

import de.rk42.openapi.codegen.model.contract.CtrParameter
import de.rk42.openapi.codegen.model.intermediate.ItmOperation
import de.rk42.openapi.codegen.model.intermediate.ItmSpecification
import de.rk42.openapi.codegen.model.java.JavaOperation
import de.rk42.openapi.codegen.model.java.JavaOperationGroup
import de.rk42.openapi.codegen.model.java.JavaParameter
import de.rk42.openapi.codegen.model.java.JavaSpecification

/**
 * Transforms the intermediate model into a Java-specific model for code generation.
 */
class IntermediateToJavaModelTransformer {

  fun transform(specification: ItmSpecification): JavaSpecification {
    return JavaSpecification(groupOperations(specification.operations))
  }

  private fun groupOperations(operations: List<ItmOperation>): List<JavaOperationGroup> = operations
      .groupBy { it.tags.firstOrNull() ?: DEFAULT_GROUP_NAME }
      .mapKeys { it.key.toJavaTypeIdentifier() + GROUP_NAME_SUFFIX }
      .mapValues { it.value.map(::toJavaOperation) }
      .map { (groupJavaIdentifier, operations) -> JavaOperationGroup(groupJavaIdentifier, operations) }

  private fun toJavaOperation(operation: ItmOperation): JavaOperation {
    return JavaOperation(
        operation.operationId.toJavaIdentifier(),
        operation.path,
        operation.method,
        operation.tags,
        operation.summary,
        operation.description,
        operation.parameters.map(::toJavaParameter)
    )
  }

  private fun toJavaParameter(parameter: CtrParameter): JavaParameter = JavaParameter(
      parameter.name.toJavaIdentifier(),
      parameter.name,
      parameter.location,
      parameter.description,
      parameter.required
  )

  companion object {

    private const val GROUP_NAME_SUFFIX = "Api"
    private const val DEFAULT_GROUP_NAME = "Default"

    private val INVALID_IDENTIFIER_PATTERN = Regex("[^_a-zA-Z0-9]")
    private val CONSECUTIVE_UNDERSCORES = Regex("[_]{2,}")

    private fun String.toJavaIdentifier(): String = this
        .replace(INVALID_IDENTIFIER_PATTERN, "_")
        .replace(CONSECUTIVE_UNDERSCORES, "_")
        .let { if (it.first().isDigit()) "_$it" else it }

    private fun String.toJavaTypeIdentifier(): String = this
        .toJavaIdentifier()
        .replaceFirstChar(Char::uppercase)
  }
}
