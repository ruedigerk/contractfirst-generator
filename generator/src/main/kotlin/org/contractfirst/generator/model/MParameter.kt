package org.contractfirst.generator.model

/**
 * Represents a parameter of a operation in the contract.
 */
data class MParameter(
    val name: String,
    val location: ParameterLocation,
    val description: String?,
    val required: Boolean,
    var schema: MSchema
)