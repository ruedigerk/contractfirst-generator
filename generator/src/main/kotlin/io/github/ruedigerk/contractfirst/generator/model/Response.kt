package io.github.ruedigerk.contractfirst.generator.model

/**
 * Represents a response of an operation in the contract.
 */
data class Response(
    val statusCode: ResponseStatusCode,
    val contents: List<Content>
)
