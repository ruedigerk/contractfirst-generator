package org.contractfirst.generator.model

/**
 * Represents an operation in the contract.
 */
data class MOperation(
    val path: String,
    val method: String,
    val tags: List<String>,
    val summary: String?,
    val description: String?,
    val operationId: String,
    val requestBody: MRequestBody?,
    val parameters: List<MParameter>,
    val responses: List<MResponse>
)
