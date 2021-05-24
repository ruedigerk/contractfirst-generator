package de.rk42.openapi.codegen.model.contract

data class CtrPathItem(
    val path: String,
    val summary: String,
    val description: String,
    val operations: List<CtrOperation>
)
