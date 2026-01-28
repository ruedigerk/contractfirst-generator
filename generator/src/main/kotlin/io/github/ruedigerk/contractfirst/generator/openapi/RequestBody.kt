package io.github.ruedigerk.contractfirst.generator.openapi

/**
 * Represents the request body of an operation in the contract.
 */
data class RequestBody(
    val description: String?,
    val required: Boolean,
    val contents: List<Content>
)
