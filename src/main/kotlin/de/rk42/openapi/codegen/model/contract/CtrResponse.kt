package de.rk42.openapi.codegen.model.contract

data class CtrResponse(
    val statusCode: ResponseStatusCode,
    val content: List<CtrResponseContent>
)
