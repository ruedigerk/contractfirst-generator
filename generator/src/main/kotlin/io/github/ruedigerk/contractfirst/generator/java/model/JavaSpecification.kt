package io.github.ruedigerk.contractfirst.generator.java.model

/**
 * Represents the contract.
 */
data class JavaSpecification(
    val operationGroups: List<JavaOperationGroup>,
    val modelFiles: List<JavaSourceFile>
)
