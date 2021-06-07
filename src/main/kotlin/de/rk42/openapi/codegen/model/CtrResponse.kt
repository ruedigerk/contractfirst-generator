package de.rk42.openapi.codegen.model

data class CtrResponse(
    val statusCode: ResponseStatusCode,
    val contents: List<CtrContent>
)
