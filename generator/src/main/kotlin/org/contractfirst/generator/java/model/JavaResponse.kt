package org.contractfirst.generator.java.model

import org.contractfirst.generator.model.ResponseStatusCode

/**
 * Represents a response of an operation of the contract.
 */
data class JavaResponse(
    val statusCode: ResponseStatusCode,
    val contents: List<JavaContent>
)