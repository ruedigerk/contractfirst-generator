package io.github.ruedigerk.contractfirst.generator.java.transform

import io.github.ruedigerk.contractfirst.generator.java.Identifiers.toJavaIdentifier
import io.github.ruedigerk.contractfirst.generator.model.Operation

/**
 * Responsible for creating Java method names for operations.
 */
object OperationNaming {
  
  /**
   * This computes the Java method names to use for operations. It uses the operationIds when they are available in the contract. Otherwise, generates names
   * from the path and method of operations.
   */
  fun determineMethodNames(operations: List<Operation>): Map<Operation.PathAndMethod, String> {
    return operations
        .associateBy { it.pathAndMethod }
        .mapValues { (_, operation) -> determineOperationName(operation) }
  }

  private fun determineOperationName(operation: Operation) = when (operation.operationId) {
    null -> (operation.method.lowercase() + " " + operation.path).toJavaIdentifier()
    else -> operation.operationId.toJavaIdentifier()
  }
  
  fun Map<Operation.PathAndMethod, String>.getJavaMethodName(pathAndMethod: Operation.PathAndMethod): String = this[pathAndMethod]
      ?: error("Trying to get operation name for unknown operation $pathAndMethod")
}