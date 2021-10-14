package io.github.ruedigerk.contractfirst.generator.model

/**
 * Represents an operation in the contract.
 */
data class Operation(
    val path: String,
    val method: String,
    val tags: List<String>,
    val summary: String?,
    val description: String?,
    val operationId: String,
    val requestBody: RequestBody?,
    val parameters: List<Parameter>,
    val responses: List<Response>
)
