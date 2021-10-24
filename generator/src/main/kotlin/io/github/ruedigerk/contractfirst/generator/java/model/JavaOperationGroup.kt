package io.github.ruedigerk.contractfirst.generator.java.model

/**
 * Represents a group of operations of the contract.
 */
data class JavaOperationGroup(
    val javaIdentifier: String,
    val operations: List<JavaOperation>,
    val originalTag: String
)
