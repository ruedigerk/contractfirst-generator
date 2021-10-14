package io.github.ruedigerk.contractfirst.generator.model

/**
 * Represents a parameter of a operation in the contract.
 */
data class Parameter(
    val name: String,
    val location: ParameterLocation,
    val description: String?,
    val required: Boolean,
    val schema: Schema
)