package io.github.ruedigerk.contractfirst.generator.java.model

import io.github.ruedigerk.contractfirst.generator.model.ResponseStatusCode

/**
 * Represents a response of an operation of the contract.
 */
data class JavaResponse(
    val statusCode: ResponseStatusCode,
    val contents: List<JavaContent>
)