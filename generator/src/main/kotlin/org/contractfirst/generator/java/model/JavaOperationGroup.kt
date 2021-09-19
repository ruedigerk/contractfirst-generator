package org.contractfirst.generator.java.model

/**
 * Represents a group of operations of the contract.
 */
data class JavaOperationGroup(
    val javaIdentifier: String,
    val operations: List<JavaOperation>
)
