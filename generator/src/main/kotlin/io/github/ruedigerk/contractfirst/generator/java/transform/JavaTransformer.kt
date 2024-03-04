package io.github.ruedigerk.contractfirst.generator.java.transform

import io.github.ruedigerk.contractfirst.generator.java.JavaConfiguration
import io.github.ruedigerk.contractfirst.generator.java.model.JavaSpecification
import io.github.ruedigerk.contractfirst.generator.logging.Log
import io.github.ruedigerk.contractfirst.generator.model.Specification

/**
 * Transforms the parsed specification into a Java-specific specification, appropriate for code generation.
 */
class JavaTransformer(
    private val log: Log,
    private val configuration: JavaConfiguration,
) {

  fun transform(specification: Specification): JavaSpecification {
    val operationMethodNames = OperationNaming.determineMethodNames(specification.operations)
    val types = JavaSchemaToTypeTransformer(log, specification.schemas, configuration, operationMethodNames).transform()
    val operationTransformerResult = JavaOperationTransformer(specification.schemas, types, operationMethodNames).transform(specification.operations)
    val javaModelFiles = JavaSchemaToSourceTransformer(operationTransformerResult.schemasToGenerateModelFilesFor, types).transform()

    return JavaSpecification(operationTransformerResult.operationGroups, javaModelFiles)
  }
}

