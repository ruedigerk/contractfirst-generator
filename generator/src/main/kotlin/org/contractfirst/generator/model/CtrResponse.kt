package org.contractfirst.generator.model

data class CtrResponse(
    val statusCode: ResponseStatusCode,
    val contents: List<CtrContent>
)
