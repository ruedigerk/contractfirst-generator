package org.contractfirst.generator.model

/**
 * Represents a response of an operation in the contract.
 */
data class MResponse(
    val statusCode: ResponseStatusCode,
    val contents: List<MContent>
)
