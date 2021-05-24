package de.rk42.openapi.codegen.model.intermediate

import de.rk42.openapi.codegen.model.contract.CtrParameter

data class ItmOperation(
    val path: String,
    val method: String,
    val tags: List<String>,
    val summary: String,
    val description: String,
    val operationId: String,
    val parameters: List<CtrParameter>
)