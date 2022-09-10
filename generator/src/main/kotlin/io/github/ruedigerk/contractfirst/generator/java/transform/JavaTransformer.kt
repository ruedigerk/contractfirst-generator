package io.github.ruedigerk.contractfirst.generator.java.transform

import io.github.ruedigerk.contractfirst.generator.java.Identifiers.toJavaIdentifier
import io.github.ruedigerk.contractfirst.generator.java.JavaConfiguration
import io.github.ruedigerk.contractfirst.generator.java.model.JavaSpecification
import io.github.ruedigerk.contractfirst.generator.logging.Log
import io.github.ruedigerk.contractfirst.generator.model.Operation
import io.github.ruedigerk.contractfirst.generator.model.Specification

/**
 * Transforms the parsed specification into a Java-specific specification, appropriate for code generation.
 */
class JavaTransformer(
    private val log: Log,
    private val configuration: JavaConfiguration,
) {

  fun transform(specification: Specification): JavaSpecification {
    val effectiveOperationIds = computeEffectiveOperationIds(specification.operations)

    val types = JavaSchemaToTypeTransformer(log, specification.schemas, configuration, effectiveOperationIds).transform()
    val javaModelFiles = JavaSchemaToSourceTransformer(specification.schemas, types).transform()
    val operationGroups = JavaOperationTransformer(specification.schemas, types, effectiveOperationIds).transform(specification.operations)

    return JavaSpecification(operationGroups, javaModelFiles)
  }

  /**
   * This computes the effective operationIds, as they are optional in the contract. This generates IDs from the path and method of operations missing an
   * operationId.
   */
  private fun computeEffectiveOperationIds(operations: List<Operation>): Map<List<String>, String> {
    return operations
        .associateBy { it.position.path }
        .mapValues { (_, operation) -> operation.operationId ?: deriveOperationId(operation) }
  }

  private fun deriveOperationId(operation: Operation) = (operation.method.lowercase() + " " + operation.path).toJavaIdentifier()
}

