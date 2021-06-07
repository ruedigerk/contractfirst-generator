package de.rk42.openapi.codegen.model.contract

data class CtrOperation(
    val path: String,
    val method: String,
    val tags: List<String>,
    val summary: String?,
    val description: String?,
    val operationId: String,
    val requestBody: CtrRequestBody?,
    val parameters: List<CtrParameter>,
    val responses: List<CtrResponse>
)
